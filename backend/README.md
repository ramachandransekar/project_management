# Project Management Backend

A Spring Boot application with JWT authentication and PostgreSQL database integration.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

## Database Setup

1. Install PostgreSQL if not already installed
2. Create a database named `project_management`:
   ```sql
   CREATE DATABASE project_management;
   ```
3. The application will automatically create the required tables on startup

## Configuration

The application is configured to connect to PostgreSQL with the following default settings:
- Host: localhost
- Port: 5432
- Database: project_management
- Username: postgres
- Password: postgres

You can modify these settings in `src/main/resources/application.properties`.

## Running the Application

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/signin` - Login user

### Test Endpoints
- `GET /api/test/all` - Public endpoint
- `GET /api/test/user` - Protected endpoint (requires authentication)
- `GET /api/test/mod` - Moderator endpoint
- `GET /api/test/admin` - Admin endpoint

## JWT Configuration

The JWT secret key and expiration time can be configured in `application.properties`:
- `jwt.secret` - Secret key for JWT signing
- `jwt.expiration` - Token expiration time in milliseconds (default: 24 hours)

## Security

- Passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- CORS is configured to allow requests from the frontend
- Protected endpoints require valid JWT tokens 