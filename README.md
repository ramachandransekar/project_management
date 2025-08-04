# Project Management System

A full-stack project management application with React frontend and Spring Boot backend, featuring JWT authentication and PostgreSQL database.

## 🚀 Features

- **Frontend**: Modern React application with Vite
- **Backend**: Spring Boot with JWT authentication
- **Database**: PostgreSQL integration
- **Authentication**: Secure JWT-based authentication
- **UI/UX**: Modern, responsive design with glass morphism effects

## 📋 Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## 🗄️ Database Setup

1. Install PostgreSQL
2. Create the database:
   ```sql
   CREATE DATABASE project_management;
   ```
3. The application will automatically create tables on startup

## 🛠️ Installation & Setup

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on `http://localhost:5173`

## 🔐 Authentication Flow

1. **Signup**: Users can create new accounts with username, email, and password
2. **Login**: Users can sign in with username/email and password
3. **JWT Tokens**: Secure authentication using JSON Web Tokens
4. **Protected Routes**: Dashboard is only accessible to authenticated users
5. **Auto-redirect**: Successful signup redirects to login, successful login redirects to dashboard

## 📁 Project Structure

```
project_management/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/projectmanagement/
│   │       ├── controller/  # REST controllers
│   │       ├── dto/         # Data transfer objects
│   │       ├── model/       # JPA entities
│   │       ├── repository/  # Data access layer
│   │       ├── security/    # JWT and security config
│   │       └── service/     # Business logic
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/                # React application
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── context/         # React context
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 🔧 Configuration

### Backend Configuration
- Database connection settings in `backend/src/main/resources/application.properties`
- JWT secret and expiration time
- CORS configuration for frontend integration

### Frontend Configuration
- API endpoint configuration in `frontend/src/context/AuthContext.jsx`
- Environment variables support for API URL

## 🧪 Testing

### Backend API Endpoints
- `POST /api/auth/signup` - User registration
- `POST /api/auth/signin` - User login
- `GET /api/test/all` - Public endpoint
- `GET /api/test/user` - Protected endpoint

### Frontend Routes
- `/` - Login/Signup page
- `/dashboard` - Protected dashboard page

## 🎨 UI Features

- **Modern Design**: Clean, professional interface
- **Responsive**: Works on desktop and mobile devices
- **Animations**: Smooth transitions and hover effects
- **Form Validation**: Real-time validation with error messages
- **Loading States**: Visual feedback during API calls

## 🔒 Security Features

- **Password Encryption**: BCrypt password hashing
- **JWT Authentication**: Stateless authentication
- **CORS Configuration**: Secure cross-origin requests
- **Input Validation**: Server-side validation
- **Protected Routes**: Client and server-side protection

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file: `mvn clean package`
2. Run the JAR: `java -jar target/auth-backend-1.0.0.jar`

### Frontend Deployment
1. Build the application: `npm run build`
2. Deploy the `dist` folder to your web server

## 📝 License

This project is open source and available under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📞 Support

For support and questions, please open an issue in the repository. 