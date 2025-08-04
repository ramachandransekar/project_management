# Authentication Debugging Guide

This guide helps troubleshoot the "Full authentication is required to access this resource" errors.

## Common Causes

### 1. Missing JWT Token
The most common cause is that requests are being made without the JWT token in the Authorization header.

**Symptoms:**
- 401 Unauthorized errors
- "Full authentication is required" messages
- Requests to protected endpoints without Authorization header

**Debug Steps:**
1. Check browser console for requests being made
2. Verify localStorage contains 'token'
3. Check if Authorization header is being sent

### 2. Invalid JWT Token
The token might be expired, malformed, or invalid.

**Symptoms:**
- 401 Unauthorized errors even with Authorization header
- Token validation failures in logs

**Debug Steps:**
1. Check JWT token format in localStorage
2. Verify token hasn't expired
3. Check server logs for JWT validation errors

### 3. Race Conditions
Components might be making requests before authentication is complete.

**Symptoms:**
- Errors on page load
- Requests made before user is authenticated

**Debug Steps:**
1. Check component loading order
2. Verify authentication state before making requests
3. Add loading states to prevent premature requests

## Debugging Steps

### 1. Check Browser Console
Open browser developer tools and check:
- Network tab for failed requests
- Console for error messages
- Application tab for localStorage contents

### 2. Verify Authentication State
```javascript
// In browser console
console.log('Token:', localStorage.getItem('token'));
console.log('User:', localStorage.getItem('user'));
```

### 3. Test Authentication Endpoints
```bash
# Test login
curl -X POST http://project-management-1-kkb0.onrender.com/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"your_username","password":"your_password"}'

# Test protected endpoint with token
curl -X GET http://project-management-1-kkb0.onrender.com/api/test/auth-test \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Check Server Logs
Look for these log messages:
- JWT parsing and validation
- Authentication failures
- Request details

## Recent Changes Made

### 1. Enhanced Logging
- Added detailed request logging in JwtAuthenticationFilter
- Enhanced error logging in JwtAuthenticationEntryPoint
- Added request headers logging

### 2. Frontend Improvements
- Fixed race condition in Dashboard component
- Added loading state checks
- Improved authentication flow

### 3. Debug Endpoints
- `/api/test/cors-test` - Test CORS functionality
- `/api/test/auth-test` - Test authentication (requires valid token)

## Testing Authentication Flow

### 1. Login Process
1. Navigate to the application
2. Enter valid credentials
3. Check localStorage for token
4. Verify Authorization header is set

### 2. Protected Endpoint Access
1. After login, try accessing dashboard
2. Check network requests for Authorization header
3. Verify successful responses

### 3. Token Validation
1. Check token format in localStorage
2. Decode JWT token (if needed)
3. Verify token expiration

## Common Fixes

### 1. Add Authentication Check
```javascript
// Before making API calls
if (!user || !localStorage.getItem('token')) {
  // Redirect to login or show error
  return;
}
```

### 2. Add Loading States
```javascript
// Prevent requests during loading
if (loading) {
  return <div>Loading...</div>;
}
```

### 3. Use Axios Interceptors
```javascript
// Automatically add token to requests
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## Server-Side Debugging

### 1. Enable Debug Logging
Add to application.properties:
```properties
logging.level.com.projectmanagement.security=DEBUG
logging.level.org.springframework.security=DEBUG
```

### 2. Check JWT Configuration
Verify JWT secret and expiration settings:
```properties
jwt.secret=your-secret-key-here-make-it-very-long-and-secure-in-production
jwt.expiration=86400000
```

### 3. Test JWT Token
Use a JWT decoder to verify token contents and expiration.

## Next Steps

1. Check browser console for specific error messages
2. Verify localStorage contains valid token
3. Test authentication endpoints manually
4. Check server logs for detailed error information
5. Update this guide with specific findings 