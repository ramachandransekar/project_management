# Signup Debugging Guide

This guide helps troubleshoot signup issues in the Project Management API.

## Recent Changes Made

### 1. Enhanced Logging
- Added detailed logging to AuthController for signup requests
- Added debug logging for request data and responses
- Configured logging levels in application.properties

### 2. Improved Error Handling
- Enhanced GlobalExceptionHandler for authentication errors
- Added specific handling for username/email conflicts
- Better error messages for validation failures

### 3. Test Endpoints
- Added `/api/test/test-signup` for testing request format
- Enhanced `/api/test/cors-test` for CORS verification

## Debugging Steps

### 1. Check Server Logs
Look for these log messages when attempting signup:
```
Signup request received for username: [username]
Signup request data: username=[username], email=[email], firstName=[firstName], lastName=[lastName]
Signup successful for username: [username]
```

### 2. Test Basic Connectivity
```bash
# Test if the server is reachable
curl -X GET http://project-management-1-kkb0.onrender.com/health

# Test CORS
curl -X GET http://project-management-1-kkb0.onrender.com/api/test/cors-test
```

### 3. Test Signup Endpoint
```bash
# Test signup with valid data
curl -X POST http://project-management-1-kkb0.onrender.com/api/auth/signup \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "testuser123",
    "email": "testuser123@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }' \
  -v

# Test with test endpoint (no validation)
curl -X POST http://project-management-1-kkb0.onrender.com/api/test/test-signup \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{
    "username": "testuser123",
    "email": "testuser123@example.com",
    "password": "password123"
  }' \
  -v
```

### 4. Test CORS Preflight
```bash
# Test OPTIONS request
curl -X OPTIONS http://project-management-1-kkb0.onrender.com/api/auth/signup \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

## Common Issues and Solutions

### 1. CORS Issues
**Symptoms:**
- Browser console shows CORS errors
- Preflight requests fail

**Solutions:**
- Check if origin is in allowed origins list
- Verify CORS configuration is loaded
- Test with curl to isolate frontend vs backend issues

### 2. Validation Errors
**Symptoms:**
- 400 Bad Request responses
- Validation error messages

**Common Validation Rules:**
- Username: 3-20 characters, required
- Email: Valid email format, required
- Password: 6-40 characters, required
- firstName, lastName: Optional

### 3. Database Issues
**Symptoms:**
- 500 Internal Server Error
- Database connection errors

**Solutions:**
- Check database connectivity
- Verify database schema
- Check for unique constraint violations

### 4. Username/Email Conflicts
**Symptoms:**
- "Username is already taken" error
- "Email is already in use" error

**Solutions:**
- Use unique username/email combinations
- Check existing users in database

## Frontend Debugging

### 1. Check Browser Console
```javascript
// Check if request is being made
console.log('Making signup request...');

// Check request data
console.log('Signup data:', formData);

// Check response
console.log('Signup response:', response);
```

### 2. Check Network Tab
- Look for failed requests
- Check request headers
- Verify request payload
- Check response status and body

### 3. Verify Request Format
The signup request should have:
- Method: POST
- URL: http://project-management-1-kkb0.onrender.com/api/auth/signup
- Headers: Content-Type: application/json
- Body: JSON with username, email, password, firstName, lastName

## Expected Response Format

### Success Response:
```json
{
  "message": "User registered successfully!",
  "id": 123,
  "username": "testuser",
  "email": "testuser@example.com",
  "firstName": "Test",
  "lastName": "User"
}
```

### Error Response:
```json
{
  "message": "Username is already taken!",
  "status": "error"
}
```

## Testing Checklist

- [ ] Server is running and accessible
- [ ] CORS is properly configured
- [ ] Request format is correct
- [ ] All required fields are provided
- [ ] Username/email are unique
- [ ] Password meets requirements
- [ ] Database is accessible
- [ ] No validation errors

## Next Steps

1. Run the test commands above
2. Check server logs for detailed error messages
3. Verify frontend request format
4. Test with different data combinations
5. Check database connectivity and schema 