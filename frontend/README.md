# Project Management Frontend

A React application built with Vite for the project management system.

## Prerequisites

- Node.js 16 or higher
- npm or yarn

## Features

- Modern React with Hooks
- JWT Authentication
- Responsive Design
- Form Validation
- Error Handling
- Loading States

## Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

## Running the Application

1. Start the development server:
   ```bash
   npm run dev
   ```

2. Open your browser and navigate to `http://localhost:5173`

## Build for Production

1. Build the application:
   ```bash
   npm run build
   ```

2. Preview the production build:
   ```bash
   npm run preview
   ```

## Project Structure

```
src/
├── components/          # React components
│   ├── AuthForm.jsx    # Login/Signup form
│   └── Dashboard.jsx   # Dashboard component
├── context/            # React context
│   └── AuthContext.jsx # Authentication context
├── App.jsx             # Main app component
├── main.jsx            # Application entry point
└── index.css           # Global styles
```

## Features

### Authentication
- Toggle between Login and Signup forms
- Form validation
- Error handling
- Success messages
- Automatic redirection after successful authentication

### Dashboard
- Protected route
- User information display
- Logout functionality
- Responsive design

### Styling
- Modern gradient background
- Glass morphism design
- Smooth animations
- Mobile responsive

## API Integration

The frontend communicates with the backend API at `http://localhost:8080`:
- Authentication endpoints
- JWT token management
- Automatic token inclusion in requests

## Environment Variables

You can configure the API URL by creating a `.env` file:
```
VITE_API_URL=http://localhost:8080
``` 