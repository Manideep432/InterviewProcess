import React, { useState } from 'react';
import './TechnicalAssessmentForm.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';

/**
 * TechnicalAssessmentForm Component
 * Comprehensive Technical Interview Assessment Form for AWS Cloud Full Stack
 */
const TechnicalAssessmentForm = ({ interview, onClose, onSubmitSuccess }) => {
  const [formData, setFormData] = useState({
    interviewId: interview.id,
    candidateName: interview.candidateName || '',
    source: 'External Hire - US Insurance Cluster',
    yearsOfExperience: '',
    yearsOfExperienceInTech: '',
    evaluationType: 'VENDOR',
    evaluatorNames: '',
    evaluationDate: new Date().toISOString().split('T')[0],
    jobRoleSpecification: 'AWS Cloud Full Stack',
    jobDescription: '',
    accountName: 'US Insurance Cluster',
    jobLevel: 'SSE',
    
    // Ratings
    communicationRating: '',
    awsNativeServicesRating: '',
    awsIntegrationServicesRating: '',
    awsComputeServicesRating: '',
    programmingLanguageRating: '',
    awsDevOpsServicesRating: '',
    awsStorageRating: '',
    agileScrumRating: '',
    awsCliRating: '',
    deploymentManagementRating: '',
    containerOrchestrationRating: '',
    microservicesDesignPatternsRating: '',
    microservicesCommunicationRating: '',
    disasterRecoveryRating: '',
    containerizationRating: '',
    iacRating: '',
    frontendStackRating: '',
    htmlCssRating: '',
    springCloudAwsRating: '',
    sqlTuningRating: '',
    setupPackagingRating: '',
    
    certifications: '',
    
    // Architecting (for TL/ML/Architect)
    estimationRating: '',
    architectureRating: '',
    solutioningRating: '',
    deliveryMethodologiesRating: '',
    operationsRating: '',
    customerHandlingRating: '',
    otherManagementSkillsRating: '',
    
    // Notes
    awsNativeServicesNotes: '',
    technicalSkillsNotes: '',
    
    // Overall
    overallRating: '',
    toolRecommendation: 'Selected',
    techPanelRecommendation: 'Selected',
    overallFeedback: '',
    suitabilityForRequirement: 'Selected',
    improvementFocusArea: '',
    declarationAccepted: false,
    evaluatorSignature: ''
  });

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [currentSection, setCurrentSection] = useState(1);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const calculateOverallRating = () => {
    const mandatoryRatings = [
      formData.communicationRating,
      formData.awsNativeServicesRating,
      formData.awsIntegrationServicesRating,
      formData.awsComputeServicesRating,
      formData.programmingLanguageRating,
      formData.awsDevOpsServicesRating,
      formData.awsStorageRating,
      formData.agileScrumRating,
      formData.awsCliRating,
      formData.deploymentManagementRating,
      formData.containerOrchestrationRating,
      formData.microservicesDesignPatternsRating,
      formData.microservicesCommunicationRating,
      formData.disasterRecoveryRating,
      formData.containerizationRating,
      formData.iacRating,
      formData.frontendStackRating,
      formData.htmlCssRating
    ].filter(r => r !== '' && r !== null);

    if (mandatoryRatings.length === 0) return 0;
    
    const sum = mandatoryRatings.reduce((acc, val) => acc + parseFloat(val), 0);
    return (sum / mandatoryRatings.length).toFixed(2);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.declarationAccepted) {
      setError('Please accept the declaration before submitting');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      // Calculate overall rating
      const overallRating = calculateOverallRating();
      
      const submitData = {
        ...formData,
        overallRating: parseFloat(overallRating),
        yearsOfExperience: parseFloat(formData.yearsOfExperience),
        yearsOfExperienceInTech: parseFloat(formData.yearsOfExperienceInTech),
        communicationRating: formData.communicationRating ? parseFloat(formData.communicationRating) : null,
        awsNativeServicesRating: formData.awsNativeServicesRating ? parseFloat(formData.awsNativeServicesRating) : null,
        awsIntegrationServicesRating: formData.awsIntegrationServicesRating ? parseFloat(formData.awsIntegrationServicesRating) : null,
        awsComputeServicesRating: formData.awsComputeServicesRating ? parseFloat(formData.awsComputeServicesRating) : null,
        programmingLanguageRating: formData.programmingLanguageRating ? parseFloat(formData.programmingLanguageRating) : null,
        awsDevOpsServicesRating: formData.awsDevOpsServicesRating ? parseFloat(formData.awsDevOpsServicesRating) : null,
        awsStorageRating: formData.awsStorageRating ? parseFloat(formData.awsStorageRating) : null,
        agileScrumRating: formData.agileScrumRating ? parseFloat(formData.agileScrumRating) : null,
        awsCliRating: formData.awsCliRating ? parseFloat(formData.awsCliRating) : null,
        deploymentManagementRating: formData.deploymentManagementRating ? parseFloat(formData.deploymentManagementRating) : null,
        containerOrchestrationRating: formData.containerOrchestrationRating ? parseFloat(formData.containerOrchestrationRating) : null,
        microservicesDesignPatternsRating: formData.microservicesDesignPatternsRating ? parseFloat(formData.microservicesDesignPatternsRating) : null,
        microservicesCommunicationRating: formData.microservicesCommunicationRating ? parseFloat(formData.microservicesCommunicationRating) : null,
        disasterRecoveryRating: formData.disasterRecoveryRating ? parseFloat(formData.disasterRecoveryRating) : null,
        containerizationRating: formData.containerizationRating ? parseFloat(formData.containerizationRating) : null,
        iacRating: formData.iacRating ? parseFloat(formData.iacRating) : null,
        frontendStackRating: formData.frontendStackRating ? parseFloat(formData.frontendStackRating) : null,
        htmlCssRating: formData.htmlCssRating ? parseFloat(formData.htmlCssRating) : null,
        springCloudAwsRating: formData.springCloudAwsRating ? parseFloat(formData.springCloudAwsRating) : null,
        sqlTuningRating: formData.sqlTuningRating ? parseFloat(formData.sqlTuningRating) : null,
        setupPackagingRating: formData.setupPackagingRating ? parseFloat(formData.setupPackagingRating) : null,
        estimationRating: formData.estimationRating ? parseFloat(formData.estimationRating) : null,
        architectureRating: formData.architectureRating ? parseFloat(formData.architectureRating) : null,
        solutioningRating: formData.solutioningRating ? parseFloat(formData.solutioningRating) : null,
        deliveryMethodologiesRating: formData.deliveryMethodologiesRating ? parseFloat(formData.deliveryMethodologiesRating) : null,
        operationsRating: formData.operationsRating ? parseFloat(formData.operationsRating) : null,
        customerHandlingRating: formData.customerHandlingRating ? parseFloat(formData.customerHandlingRating) : null,
        otherManagementSkillsRating: formData.otherManagementSkillsRating ? parseFloat(formData.otherManagementSkillsRating) : null
      };

      const token = localStorage.getItem('token');
      const response = await fetch(`${API_URL}/api/interview-feedback/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(submitData)
      });

      const data = await response.json();
      
      if (data.success) {
        alert('✅ Technical Feedback Form submitted successfully!\n\n' +
              '📧 The feedback has been sent to HR via email with PDF attachment.\n' +
              '📊 HR can now view this feedback in their dashboard.');
        onSubmitSuccess();
        onClose();
      } else {
        setError(data.message || 'Failed to submit assessment form');
      }
    } catch (err) {
      setError('Error submitting assessment form: ' + err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const renderSection1 = () => (
    <div className="form-section">
      <h3>📋 Basic Information</h3>
      
      <div className="form-row">
        <div className="form-group">
          <label>Candidate Name *</label>
          <input
            type="text"
            name="candidateName"
            value={formData.candidateName}
            onChange={handleChange}
            required
            readOnly
          />
        </div>
        <div className="form-group">
          <label>Source *</label>
          <select name="source" value={formData.source} onChange={handleChange} required>
            <option value="External Hire - US Insurance Cluster">External Hire - US Insurance Cluster</option>
            <option value="Internal Transfer">Internal Transfer</option>
            <option value="Campus Hire">Campus Hire</option>
          </select>
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Years of Experience *</label>
          <input
            type="number"
            step="0.5"
            name="yearsOfExperience"
            value={formData.yearsOfExperience}
            onChange={handleChange}
            required
            placeholder="e.g., 6.5"
          />
        </div>
        <div className="form-group">
          <label>Years of Experience in AWS *</label>
          <input
            type="number"
            step="0.5"
            name="yearsOfExperienceInTech"
            value={formData.yearsOfExperienceInTech}
            onChange={handleChange}
            required
            placeholder="e.g., 3.0"
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Evaluation Type *</label>
          <select name="evaluationType" value={formData.evaluationType} onChange={handleChange} required>
            <option value="VENDOR">VENDOR</option>
            <option value="SELF">SELF</option>
            <option value="IBM REFERAL">IBM REFERAL</option>
            <option value="OTHERS">OTHERS</option>
          </select>
        </div>
        <div className="form-group">
          <label>Evaluator Names & ID *</label>
          <input
            type="text"
            name="evaluatorNames"
            value={formData.evaluatorNames}
            onChange={handleChange}
            required
            placeholder="e.g., John Doe (ID: 12345)"
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Evaluation Date *</label>
          <input
            type="date"
            name="evaluationDate"
            value={formData.evaluationDate}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Job Level *</label>
          <select name="jobLevel" value={formData.jobLevel} onChange={handleChange} required>
            <option value="SE">SE - Systems Engineer</option>
            <option value="SSE">SSE - Senior Systems Engineer</option>
            <option value="TL">TL - Tech Lead</option>
            <option value="ML">ML - Module Lead</option>
            <option value="Architect">Architect</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label>Job Role Specification (JRS) *</label>
        <input
          type="text"
          name="jobRoleSpecification"
          value={formData.jobRoleSpecification}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-group">
        <label>Job Description (JD)</label>
        <textarea
          name="jobDescription"
          value={formData.jobDescription}
          onChange={handleChange}
          rows="3"
          placeholder="e.g., Java + SpringBoot + Microservices and AWS and React Js"
        />
      </div>

      <div className="form-group">
        <label>Account Name / Hire Ahead *</label>
        <input
          type="text"
          name="accountName"
          value={formData.accountName}
          onChange={handleChange}
          required
        />
      </div>
    </div>
  );

  const renderSection2 = () => (
    <div className="form-section">
      <h3>💬 A. Soft Skills</h3>
      <p className="rating-info">Rating Scale: 0-10 (0 = No Skill, 1-3 = Acquired, 4-8 = Applied, 9-10 = Mastered)</p>
      
      <div className="rating-row">
        <label>Communication - grammar, correctness, pronunciation, clarity *</label>
        <input
          type="number"
          step="0.5"
          min="0"
          max="10"
          name="communicationRating"
          value={formData.communicationRating}
          onChange={handleChange}
          required
          placeholder="0-10"
        />
        <span className="skill-type mandatory">M</span>
      </div>
    </div>
  );

  const renderSection3 = () => (
    <div className="form-section">
      <h3>💻 B. Technical Skills</h3>
      <p className="rating-info">M = Mandatory, D = Desirable | Rating: 0-10</p>
      
      {[
        { name: 'awsNativeServicesRating', label: 'AWS Native Services', type: 'M' },
        { name: 'awsIntegrationServicesRating', label: 'AWS Integration Services (EventBus, SQS, SNS, etc.)', type: 'M' },
        { name: 'awsComputeServicesRating', label: 'AWS Compute Services (Lambda, EC2)', type: 'M' },
        { name: 'programmingLanguageRating', label: '.NET / Java / Python / Node.JS', type: 'M' },
        { name: 'awsDevOpsServicesRating', label: 'AWS DevOps Services', type: 'M' },
        { name: 'awsStorageRating', label: 'AWS Storage (RDS/Aurora/DynamoDB/S3)', type: 'M' },
        { name: 'agileScrumRating', label: 'Agile/Scrum', type: 'M' },
        { name: 'awsCliRating', label: 'AWS CLI', type: 'M' },
        { name: 'deploymentManagementRating', label: 'Software Deployment/Configuration/Release Management', type: 'M' },
        { name: 'containerOrchestrationRating', label: 'AWS EKS/ECS or OpenShift/Kubernetes', type: 'M' },
        { name: 'microservicesDesignPatternsRating', label: 'Microservices Design Patterns', type: 'M' },
        { name: 'microservicesCommunicationRating', label: 'Microservices Communications', type: 'M' },
        { name: 'disasterRecoveryRating', label: 'AutoScaling, Disaster Recovery, Backup Technologies', type: 'M' },
        { name: 'containerizationRating', label: 'Container and Containerization (Docker/Podman)', type: 'M' },
        { name: 'iacRating', label: 'IaaC (Terraform / CloudFormation)', type: 'M' },
        { name: 'frontendStackRating', label: 'FrontEnd Stack: Angular/React/Node/Vue/etc', type: 'M' },
        { name: 'htmlCssRating', label: 'FrontEnd Stack: HTML/CSS, Responsive Design', type: 'M' },
        { name: 'springCloudAwsRating', label: 'SpringCloud for AWS', type: 'D' },
        { name: 'sqlTuningRating', label: 'SQL Tuning and DB Related Operations', type: 'D' },
        { name: 'setupPackagingRating', label: 'Setup, Packaging and Deployment', type: 'D' }
      ].map(skill => (
        <div key={skill.name} className="rating-row">
          <label>{skill.label} {skill.type === 'M' && '*'}</label>
          <input
            type="number"
            step="0.5"
            min="0"
            max="10"
            name={skill.name}
            value={formData[skill.name]}
            onChange={handleChange}
            required={skill.type === 'M'}
            placeholder="0-10"
          />
          <span className={`skill-type ${skill.type === 'M' ? 'mandatory' : 'desirable'}`}>{skill.type}</span>
        </div>
      ))}

      <div className="form-group">
        <label>Panel Notes - AWS Native Services</label>
        <textarea
          name="awsNativeServicesNotes"
          value={formData.awsNativeServicesNotes}
          onChange={handleChange}
          rows="4"
          placeholder="e.g., Terraform, AWS EKS, AWS ECS, AWS Route53, AWS S3, AWS DynamoDB, AWS Lambda, AWS RDS, AWS CloudWatch, Kubernetes etc"
        />
      </div>

      <div className="form-group">
        <label>Certifications</label>
        <textarea
          name="certifications"
          value={formData.certifications}
          onChange={handleChange}
          rows="3"
          placeholder="List any AWS or relevant certifications"
        />
      </div>
    </div>
  );

  const renderSection4 = () => {
    if (!['TL', 'ML', 'Architect'].includes(formData.jobLevel)) {
      return null;
    }

    return (
      <div className="form-section">
        <h3>🏗️ D. Architecting and Solutioning</h3>
        <p className="rating-info">For TL/ML/Architect roles only</p>
        
        {[
          { name: 'estimationRating', label: 'Functional Point / WBS Estimation', type: 'M' },
          { name: 'architectureRating', label: 'Architecture (HLD, LLD, etc.)', type: 'M' },
          { name: 'solutioningRating', label: 'Solutioning / RFP Support', type: 'M' },
          { name: 'deliveryMethodologiesRating', label: 'Delivery - Methodologies, Processes, Quality', type: 'D' },
          { name: 'operationsRating', label: 'Operations - Infrastructure, Support, Recruitment', type: 'D' },
          { name: 'customerHandlingRating', label: 'Customer Handling - Communication, Account Management', type: 'D' },
          { name: 'otherManagementSkillsRating', label: 'Other Management Skills', type: 'D' }
        ].map(skill => (
          <div key={skill.name} className="rating-row">
            <label>{skill.label} {skill.type === 'M' && '*'}</label>
            <input
              type="number"
              step="0.5"
              min="0"
              max="10"
              name={skill.name}
              value={formData[skill.name]}
              onChange={handleChange}
              required={skill.type === 'M'}
              placeholder="0-10"
            />
            <span className={`skill-type ${skill.type === 'M' ? 'mandatory' : 'desirable'}`}>{skill.type}</span>
          </div>
        ))}
      </div>
    );
  };

  const renderSection5 = () => (
    <div className="form-section">
      <h3>📊 E. Overall Rating & Recommendations</h3>
      
      <div className="overall-rating-display">
        <label>Overall Rating (Calculated from Mandatory Skills):</label>
        <div className="rating-value">{calculateOverallRating()}</div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label>Tool Recommendation *</label>
          <select name="toolRecommendation" value={formData.toolRecommendation} onChange={handleChange} required>
            <option value="Selected">Selected</option>
            <option value="Rejected">Rejected</option>
          </select>
        </div>
        <div className="form-group">
          <label>Tech Panel Recommendation *</label>
          <select name="techPanelRecommendation" value={formData.techPanelRecommendation} onChange={handleChange} required>
            <option value="Selected">Selected</option>
            <option value="Rejected">Rejected</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label>Overall Feedback on this Candidate *</label>
        <textarea
          name="overallFeedback"
          value={formData.overallFeedback}
          onChange={handleChange}
          required
          rows="6"
          placeholder="Provide detailed feedback about the candidate's performance, skills, strengths, and areas of improvement..."
        />
      </div>

      <div className="form-group">
        <label>How Suitable for this Requirement *</label>
        <select name="suitabilityForRequirement" value={formData.suitabilityForRequirement} onChange={handleChange} required>
          <option value="Selected">Selected</option>
          <option value="Rejected">Rejected</option>
          <option value="Maybe">Maybe</option>
        </select>
      </div>

      <div className="form-group">
        <label>Improvement / Focus Area *</label>
        <textarea
          name="improvementFocusArea"
          value={formData.improvementFocusArea}
          onChange={handleChange}
          required
          rows="3"
          placeholder="Suggest areas where the candidate should focus for improvement..."
        />
      </div>
    </div>
  );

  const renderSection6 = () => (
    <div className="form-section">
      <h3>✍️ Declaration & Signature</h3>
      
      <div className="declaration-box">
        <p><strong>Declaration:</strong></p>
        <p>I confirm:</p>
        <ol>
          <li>The candidate I interviewed/will interview is not referred by me or otherwise known to me in any manner.</li>
          <li>I have not received/or am not entitled to seek or receive, any benefit, monetary or otherwise, as a result of this hiring decision of mine, whether directly or indirectly, and whether for myself or for anyone else.</li>
          <li>If I know any candidate in any manner, whether personally or professionally, or indirectly through a reference or otherwise, I shall immediately bring this to the attention of my manager for their advice. I shall not participate in the recruitment process unless specifically authorized by my manager and/or IBM Recruitment.</li>
          <li>I understand that if the above is found to be untrue or misleading, this will be viewed as a BCG violation, and IBM may initiate disciplinary action against me.</li>
        </ol>
      </div>

      <div className="form-group checkbox-group">
        <label>
          <input
            type="checkbox"
            name="declarationAccepted"
            checked={formData.declarationAccepted}
            onChange={handleChange}
            required
          />
          <span>I accept the above declaration *</span>
        </label>
      </div>

      <div className="form-group">
        <label>Signature of the Evaluator(s) *</label>
        <input
          type="text"
          name="evaluatorSignature"
          value={formData.evaluatorSignature}
          onChange={handleChange}
          required
          placeholder="Enter your full name as signature"
        />
      </div>
    </div>
  );

  return (
    <div className="assessment-form-overlay">
      <div className="assessment-form-container">
        <div className="assessment-form-header">
          <h2>📋 Technical Feedback Form</h2>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>

        {error && (
          <div className="error-message">
            ❌ {error}
          </div>
        )}

        <div className="section-tabs">
          {[1, 2, 3, 4, 5, 6].map(num => (
            <button
              key={num}
              className={`tab-btn ${currentSection === num ? 'active' : ''}`}
              onClick={() => setCurrentSection(num)}
              type="button"
            >
              {num === 1 && '📋 Basic'}
              {num === 2 && '💬 Soft Skills'}
              {num === 3 && '💻 Technical'}
              {num === 4 && '🏗️ Architecting'}
              {num === 5 && '📊 Overall'}
              {num === 6 && '✍️ Declaration'}
            </button>
          ))}
        </div>

        <form onSubmit={handleSubmit} className="assessment-form">
          {currentSection === 1 && renderSection1()}
          {currentSection === 2 && renderSection2()}
          {currentSection === 3 && renderSection3()}
          {currentSection === 4 && renderSection4()}
          {currentSection === 5 && renderSection5()}
          {currentSection === 6 && renderSection6()}

          <div className="form-actions">
            {currentSection > 1 && (
              <button
                type="button"
                className="btn-secondary"
                onClick={() => setCurrentSection(currentSection - 1)}
              >
                ← Previous
              </button>
            )}
            {currentSection < 6 && (
              <button
                type="button"
                className="btn-primary"
                onClick={() => setCurrentSection(currentSection + 1)}
              >
                Next →
              </button>
            )}
            {currentSection === 6 && (
              <button
                type="submit"
                className="btn-submit"
                disabled={submitting}
              >
                {submitting ? '⏳ Submitting...' : '✅ Submit Feedback Form'}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default TechnicalAssessmentForm;

// Made with Bob