#!/bin/bash

# Quick Database Setup Script for Hotel Reservation System
# This script helps set up MySQL database

echo "=========================================="
echo "Hotel Reservation System - Database Setup"
echo "=========================================="
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed."
    echo ""
    echo "To install MySQL:"
    echo "  1. Install Homebrew (if not installed):"
    echo "     /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
    echo ""
    echo "  2. Install MySQL:"
    echo "     brew install mysql"
    echo ""
    echo "  3. Start MySQL:"
    echo "     brew services start mysql"
    echo ""
    echo "  4. Then run this script again."
    exit 1
fi

echo "✅ MySQL is installed"
echo ""

# Check if MySQL is running
if ! pgrep -x mysqld > /dev/null; then
    echo "⚠️  MySQL is not running."
    echo ""
    echo "Starting MySQL..."
    if command -v brew &> /dev/null; then
        brew services start mysql
        sleep 3
    else
        echo "Please start MySQL manually and run this script again."
        exit 1
    fi
fi

echo "✅ MySQL is running"
echo ""

# Get MySQL root password
echo "Enter MySQL root password (press Enter if no password):"
read -s MYSQL_PASSWORD

if [ -z "$MYSQL_PASSWORD" ]; then
    MYSQL_CMD="mysql -u root"
else
    MYSQL_CMD="mysql -u root -p$MYSQL_PASSWORD"
fi

echo ""
echo "Creating database..."

# Create database
$MYSQL_CMD <<EOF
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;
SELECT 'Database hotel_db created successfully!' AS Status;
EOF

if [ $? -ne 0 ]; then
    echo "❌ Failed to create database. Please check your MySQL credentials."
    exit 1
fi

echo "✅ Database created"
echo ""

# Get project directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCHEMA_FILE="$SCRIPT_DIR/database/create_schema.sql"
SEED_FILE="$SCRIPT_DIR/database/seed_data.sql"

# Run schema script
if [ -f "$SCHEMA_FILE" ]; then
    echo "Running schema creation script..."
    $MYSQL_CMD hotel_db < "$SCHEMA_FILE"
    if [ $? -eq 0 ]; then
        echo "✅ Schema created successfully"
    else
        echo "❌ Failed to create schema"
        exit 1
    fi
else
    echo "❌ Schema file not found: $SCHEMA_FILE"
    exit 1
fi

echo ""

# Run seed data script
if [ -f "$SEED_FILE" ]; then
    echo "Running seed data script..."
    $MYSQL_CMD hotel_db < "$SEED_FILE"
    if [ $? -eq 0 ]; then
        echo "✅ Seed data loaded successfully"
    else
        echo "❌ Failed to load seed data"
        exit 1
    fi
else
    echo "❌ Seed data file not found: $SEED_FILE"
    exit 1
fi

echo ""

# Verify tables
echo "Verifying database setup..."
TABLE_COUNT=$($MYSQL_CMD hotel_db -e "SHOW TABLES;" 2>/dev/null | wc -l | tr -d ' ')
TABLE_COUNT=$((TABLE_COUNT - 1)) # Subtract header row

if [ "$TABLE_COUNT" -ge 10 ]; then
    echo "✅ Found $TABLE_COUNT tables in database"
else
    echo "⚠️  Expected 13 tables, found $TABLE_COUNT"
fi

echo ""
echo "=========================================="
echo "✅ Database setup complete!"
echo "=========================================="
echo ""
echo "Default admin credentials:"
echo "  Username: admin"
echo "  Password: admin123"
echo ""
echo "You can now run the application in IntelliJ!"
echo ""

