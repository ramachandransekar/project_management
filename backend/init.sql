-- Initialize Project Management Database
-- This script runs when the PostgreSQL container starts for the first time

-- Create database if it doesn't exist (handled by environment variables)
-- CREATE DATABASE IF NOT EXISTS project_management;

-- Connect to the database
\c project_management;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create any additional indexes or configurations
-- (Spring Boot JPA will handle table creation with ddl-auto=update)

-- Optional: Create a read-only user for monitoring
-- CREATE USER readonly WITH PASSWORD 'readonly_password';
-- GRANT CONNECT ON DATABASE project_management TO readonly;
-- GRANT USAGE ON SCHEMA public TO readonly;
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly;

-- Log successful initialization
DO $$
BEGIN
    RAISE NOTICE 'Project Management Database initialized successfully';
END $$; 