import axios from 'axios';

// Create axios instance
const api = axios.create({
  baseURL: 'http://project-management-1-kkb0.onrender.com/api',
  timeout: 10000,
});

// Request interceptor to add auth token and log requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    console.log(`Making ${config.method?.toUpperCase()} request to: ${config.url}`);
    console.log('Headers:', config.headers);
    
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to log responses and handle auth errors
api.interceptors.response.use(
  (response) => {
    console.log(`Response from ${response.config.url}:`, response.status);
    return response;
  },
  (error) => {
    console.error(`Error from ${error.config?.url}:`, error.response?.status, error.response?.data);
    
    // Handle 401 Unauthorized errors
    if (error.response?.status === 401) {
      console.warn('Authentication failed, clearing token');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // You might want to redirect to login here
    }
    
    return Promise.reject(error);
  }
);

export default api; 