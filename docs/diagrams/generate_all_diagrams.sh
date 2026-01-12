#!/bin/bash

# Script to generate all PlantUML diagrams
# Requires PlantUML to be installed locally
# Download from: https://plantuml.com/download

echo "Generating PlantUML diagrams..."

# Create images directory if it doesn't exist
mkdir -p images

# Check if PlantUML is available
if ! command -v plantuml &> /dev/null; then
    echo "PlantUML not found. Please install PlantUML first."
    echo "Download from: https://plantuml.com/download"
    echo ""
    echo "Or use the online server: https://www.plantuml.com/plantuml/uml/"
    exit 1
fi

# Generate all diagrams
for file in *.puml; do
    if [ -f "$file" ]; then
        echo "Generating diagram from $file..."
        plantuml -tpng -o images "$file"
        echo "✓ Generated: images/${file%.puml}.png"
    fi
done

echo ""
echo "All diagrams generated successfully!"
echo "Check the 'images' directory for PNG files."





