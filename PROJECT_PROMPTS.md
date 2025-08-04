# Project Management System - Development Prompts

## Overview
This document contains comprehensive development prompts organized by features for the Project Management System. The system consists of a React frontend and Spring Boot backend with JWT authentication and PostgreSQL database.

## Table of Contents
1. [Authentication & User Management](#authentication--user-management)
2. [Project Management](#project-management)
3. [Task Management](#task-management)
4. [Time Tracking](#time-tracking)
5. [Team Collaboration](#team-collaboration)
6. [Progress Tracking](#progress-tracking)
7. [Dashboard & Analytics](#dashboard--analytics)
8. [File Management](#file-management)
9. [Security & Configuration](#security--configuration)
10. [Testing & Deployment](#testing--deployment)

---

## Authentication & User Management

### Backend Implementation

**Prompt for AuthController:**
```
Create a comprehensive authentication controller with the following endpoints:
- POST /api/auth/signin - User login with JWT token response
- POST /api/auth/signup - User registration
- POST /api/auth/create-user - Admin user creation (protected)
- GET /api/auth/users - Get all users (admin only)

Requirements:
- Implement proper validation for all request DTOs
- Use BCrypt for password hashing
- Generate JWT tokens with configurable expiration
- Include role-based access control (USER, ADMIN)
- Handle duplicate username/email errors gracefully
- Return appropriate HTTP status codes and error messages
- Implement proper exception handling with GlobalExceptionHandler
```

**Prompt for User Management Service:**
```
Create a UserService that handles:
- User registration with email validation
- Password encryption using BCrypt
- JWT token generation and validation
- User role management (USER, ADMIN)
- User profile updates
- Password reset functionality
- Account deactivation/reactivation

Include proper validation, error handling, and security measures.
```

**Prompt for JWT Security Configuration:**
```
Implement JWT-based security configuration with:
- JWT token generation and validation
- Password encryption with BCrypt
- Role-based authorization
- CORS configuration for frontend integration
- Protected endpoint configuration
- Token refresh mechanism
- Security headers configuration
```

### Frontend Implementation

**Prompt for Authentication Components:**
```
Create a comprehensive authentication system with:
- Login form with username/email and password
- Registration form with validation
- JWT token storage in localStorage
- Protected route components
- Authentication context for state management
- Auto-logout on token expiration
- Password strength validation
- Remember me functionality
- Forgot password flow

UI Requirements:
- Modern, responsive design with glass morphism effects
- Real-time form validation with error messages
- Loading states during API calls
- Success/error notifications
- Mobile-friendly interface
```

**Prompt for User Creation Component:**
```
Create an admin user creation component with:
- Form for creating new users (username, email, first name, last name, password)
- Role selection (USER, ADMIN)
- Real-time validation
- Success/error feedback
- User list display
- User management actions (edit, deactivate, delete)

Include proper form validation, loading states, and responsive design.
```

---

## Project Management

### Backend Implementation

**Prompt for ProjectController:**
```
Create a comprehensive project management controller with endpoints:
- POST /api/projects - Create new project
- GET /api/projects - Get user's projects
- GET /api/projects/{id} - Get project by ID
- PUT /api/projects/{id} - Update project
- DELETE /api/projects/{id} - Delete project
- GET /api/projects/templates - Get project templates
- GET /api/projects/search - Search projects
- GET /api/projects/status/{status} - Filter by status
- GET /api/projects/priority/{priority} - Filter by priority
- GET /api/projects/statistics - Get project statistics

Requirements:
- JWT authentication for all endpoints
- Proper validation and error handling
- Project ownership verification
- Support for project templates (web-dev, mobile-app, marketing, research)
- Priority levels (low, medium, high, urgent)
- Status management (active, completed, on_hold, cancelled)
- Search functionality with keyword matching
- Statistics aggregation
```

**Prompt for Project Service:**
```
Implement ProjectService with business logic for:
- Project creation with template support
- Project CRUD operations
- Project search and filtering
- Project statistics calculation
- Project member management
- Project status transitions
- Project priority management
- Project template handling
- Data validation and business rules
- Exception handling and error responses
```

### Frontend Implementation

**Prompt for Project Creation Component:**
```
Create a project creation form with:
- Project name and description fields
- Start and end date selection
- Priority selection (low, medium, high, urgent)
- Project template selection with icons
- Real-time validation
- Preview of selected template
- Success/error feedback

UI Requirements:
- Modern form design with validation
- Date picker components
- Template selection with visual previews
- Responsive layout
- Loading states
```

**Prompt for Project View/Edit Components:**
```
Create project management components:
- Project list with filtering and search
- Project detail view
- Project edit form
- Project status management
- Project member management
- Project statistics dashboard

Features:
- Kanban-style project board
- Project cards with priority indicators
- Search and filter functionality
- Bulk actions
- Project templates
- Progress visualization
```

---

## Task Management

### Backend Implementation

**Prompt for TaskController:**
```
Create a comprehensive task management controller with endpoints:
- POST /api/tasks - Create new task
- GET /api/tasks - Get all user tasks
- GET /api/tasks/{id} - Get task by ID
- PUT /api/tasks/{id} - Update task
- DELETE /api/tasks/{id} - Delete task
- GET /api/tasks/project/{projectId} - Get project tasks
- POST /api/tasks/{id}/comments - Add task comment
- GET /api/tasks/{id}/comments - Get task comments
- POST /api/tasks/{id}/attachments - Upload attachment
- GET /api/tasks/{id}/attachments - Get task attachments
- DELETE /api/tasks/attachments/{id} - Delete attachment
- GET /api/tasks/statistics - Get task statistics

Requirements:
- JWT authentication
- File upload handling
- Task assignment to users
- Task priority and status management
- Subtask support
- Task dependencies
- Task comments and attachments
- Task statistics and reporting
```

**Prompt for Task Service:**
```
Implement TaskService with features:
- Task CRUD operations
- Task assignment and reassignment
- Task status workflow management
- Task priority handling
- Subtask management
- Task dependency tracking
- File attachment handling
- Task commenting system
- Task search and filtering
- Task statistics calculation
- Task progress tracking
- Task deadline management
```

### Frontend Implementation

**Prompt for Task Management Component:**
```
Create a comprehensive task management interface with:
- Task creation form
- Task list with filtering and sorting
- Task detail view
- Task edit functionality
- Task assignment interface
- Task status management
- Subtask management
- Task comments section
- File attachment handling
- Task search functionality

UI Features:
- Drag-and-drop task reordering
- Kanban board view
- List view with sorting
- Task cards with priority indicators
- Progress bars
- Due date indicators
- Assignment avatars
- File preview
- Real-time updates
```

---

## Time Tracking

### Backend Implementation

**Prompt for TimeTrackingController:**
```
Create a time tracking controller with endpoints:
- POST /api/time-tracking/entries - Create time entry
- GET /api/time-tracking/entries - Get user time entries
- GET /api/time-tracking/entries/range - Get entries by date range
- GET /api/time-tracking/projects/{id}/entries - Get project entries
- GET /api/time-tracking/projects/{id}/entries/range - Get project entries by range
- GET /api/time-tracking/projects/{id}/summary - Get project time summary
- GET /api/time-tracking/summary - Get user time summary
- PUT /api/time-tracking/entries/{id}/status - Update entry status
- POST /api/time-tracking/entries/submit - Submit entries for approval
- GET /api/time-tracking/entries/status/{status} - Get entries by status
- DELETE /api/time-tracking/entries/{id} - Delete time entry

Requirements:
- Time entry creation with start/end times
- Project and task association
- Time entry status management (draft, submitted, approved, rejected)
- Time summary calculations
- Date range filtering
- Approval workflow
- Time entry validation
```

**Prompt for Time Tracking Service:**
```
Implement TimeTrackingService with:
- Time entry CRUD operations
- Time calculation and validation
- Project and task time tracking
- Time entry status management
- Approval workflow handling
- Time summary generation
- Date range filtering
- Time entry reporting
- Overtime calculation
- Time entry validation rules
```

### Frontend Implementation

**Prompt for Time Tracking Component:**
```
Create a time tracking interface with:
- Time entry creation form
- Timer functionality (start/stop/pause)
- Time entry list with filtering
- Time entry edit functionality
- Project and task selection
- Time summary dashboard
- Time entry approval interface
- Time reporting features

UI Features:
- Timer with start/stop controls
- Time entry grid with date navigation
- Project and task dropdowns
- Time summary charts
- Approval workflow interface
- Time entry validation
- Export functionality
- Mobile-friendly timer
```

---

## Team Collaboration

### Backend Implementation

**Prompt for TeamCollaborationController:**
```
Create a team collaboration controller with endpoints:
- POST /api/team/project/{id}/add-member - Add member to project
- GET /api/team/project/{id}/members - Get project members
- DELETE /api/team/project/{id}/remove-member/{userId} - Remove member
- GET /api/team/project/{id}/activity - Get project activity
- GET /api/team/project/{id}/activity/recent - Get recent activity
- POST /api/team/project/{id}/note - Create/update project note
- GET /api/team/project/{id}/note - Get project note
- POST /api/team/tasks/{id}/comment - Add task comment

Requirements:
- Project member management
- Activity logging
- Project notes
- Task comments
- Role-based permissions
- Activity feed generation
- Real-time notifications
```

**Prompt for Team Collaboration Service:**
```
Implement TeamCollaborationService with:
- Project member management
- Activity logging system
- Project notes handling
- Task commenting system
- Role-based access control
- Activity feed generation
- Notification system
- Team member permissions
- Activity tracking
- Collaboration analytics
```

### Frontend Implementation

**Prompt for Team Collaboration Component:**
```
Create a team collaboration interface with:
- Project member management
- Activity feed display
- Project notes editor
- Task comments section
- Team member roles
- Activity notifications
- Team member profiles

UI Features:
- Member list with roles
- Activity timeline
- Rich text editor for notes
- Comment threads
- Role management interface
- Activity notifications
- Team member avatars
- Real-time updates
```

---

## Progress Tracking

### Backend Implementation

**Prompt for ProgressController:**
```
Create a progress tracking controller with endpoints:
- GET /api/progress/project/{id} - Get project progress
- GET /api/progress/projects - Get all projects progress
- GET /api/progress/project/{id}/leaderboard - Get team leaderboard

Requirements:
- Project progress calculation
- Task completion tracking
- Team member progress
- Progress visualization data
- Leaderboard generation
- Progress metrics
- Burndown chart data
- Velocity tracking
```

**Prompt for Progress Service:**
```
Implement ProgressService with:
- Project progress calculation
- Task completion tracking
- Team member progress
- Progress metrics calculation
- Leaderboard generation
- Burndown chart data
- Velocity tracking
- Progress reporting
- Milestone tracking
- Progress analytics
```

### Frontend Implementation

**Prompt for Progress Tracking Component:**
```
Create a progress tracking interface with:
- Project progress dashboard
- Task completion tracking
- Team member progress
- Progress charts and graphs
- Leaderboard display
- Burndown charts
- Velocity tracking
- Milestone tracking

UI Features:
- Progress bars and charts
- Team leaderboard
- Burndown charts
- Velocity graphs
- Milestone indicators
- Progress filters
- Export functionality
- Real-time updates
```

---

## Dashboard & Analytics

### Backend Implementation

**Prompt for Dashboard Analytics:**
```
Create dashboard analytics endpoints:
- GET /api/dashboard/overview - Get dashboard overview
- GET /api/dashboard/projects/summary - Get projects summary
- GET /api/dashboard/tasks/summary - Get tasks summary
- GET /api/dashboard/time/summary - Get time tracking summary
- GET /api/dashboard/team/summary - Get team summary
- GET /api/dashboard/activity/recent - Get recent activity

Requirements:
- Comprehensive dashboard data
- Project statistics
- Task statistics
- Time tracking analytics
- Team performance metrics
- Activity feed
- Performance indicators
- Trend analysis
```

### Frontend Implementation

**Prompt for Dashboard Component:**
```
Create a comprehensive dashboard with:
- Overview statistics
- Project summary cards
- Task status overview
- Time tracking summary
- Team performance metrics
- Recent activity feed
- Quick action buttons
- Performance charts

UI Features:
- Responsive grid layout
- Interactive charts
- Real-time updates
- Quick navigation
- Customizable widgets
- Export functionality
- Mobile optimization
- Dark/light theme
```

---

## File Management

### Backend Implementation

**Prompt for File Management:**
```
Implement file management system with:
- File upload handling
- File storage management
- File type validation
- File size limits
- File security
- File metadata storage
- File access control
- File versioning
- File cleanup
- File compression

Requirements:
- Secure file upload
- File type restrictions
- Size limit enforcement
- Virus scanning
- Access control
- File organization
- Backup system
```

### Frontend Implementation

**Prompt for File Management UI:**
```
Create file management interface with:
- File upload component
- File list display
- File preview
- File download
- File deletion
- File organization
- File search
- File sharing

UI Features:
- Drag-and-drop upload
- File preview
- Progress indicators
- File type icons
- File size display
- Upload queue
- File organization
- Search functionality
```

---

## Security & Configuration

### Backend Implementation

**Prompt for Security Configuration:**
```
Implement comprehensive security with:
- JWT authentication
- Password encryption
- Role-based access control
- CORS configuration
- Security headers
- Input validation
- SQL injection prevention
- XSS protection
- CSRF protection
- Rate limiting

Requirements:
- Secure authentication
- Data protection
- API security
- Error handling
- Logging
- Monitoring
```

### Frontend Implementation

**Prompt for Security Features:**
```
Implement frontend security with:
- JWT token management
- Protected routes
- Input sanitization
- XSS prevention
- Secure storage
- Error handling
- Session management
- Auto-logout

Requirements:
- Secure authentication
- Data protection
- User privacy
- Error handling
- Session management
```

---

## Testing & Deployment

### Backend Testing

**Prompt for Backend Testing:**
```
Create comprehensive backend tests:
- Unit tests for services
- Integration tests for controllers
- Repository tests
- Security tests
- API endpoint tests
- Database tests
- Performance tests

Requirements:
- Test coverage > 80%
- Mock external dependencies
- Test data management
- CI/CD integration
- Performance benchmarks
```

### Frontend Testing

**Prompt for Frontend Testing:**
```
Create comprehensive frontend tests:
- Component unit tests
- Integration tests
- User interaction tests
- API integration tests
- Responsive design tests
- Accessibility tests
- Performance tests

Requirements:
- Test coverage > 70%
- Mock API calls
- User interaction testing
- Responsive testing
- Accessibility compliance
```

### Deployment

**Prompt for Deployment Configuration:**
```
Create deployment configuration for:
- Backend deployment (Docker, Kubernetes)
- Frontend deployment (Vercel, Netlify)
- Database deployment
- Environment configuration
- CI/CD pipeline
- Monitoring setup
- Backup strategy
- SSL configuration

Requirements:
- Automated deployment
- Environment management
- Monitoring and logging
- Backup and recovery
- Security compliance
- Performance optimization
```

---

## Development Guidelines

### Code Quality
- Follow coding standards and best practices
- Implement proper error handling
- Use meaningful variable and function names
- Add comprehensive documentation
- Implement logging and monitoring
- Follow security best practices

### Performance
- Optimize database queries
- Implement caching strategies
- Use pagination for large datasets
- Optimize frontend bundle size
- Implement lazy loading
- Use CDN for static assets

### User Experience
- Implement responsive design
- Add loading states and feedback
- Provide clear error messages
- Implement keyboard navigation
- Ensure accessibility compliance
- Add helpful tooltips and guides

### Security
- Validate all inputs
- Implement proper authentication
- Use HTTPS in production
- Sanitize user data
- Implement rate limiting
- Regular security audits

This comprehensive prompt file provides detailed guidance for implementing all features of the Project Management System, ensuring consistency, quality, and completeness across both frontend and backend development. 