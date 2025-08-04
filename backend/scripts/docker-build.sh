#!/bin/bash

# Docker Build and Deploy Script for Project Management Backend
# Usage: ./scripts/docker-build.sh [dev|prod]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Build the Docker image
build_image() {
    local environment=$1
    print_status "Building Docker image for $environment environment..."
    
    if [ "$environment" = "prod" ]; then
        docker build -t project-management-backend:latest .
        print_success "Production image built successfully"
    else
        docker build -t project-management-backend:dev .
        print_success "Development image built successfully"
    fi
}

# Start services
start_services() {
    local environment=$1
    print_status "Starting services for $environment environment..."
    
    if [ "$environment" = "prod" ]; then
        # Check if .env file exists for production
        if [ ! -f .env ]; then
            print_warning "No .env file found. Creating sample .env file..."
            cat > .env << EOF
# Database Configuration
DB_USER=postgres
DB_PASSWORD=your_secure_password_here
JWT_SECRET=your-very-long-and-secure-jwt-secret-key-here
JWT_EXPIRATION=86400000
CORS_ORIGINS=http://localhost:5173,https://yourdomain.com
EOF
            print_warning "Please update the .env file with your actual values before running in production"
        fi
        
        docker-compose -f docker-compose.prod.yml up -d
        print_success "Production services started"
    else
        docker-compose up -d
        print_success "Development services started"
    fi
}

# Stop services
stop_services() {
    local environment=$1
    print_status "Stopping services for $environment environment..."
    
    if [ "$environment" = "prod" ]; then
        docker-compose -f docker-compose.prod.yml down
    else
        docker-compose down
    fi
    print_success "Services stopped"
}

# Show logs
show_logs() {
    local environment=$1
    local service=${2:-backend}
    
    print_status "Showing logs for $service service..."
    
    if [ "$environment" = "prod" ]; then
        docker-compose -f docker-compose.prod.yml logs -f $service
    else
        docker-compose logs -f $service
    fi
}

# Clean up
cleanup() {
    print_status "Cleaning up Docker resources..."
    
    # Stop and remove containers
    docker-compose down --remove-orphans 2>/dev/null || true
    docker-compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null || true
    
    # Remove unused images
    docker image prune -f
    
    # Remove unused volumes (be careful with this in production)
    read -p "Do you want to remove unused volumes? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker volume prune -f
    fi
    
    print_success "Cleanup completed"
}

# Show status
show_status() {
    local environment=$1
    print_status "Showing service status..."
    
    if [ "$environment" = "prod" ]; then
        docker-compose -f docker-compose.prod.yml ps
    else
        docker-compose ps
    fi
}

# Main script logic
main() {
    local environment=${1:-dev}
    local action=${2:-start}
    
    case $action in
        "build")
            check_docker
            build_image $environment
            ;;
        "start")
            check_docker
            start_services $environment
            ;;
        "stop")
            stop_services $environment
            ;;
        "restart")
            stop_services $environment
            sleep 2
            start_services $environment
            ;;
        "logs")
            show_logs $environment ${3:-backend}
            ;;
        "status")
            show_status $environment
            ;;
        "cleanup")
            cleanup
            ;;
        *)
            echo "Usage: $0 [dev|prod] [build|start|stop|restart|logs|status|cleanup] [service_name]"
            echo ""
            echo "Commands:"
            echo "  build    - Build Docker image"
            echo "  start    - Start services"
            echo "  stop     - Stop services"
            echo "  restart  - Restart services"
            echo "  logs     - Show logs (optionally specify service name)"
            echo "  status   - Show service status"
            echo "  cleanup  - Clean up Docker resources"
            echo ""
            echo "Examples:"
            echo "  $0 dev start"
            echo "  $0 prod build"
            echo "  $0 dev logs postgres"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@" 