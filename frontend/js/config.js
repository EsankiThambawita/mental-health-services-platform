// API Configuration
const API_CONFIG = {
  BASE_URL: 'http://localhost:8082/api/recovery-plans',
  
  // Helper function to build URLs
  buildUrl: (path) => `${API_CONFIG.BASE_URL}${path}`,
  
  // Helper function to handle API responses
  handleResponse: async (response) => {
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'An error occurred');
    }
    return response.json();
  },
  
  // Helper function for GET requests
  get: async (path) => {
    const response = await fetch(API_CONFIG.buildUrl(path));
    return API_CONFIG.handleResponse(response);
  },
  
  // Helper function for POST requests
  post: async (path, data) => {
    const response = await fetch(API_CONFIG.buildUrl(path), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return API_CONFIG.handleResponse(response);
  },
  
  // Helper function for PUT requests
  put: async (path, data) => {
    const response = await fetch(API_CONFIG.buildUrl(path), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return API_CONFIG.handleResponse(response);
  },
  
  // Helper function for PATCH requests
  patch: async (path, data = null) => {
    const options = {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' }
    };
    if (data) {
      options.body = JSON.stringify(data);
    }
    const response = await fetch(API_CONFIG.buildUrl(path), options);
    return API_CONFIG.handleResponse(response);
  },
  
  // Helper function for DELETE requests
  delete: async (path) => {
    const response = await fetch(API_CONFIG.buildUrl(path), {
      method: 'DELETE'
    });
    if (response.status === 204) {
      return { success: true };
    }
    return API_CONFIG.handleResponse(response);
  }
};

// Utility functions
const formatDate = (dateString) => {
  if (!dateString) return 'No due date';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric' 
  });
};

const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('en-US', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const isOverdue = (dueDate) => {
  if (!dueDate) return false;
  return new Date(dueDate) < new Date();
};

// Show alert messages
const showAlert = (message, type = 'info') => {
  const alertDiv = document.createElement('div');
  alertDiv.className = `alert alert-${type}`;
  alertDiv.textContent = message;
  
  const container = document.querySelector('.container');
  container.insertBefore(alertDiv, container.firstChild);
  
  setTimeout(() => alertDiv.remove(), 5000);
};

// Show error messages
const showError = (error) => {
  console.error('Error:', error);
  showAlert(error.message || 'An error occurred', 'error');
};