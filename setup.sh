#!/bin/bash

echo "🚀 Setting up Project Management System"
echo "========================================"

# Check if PostgreSQL is running
echo "📊 Checking PostgreSQL..."
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "❌ PostgreSQL is not running. Please start PostgreSQL first."
    echo "   You can start it with: sudo systemctl start postgresql"
    exit 1
fi
echo "✅ PostgreSQL is running"

# Create database if it doesn't exist
echo "🗄️  Setting up database..."
psql -h localhost -U postgres -c "CREATE DATABASE project_management;" 2>/dev/null || echo "Database already exists or connection failed"
echo "✅ Database setup complete"

# Setup Backend
echo "🔧 Setting up Backend..."
cd backend
if [ ! -d "target" ]; then
    echo "📦 Building backend with Maven..."
    mvn clean install -q
fi
echo "✅ Backend setup complete"
cd ..

# Setup Frontend
echo "🎨 Setting up Frontend..."
cd frontend
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install -q
fi
echo "✅ Frontend setup complete"
cd ..

echo ""
echo "🎉 Setup complete!"
echo ""
echo "To start the application:"
echo "1. Start the backend: cd backend && mvn spring-boot:run"
echo "2. Start the frontend: cd frontend && npm run dev"
echo ""
echo "Backend will run on: http://localhost:8080"
echo "Frontend will run on: http://localhost:5173"
echo ""
echo "Happy coding! 🚀" 