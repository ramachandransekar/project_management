# CORS (Cross-Origin Resource Sharing) Implementation

This document describes the CORS implementation in the Project Management Backend API.

## Overview

CORS (Cross-Origin Resource Sharing) is a security feature implemented by web browsers that controls how web pages in one domain can request and interact with resources from another domain. This implementation ensures secure communication between the frontend and backend applications.

## Implementation Details

### 1. Global CORS Configuration

The main CORS configuration is implemented in `CorsConfig.java` which provides:

- **Allowed Origins**: Specific domains that can access the API
- **Allowed Methods**: HTTP methods permitted (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- **Allowed Headers**: Headers that can be sent with requests
- **Exposed Headers**: Headers that the client can access
- **Credentials**: Support for cookies and authorization headers
- **Max Age**: Cache duration for preflight requests

### 2. Security Configuration

The `WebSecurityConfig.java` is configured to work with the CORS implementation:

- CORS is enabled for all endpoints
- CSRF is disabled (not needed for stateless JWT authentication)
- JWT authentication filter is applied

### 3. Controller-Level Configuration

All controller-level `@CrossOrigin` annotations have been removed in favor of the global configuration to ensure consistency and security.

## Allowed Origins

The following origins are currently allowed to access the API:

### Development
- `http://localhost:3000` - React development server
- `http://localhost:5173` - Vite development server
- `http://127.0.0.1:3000` - Alternative local development
- `http://127.0.0.1:5173` - Alternative Vite development

### Production
- `https://project-management-frontend.onrender.com` - Production frontend (update with actual domain)
- `https://your-frontend-domain.com` - Placeholder for your actual frontend domain

## Allowed Headers

The following headers are allowed in requests:

- `Authorization` - JWT token authentication
- `Content-Type` - Request content type
- `X-Requested-With` - AJAX request identifier
- `Accept` - Response content type preference
- `Origin` - Request origin
- `Access-Control-Request-Method` - Preflight request method
- `Access-Control-Request-Headers` - Preflight request headers
- `x-auth-token` - Custom authentication token

## Exposed Headers

The following headers are exposed to the client:

- `Authorization` - Response authorization header
- `x-auth-token` - Custom authentication token
- `Access-Control-Allow-Origin` - CORS origin header
- `Access-Control-Allow-Credentials` - CORS credentials header

## Configuration Files

### 1. CorsConfig.java
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // Global CORS configuration
}
```

### 2. application.properties
```properties
# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173
spring.web.cors.allowed-methods=GET,POST,PUT,PATCH,DELETE,OPTIONS
spring.web.cors.allowed-headers=Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,x-auth-token
spring.web.cors.exposed-headers=Authorization,x-auth-token,Access-Control-Allow-Origin,Access-Control-Allow-Credentials
spring.web.cors.allow-credentials=true
spring.web.cors.max-age=3600
```

## Testing CORS

### 1. CORS Test Endpoint
A test endpoint is available at `/api/test/cors-test` to verify CORS functionality:

```bash
curl -X GET http://localhost:8080/api/test/cors-test \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Content-Type"
```

### 2. Preflight Request Test
Test OPTIONS preflight requests:

```bash
curl -X OPTIONS http://localhost:8080/api/projects \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization,Content-Type" \
  -v
```

## Security Considerations

### 1. Origin Validation
- Only specific origins are allowed (no wildcards in production)
- Origins should be updated when deploying to production

### 2. Credentials
- Credentials are allowed for authentication
- JWT tokens are properly handled

### 3. Headers
- Only necessary headers are allowed
- Sensitive headers are not exposed

## Troubleshooting

### Common CORS Issues

1. **Preflight Request Failing**
   - Ensure OPTIONS method is allowed
   - Check that all required headers are in allowed-headers

2. **Credentials Not Working**
   - Verify `allow-credentials=true`
   - Check that origin is not using wildcard when credentials are enabled

3. **Authorization Header Issues**
   - Ensure `Authorization` is in allowed-headers
   - Verify JWT token format

### Debug Steps

1. Check browser console for CORS errors
2. Verify the request origin matches allowed origins
3. Test with curl to isolate frontend vs backend issues
4. Check server logs for CORS-related errors

## Production Deployment

When deploying to production:

1. Update allowed origins in `CorsConfig.java` with your actual frontend domain
2. Remove development origins if not needed
3. Consider using environment variables for origins
4. Test CORS functionality in production environment

## Environment Variables

Consider using environment variables for origins in production:

```java
@Value("${cors.allowed-origins}")
private String allowedOrigins;
```

And in application.properties:
```properties
cors.allowed-origins=https://your-production-domain.com
``` 