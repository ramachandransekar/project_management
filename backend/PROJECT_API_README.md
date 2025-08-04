# Project Management API Documentation

This document describes the REST API endpoints for the Project Management system.

## Base URL
```
http://localhost:8080/api/projects
```

## Authentication
All endpoints (except `/templates` and `/health`) require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Create Project
**POST** `/api/projects`

Creates a new project for the authenticated user.

**Request Body:**
```json
{
  "name": "My New Project",
  "description": "Project description",
  "startDate": "2024-01-15",
  "endDate": "2024-06-30",
  "priority": "medium",
  "template": "web-dev"
}
```

**Field Descriptions:**
- `name` (required): Project name (max 255 characters)
- `description` (optional): Project description (max 1000 characters)
- `startDate` (optional): Project start date (YYYY-MM-DD format)
- `endDate` (optional): Project end date (YYYY-MM-DD format)
- `priority` (required): Priority level (low, medium, high, urgent)
- `template` (required): Project template (none, web-dev, mobile-app, marketing, research)

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "My New Project",
  "description": "Project description",
  "startDate": "2024-01-15",
  "endDate": "2024-06-30",
  "priority": "MEDIUM",
  "template": "WEB_DEV",
  "status": "ACTIVE",
  "createdBy": "username",
  "createdAt": "2024-01-15",
  "updatedAt": null
}
```

### 2. Get User Projects
**GET** `/api/projects`

Retrieves all projects for the authenticated user.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "My New Project",
    "description": "Project description",
    "startDate": "2024-01-15",
    "endDate": "2024-06-30",
    "priority": "MEDIUM",
    "template": "WEB_DEV",
    "status": "ACTIVE",
    "createdBy": "username",
    "createdAt": "2024-01-15",
    "updatedAt": null
  }
]
```

### 3. Get Project by ID
**GET** `/api/projects/{projectId}`

Retrieves a specific project by ID.

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "My New Project",
  "description": "Project description",
  "startDate": "2024-01-15",
  "endDate": "2024-06-30",
  "priority": "MEDIUM",
  "template": "WEB_DEV",
  "status": "ACTIVE",
  "createdBy": "username",
  "createdAt": "2024-01-15",
  "updatedAt": null
}
```

### 4. Get Project Templates
**GET** `/api/projects/templates`

Retrieves available project templates (no authentication required).

**Response (200 OK):**
```json
[
  {
    "id": "none",
    "name": "No Template",
    "description": "Start from scratch",
    "icon": "📝"
  },
  {
    "id": "web-dev",
    "name": "Web Development",
    "description": "Full-stack web application",
    "icon": "🌐"
  },
  {
    "id": "mobile-app",
    "name": "Mobile App",
    "description": "Cross-platform mobile application",
    "icon": "📱"
  },
  {
    "id": "marketing",
    "name": "Marketing Campaign",
    "description": "Digital marketing project",
    "icon": "📢"
  },
  {
    "id": "research",
    "name": "Research Project",
    "description": "Academic or business research",
    "icon": "🔬"
  }
]
```

### 5. Search Projects
**GET** `/api/projects/search?keyword={keyword}`

Searches projects by keyword in name or description.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "My New Project",
    "description": "Project description",
    "startDate": "2024-01-15",
    "endDate": "2024-06-30",
    "priority": "MEDIUM",
    "template": "WEB_DEV",
    "status": "ACTIVE",
    "createdBy": "username",
    "createdAt": "2024-01-15",
    "updatedAt": null
  }
]
```

### 6. Get Projects by Status
**GET** `/api/projects/status/{status}`

Retrieves projects filtered by status.

**Status Values:**
- `active`
- `completed`
- `on_hold`
- `cancelled`

### 7. Get Projects by Priority
**GET** `/api/projects/priority/{priority}`

Retrieves projects filtered by priority.

**Priority Values:**
- `low`
- `medium`
- `high`
- `urgent`

### 8. Get Project Statistics
**GET** `/api/projects/statistics`

Retrieves project statistics for the authenticated user.

**Response (200 OK):**
```json
{
  "totalProjects": 10,
  "activeProjects": 5,
  "completedProjects": 3,
  "urgentProjects": 2
}
```

### 9. Health Check
**GET** `/api/projects/health`

Health check endpoint (no authentication required).

**Response (200 OK):**
```json
{
  "status": "Project API is running"
}
```

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "message": "Validation failed",
  "errors": {
    "name": "Project name is required",
    "priority": "Priority is required"
  },
  "status": "error"
}
```

### Not Found (404)
```json
{
  "message": "Project not found",
  "status": "error"
}
```

### Access Denied (403 Forbidden)
```json
{
  "message": "Access denied",
  "status": "error"
}
```

### Server Error (500 Internal Server Error)
```json
{
  "message": "An unexpected error occurred",
  "status": "error",
  "details": "Error details"
}
```

## Data Models

### Project Entity
- `id`: Unique identifier
- `name`: Project name
- `description`: Project description
- `startDate`: Project start date
- `endDate`: Project end date
- `priority`: Priority level (LOW, MEDIUM, HIGH, URGENT)
- `template`: Project template (NONE, WEB_DEV, MOBILE_APP, MARKETING, RESEARCH)
- `status`: Project status (ACTIVE, COMPLETED, ON_HOLD, CANCELLED)
- `createdBy`: User who created the project
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

## Database Schema

The API uses the following database tables:
- `users`: User information
- `projects`: Project information with foreign key to users

## Security

- JWT-based authentication
- User can only access their own projects
- Input validation and sanitization
- CORS enabled for frontend integration 