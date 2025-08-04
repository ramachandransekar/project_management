import axios from 'axios';

// Create axios instance
const api = axios.create({
  baseURL: import.meta.env.DEV ? '/api' : 'https://project-management-1-kkb0.onrender.com/api',
  timeout: 10000,
  withCredentials: true,
});

// Request interceptor to add auth token and log requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add CORS headers
    config.headers['Content-Type'] = 'application/json';
    
    console.log(`Making ${config.method?.toUpperCase()} request to: ${config.url}`);
    console.log('Request headers:', config.headers);
    console.log('Request data:', config.data);
    
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
    console.log('Response headers:', response.headers);
    console.log('Response data:', response.data);
    return response;
  },
  (error) => {
    console.error(`Error from ${error.config?.url}:`, error.response?.status, error.response?.data);
    console.error('Error response headers:', error.response?.headers);
    console.error('Error config:', error.config);
    
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