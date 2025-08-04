# Docker Setup for Project Management Backend

This document provides instructions for running the Project Management backend using Docker.

## Prerequisites

- Docker (version 20.10 or higher)
- Docker Compose (version 2.0 or higher)
- At least 4GB of available RAM
- At least 10GB of available disk space

## Quick Start

### Development Environment

1. **Clone and navigate to the backend directory:**
   ```bash
   cd backend
   ```

2. **Start the development environment:**
   ```bash
   ./scripts/docker-build.sh dev start
   ```

3. **Check service status:**
   ```bash
   ./scripts/docker-build.sh dev status
   ```

4. **View logs:**
   ```bash
   ./scripts/docker-build.sh dev logs backend
   ```

### Production Environment

1. **Build the production image:**
   ```bash
   ./scripts/docker-build.sh prod build
   ```

2. **Configure environment variables:**
   ```bash
   # Edit the .env file with your production values
   nano .env
   ```

3. **Start production services:**
   ```bash
   ./scripts/docker-build.sh prod start
   ```

## Manual Docker Commands

### Development

```bash
# Build and start all services
docker-compose up -d

# Build and start with rebuild
docker-compose up -d --build

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Production

```bash
# Build production image
docker build -t project-management-backend:latest .

# Start production services
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f backend

# Stop services
docker-compose -f docker-compose.prod.yml down
```

## Services

### Development Environment

- **Backend**: Spring Boot application (port 8080)
- **PostgreSQL**: Database (port 5432)
- **Redis**: Caching (port 6379)

### Production Environment

- **Backend**: Spring Boot application (port 8080)
- **PostgreSQL**: Database (port 5432)
- **Redis**: Caching (port 6379)
- **Nginx**: Reverse proxy (ports 80, 443)

## Environment Variables

### Development (.env file)

```env
# Database Configuration
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-secret-key-here-make-it-very-long-and-secure-in-production
JWT_EXPIRATION=86400000
CORS_ORIGINS=http://localhost:5173
```

### Production (.env file)

```env
# Database Configuration
DB_USER=your_db_user
DB_PASSWORD=your_secure_password
JWT_SECRET=your-very-long-and-secure-jwt-secret-key-here
JWT_EXPIRATION=86400000
CORS_ORIGINS=http://localhost:5173,https://yourdomain.com
```

## Database

### Initialization

The database is automatically initialized with:
- Database creation
- User setup
- UUID extension installation

### Data Persistence

- **Development**: Data is stored in Docker volumes
- **Production**: Data is stored in Docker volumes with backup recommendations

### Backup

```bash
# Create backup
docker exec project_management_db pg_dump -U postgres project_management > backup.sql

# Restore backup
docker exec -i project_management_db psql -U postgres project_management < backup.sql
```

## Monitoring and Health Checks

### Health Check Endpoints

- **Backend**: `http://project-management-1-kkb0.onrender.com/api/auth/test`
- **Database**: PostgreSQL health check
- **Redis**: Redis ping command

### Logs

```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs postgres
docker-compose logs redis

# Follow logs in real-time
docker-compose logs -f backend
```

## Troubleshooting

### Common Issues

1. **Port already in use:**
   ```bash
   # Check what's using the port
   sudo netstat -tulpn | grep :8080
   
   # Stop conflicting service or change port in docker-compose.yml
   ```

2. **Database connection issues:**
   ```bash
   # Check database status
   docker-compose logs postgres
   
   # Restart database
   docker-compose restart postgres
   ```

3. **Memory issues:**
   ```bash
   # Check Docker resource usage
   docker stats
   
   # Increase Docker memory limit in Docker Desktop settings
   ```

4. **Permission issues:**
   ```bash
   # Fix uploads directory permissions
   sudo chown -R $USER:$USER uploads/
   ```

### Cleanup

```bash
# Remove all containers and volumes
./scripts/docker-build.sh cleanup

# Remove specific images
docker rmi project-management-backend:latest
docker rmi project-management-backend:dev

# Remove unused resources
docker system prune -a
```

## Performance Optimization

### Development

- Use volume mounts for hot reloading
- Enable debug logging
- Use development database settings

### Production

- Use production JVM settings
- Enable connection pooling
- Use Redis for caching
- Configure proper logging levels
- Set resource limits

## Security Considerations

### Development

- Use default passwords (not for production)
- Enable CORS for local development
- Use development JWT secrets

### Production

- Use strong, unique passwords
- Configure proper CORS origins
- Use secure JWT secrets
- Enable HTTPS with Nginx
- Regular security updates
- Database access restrictions

## Deployment

### Local Development

```bash
# Start development environment
./scripts/docker-build.sh dev start

# Access application
# Backend: http://localhost:8080
# Database: localhost:5432
```

### Production Deployment

```bash
# Build production image
./scripts/docker-build.sh prod build

# Configure environment
cp .env.example .env
# Edit .env with production values

# Deploy
./scripts/docker-build.sh prod start

# Monitor deployment
./scripts/docker-build.sh prod status
./scripts/docker-build.sh prod logs backend
```

## Maintenance

### Regular Tasks

1. **Update dependencies:**
   ```bash
   # Update base images
   docker-compose pull
   ```

2. **Backup database:**
   ```bash
   # Create backup
   docker exec project_management_db pg_dump -U postgres project_management > backup_$(date +%Y%m%d_%H%M%S).sql
   ```

3. **Monitor logs:**
   ```bash
   # Check for errors
   docker-compose logs --tail=100 backend | grep ERROR
   ```

4. **Clean up old images:**
   ```bash
   docker image prune -f
   ```

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review Docker and application logs
3. Verify environment configuration
4. Check system resources 