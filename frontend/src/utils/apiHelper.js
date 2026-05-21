/**
 * API Helper - Centralized API call handler with retry logic and error handling
 */

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';
const MAX_RETRIES = 3;
const RETRY_DELAY = 2000; // 2 seconds
const REQUEST_TIMEOUT = 90000; // 90 seconds (increased for operations like interview scheduling with email notifications)

/**
 * Sleep utility for retry delays
 */
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

/**
 * Check if backend is reachable
 * Tries multiple health endpoints for better reliability
 */
export const checkBackendHealth = async () => {
  const healthEndpoints = [
    `${API_BASE_URL}/api/health`,
    `${API_BASE_URL}/actuator/health`
  ];

  for (const endpoint of healthEndpoints) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 second timeout

      const response = await fetch(endpoint, {
        method: 'GET',
        signal: controller.signal
      }).catch(() => null);

      clearTimeout(timeoutId);

      if (response && response.ok) {
        return true;
      }
    } catch (error) {
      // Try next endpoint
      continue;
    }
  }
  
  return false;
};

/**
 * Enhanced fetch with retry logic and better error handling
 */
export const fetchWithRetry = async (url, options = {}, retries = MAX_RETRIES) => {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT);

  try {
    const response = await fetch(url, {
      ...options,
      signal: controller.signal
    });

    clearTimeout(timeoutId);

    // If response is ok, return it
    if (response.ok) {
      return response;
    }

    // Handle specific error codes
    if (response.status === 401) {
      throw new Error('UNAUTHORIZED');
    }

    if (response.status === 403) {
      throw new Error('FORBIDDEN');
    }

    // For 5xx errors, retry
    if (response.status >= 500 && retries > 0) {
      console.log(`Server error (${response.status}), retrying... (${MAX_RETRIES - retries + 1}/${MAX_RETRIES})`);
      await sleep(RETRY_DELAY);
      return fetchWithRetry(url, options, retries - 1);
    }

    // For other errors, throw
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Request failed with status ${response.status}`);

  } catch (error) {
    clearTimeout(timeoutId);

    // Handle network errors with retry
    if (error.name === 'AbortError') {
      if (retries > 0) {
        console.log(`Request timeout, retrying... (${MAX_RETRIES - retries + 1}/${MAX_RETRIES})`);
        await sleep(RETRY_DELAY);
        return fetchWithRetry(url, options, retries - 1);
      }
      throw new Error('Request timeout. Please check if backend is running.');
    }

    // Handle fetch errors (network issues)
    if (error.message === 'Failed to fetch' || error.message.includes('NetworkError')) {
      if (retries > 0) {
        console.log(`Network error, retrying... (${MAX_RETRIES - retries + 1}/${MAX_RETRIES})`);
        await sleep(RETRY_DELAY);
        return fetchWithRetry(url, options, retries - 1);
      }
      throw new Error('Failed to connect to backend. Please ensure the backend server is running on http://localhost:8081');
    }

    // Don't retry for auth errors
    if (error.message === 'UNAUTHORIZED' || error.message === 'FORBIDDEN') {
      throw error;
    }

    // For other errors, retry if retries left
    if (retries > 0) {
      console.log(`Error occurred, retrying... (${MAX_RETRIES - retries + 1}/${MAX_RETRIES})`);
      await sleep(RETRY_DELAY);
      return fetchWithRetry(url, options, retries - 1);
    }

    throw error;
  }
};

/**
 * Make authenticated API call with retry logic
 */
export const apiCall = async (endpoint, options = {}) => {
  const token = localStorage.getItem('token');
  
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    credentials: 'include',
    ...options
  };

  try {
    const response = await fetchWithRetry(url, defaultOptions);
    
    // Try to parse JSON response
    const data = await response.json().catch(() => ({}));
    
    return {
      ok: response.ok,
      status: response.status,
      data
    };
  } catch (error) {
    console.error('API call failed:', error);
    
    // Handle auth errors
    if (error.message === 'UNAUTHORIZED') {
      // Clear auth data and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/';
      throw new Error('Session expired. Please login again.');
    }

    if (error.message === 'FORBIDDEN') {
      throw new Error('Access denied. You do not have permission to perform this action.');
    }

    throw error;
  }
};

/**
 * GET request helper
 */
export const apiGet = async (endpoint) => {
  return apiCall(endpoint, { method: 'GET' });
};

/**
 * POST request helper
 */
export const apiPost = async (endpoint, body) => {
  return apiCall(endpoint, {
    method: 'POST',
    body: JSON.stringify(body)
  });
};

/**
 * PUT request helper
 */
export const apiPut = async (endpoint, body) => {
  return apiCall(endpoint, {
    method: 'PUT',
    body: JSON.stringify(body)
  });
};

/**
 * DELETE request helper
 */
export const apiDelete = async (endpoint) => {
  return apiCall(endpoint, { method: 'DELETE' });
};

// Made with Bob