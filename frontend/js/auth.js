// Auth Service Functions

const AUTH_API = 'http://localhost:8084/api/auth';

// Signup function
async function signup(data) {
  try {
    const response = await fetch(`${AUTH_API}/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Signup failed');
    }
    
    const result = await response.json();
    
    // Store token and user info
    localStorage.setItem('token', result.token);
    localStorage.setItem('userId', result.userId);
    localStorage.setItem('email', result.email);
    localStorage.setItem('name', result.name);
    localStorage.setItem('role', result.role);
    
    showAlert('Account created successfully!', 'success');
    
    // Redirect based on role
    setTimeout(() => {
      if (result.role === 'COUNSELOR') {
        window.location.href = '../recovery-plans/counselor.html';
      } else {
        window.location.href = '../recovery-plans/patient.html';
      }
    }, 1000);
    
  } catch (error) {
    showAlert(error.message, 'error');
  }
}

// Login function
async function login(data) {
  try {
    const response = await fetch(`${AUTH_API}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Login failed');
    }
    
    const result = await response.json();
    
    // Store token and user info
    localStorage.setItem('token', result.token);
    localStorage.setItem('userId', result.userId);
    localStorage.setItem('email', result.email);
    localStorage.setItem('name', result.name);
    localStorage.setItem('role', result.role);
    
    showAlert('Login successful!', 'success');
    
    // Redirect based on role
    setTimeout(() => {
      if (result.role === 'COUNSELOR') {
        window.location.href = '../recovery-plans/counselor.html';
      } else {
        window.location.href = '../recovery-plans/patient.html';
      }
    }, 1000);
    
  } catch (error) {
    showAlert(error.message, 'error');
  }
}

// Logout function
function logout() {
  localStorage.clear();
  window.location.href = '../authentication/signin.html';
}

// Check if user is logged in
function checkAuth() {
  const token = localStorage.getItem('token');
  if (!token) {
    window.location.href = '../authentication/signin.html';
  }
  return token;
}

// Get auth header
function getAuthHeader() {
  return 'Bearer ' + localStorage.getItem('token');
}

// Show alert
function showAlert(message, type) {
  const alertDiv = document.getElementById('alert');
  if (!alertDiv) return;
  
  alertDiv.className = `alert alert-${type}`;
  alertDiv.textContent = message;
  alertDiv.classList.remove('hidden');
  
  setTimeout(() => {
    alertDiv.classList.add('hidden');
  }, 5000);
}