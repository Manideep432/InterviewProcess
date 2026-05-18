import React, { useState, useEffect } from 'react';
import mfaService from '../services/mfaService';
import './MfaSetup.css';

/**
 * MFA Setup Component - Enable/Disable Multi-Factor Authentication
 * 
 * @author Bob
 */
const MfaSetup = () => {
  const [mfaEnabled, setMfaEnabled] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showSetup, setShowSetup] = useState(false);
  const [showDisable, setShowDisable] = useState(false);
  
  // Setup state
  const [qrCode, setQrCode] = useState('');
  const [secret, setSecret] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  
  // Disable state
  const [disableCode, setDisableCode] = useState('');

  useEffect(() => {
    checkMfaStatus();
  }, []);

  const checkMfaStatus = async () => {
    try {
      const status = await mfaService.getMfaStatus();
      setMfaEnabled(status.mfaEnabled);
    } catch (err) {
      console.error('Failed to check MFA status:', err);
    }
  };

  const handleSetupMfa = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await mfaService.setupMfa();
      setQrCode(response.qrCodeImage);
      setSecret(response.secret);
      setShowSetup(true);
      setSuccess('QR code generated! Scan it with your authenticator app.');
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnableMfa = async (e) => {
    e.preventDefault();
    
    if (!verificationCode || verificationCode.length !== 6) {
      setError('Please enter a valid 6-digit code');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await mfaService.enableMfa(secret, parseInt(verificationCode));
      setSuccess('✅ MFA enabled successfully!');
      setMfaEnabled(true);
      setShowSetup(false);
      setVerificationCode('');
      setQrCode('');
      setSecret('');
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDisableMfa = async (e) => {
    e.preventDefault();
    
    if (!disableCode || disableCode.length !== 6) {
      setError('Please enter a valid 6-digit code');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await mfaService.disableMfa(parseInt(disableCode));
      setSuccess('✅ MFA disabled successfully!');
      setMfaEnabled(false);
      setShowDisable(false);
      setDisableCode('');
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelSetup = () => {
    setShowSetup(false);
    setVerificationCode('');
    setQrCode('');
    setSecret('');
    setError('');
    setSuccess('');
  };

  const handleCancelDisable = () => {
    setShowDisable(false);
    setDisableCode('');
    setError('');
    setSuccess('');
  };

  return (
    <div className="mfa-setup-container">
      <div className="mfa-setup-card">
        <div className="mfa-header">
          <h3>🔐 Two-Factor Authentication (2FA)</h3>
          <div className={`mfa-status ${mfaEnabled ? 'enabled' : 'disabled'}`}>
            {mfaEnabled ? '✅ Enabled' : '❌ Disabled'}
          </div>
        </div>

        <p className="mfa-description">
          Add an extra layer of security to your account by enabling two-factor authentication.
          You'll need to enter a code from your authenticator app when logging in.
        </p>

        {error && (
          <div className="error-message">
            ❌ {error}
          </div>
        )}

        {success && (
          <div className="success-message">
            {success}
          </div>
        )}

        {!showSetup && !showDisable && (
          <div className="mfa-actions">
            {!mfaEnabled ? (
              <button
                className="mfa-button enable"
                onClick={handleSetupMfa}
                disabled={loading}
              >
                {loading ? '⏳ Loading...' : '🔒 Enable 2FA'}
              </button>
            ) : (
              <button
                className="mfa-button disable"
                onClick={() => setShowDisable(true)}
                disabled={loading}
              >
                🔓 Disable 2FA
              </button>
            )}
          </div>
        )}

        {showSetup && (
          <div className="mfa-setup-form">
            <div className="setup-steps">
              <h4>📱 Setup Instructions:</h4>
              <ol>
                <li>Download an authenticator app (Google Authenticator, Authy, etc.)</li>
                <li>Scan the QR code below with your app</li>
                <li>Enter the 6-digit code from your app to verify</li>
              </ol>
            </div>

            {qrCode && (
              <div className="qr-code-section">
                <img src={qrCode} alt="MFA QR Code" className="qr-code-image" />
                <div className="secret-key">
                  <p><strong>Manual Entry Key:</strong></p>
                  <code>{secret}</code>
                  <small>Use this if you can't scan the QR code</small>
                </div>
              </div>
            )}

            <form onSubmit={handleEnableMfa} className="verification-form">
              <div className="form-group">
                <label htmlFor="verificationCode">Verification Code</label>
                <input
                  type="text"
                  id="verificationCode"
                  value={verificationCode}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\D/g, '').slice(0, 6);
                    setVerificationCode(value);
                    setError('');
                  }}
                  placeholder="000000"
                  maxLength="6"
                  disabled={loading}
                  className="code-input"
                  autoComplete="off"
                />
                <small>Enter the 6-digit code from your authenticator app</small>
              </div>

              <div className="form-actions">
                <button
                  type="submit"
                  className="mfa-button verify"
                  disabled={loading || verificationCode.length !== 6}
                >
                  {loading ? '⏳ Verifying...' : '✅ Verify & Enable'}
                </button>
                <button
                  type="button"
                  className="mfa-button cancel"
                  onClick={handleCancelSetup}
                  disabled={loading}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {showDisable && (
          <div className="mfa-disable-form">
            <div className="warning-message">
              ⚠️ <strong>Warning:</strong> Disabling 2FA will make your account less secure.
            </div>

            <form onSubmit={handleDisableMfa} className="verification-form">
              <div className="form-group">
                <label htmlFor="disableCode">Verification Code</label>
                <input
                  type="text"
                  id="disableCode"
                  value={disableCode}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\D/g, '').slice(0, 6);
                    setDisableCode(value);
                    setError('');
                  }}
                  placeholder="000000"
                  maxLength="6"
                  disabled={loading}
                  className="code-input"
                  autoComplete="off"
                />
                <small>Enter your current 6-digit code to confirm</small>
              </div>

              <div className="form-actions">
                <button
                  type="submit"
                  className="mfa-button disable-confirm"
                  disabled={loading || disableCode.length !== 6}
                >
                  {loading ? '⏳ Processing...' : '🔓 Confirm Disable'}
                </button>
                <button
                  type="button"
                  className="mfa-button cancel"
                  onClick={handleCancelDisable}
                  disabled={loading}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        <div className="mfa-info">
          <h4>ℹ️ About Two-Factor Authentication</h4>
          <ul>
            <li>Adds an extra layer of security to your account</li>
            <li>Requires both password and authenticator code to login</li>
            <li>Works with Google Authenticator, Authy, Microsoft Authenticator, etc.</li>
            <li>Codes refresh every 30 seconds</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default MfaSetup;

// Made with Bob