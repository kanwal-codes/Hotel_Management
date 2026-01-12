#!/usr/bin/env python3
"""
Script to generate all PlantUML diagrams using PlantUML Online Server API
This script reads .puml files, encodes them, and downloads PNG images
"""

import os
import sys
import zlib
import base64
import urllib.request
import urllib.parse
from pathlib import Path

def encode_plantuml(text):
    """
    Encode PlantUML text using deflate compression and base64 encoding
    This is the encoding format used by PlantUML server URLs
    PlantUML requires ~1 prefix for deflate encoding
    """
    # Compress using deflate (level 9 for best compression)
    compressed = zlib.compress(text.encode('utf-8'), level=9)
    # Base64 encode
    encoded = base64.b64encode(compressed).decode('ascii')
    # Replace characters for URL safety (PlantUML format)
    encoded = encoded.replace('+', '-').replace('/', '_')
    # Add ~1 prefix to indicate deflate encoding
    return '~1' + encoded

def generate_diagram(puml_file, output_dir):
    """
    Generate a PNG diagram from a PlantUML file using PlantUML server
    """
    print(f"Processing: {puml_file.name}...")
    
    # Read the PlantUML file
    with open(puml_file, 'r', encoding='utf-8') as f:
        puml_text = f.read()
    
    # Encode the text
    encoded = encode_plantuml(puml_text)
    
    # Construct the PlantUML server URL
    # Format: https://www.plantuml.com/plantuml/png/{encoded}
    url = f"https://www.plantuml.com/plantuml/png/{encoded}"
    
    # Generate output filename
    output_filename = puml_file.stem + ".png"
    output_path = output_dir / output_filename
    
    try:
        # Download the PNG
        print(f"  Downloading from PlantUML server...")
        urllib.request.urlretrieve(url, output_path)
        print(f"  ✓ Saved: {output_path}")
        return True
    except Exception as e:
        print(f"  ✗ Error generating {puml_file.name}: {e}")
        return False

def main():
    # Get the directory where this script is located
    script_dir = Path(__file__).parent
    output_dir = script_dir / "images"
    
    # Create images directory if it doesn't exist
    output_dir.mkdir(exist_ok=True)
    
    print("=" * 60)
    print("PlantUML Diagram Generator")
    print("Using PlantUML Online Server API")
    print("=" * 60)
    print()
    
    # Find all .puml files
    puml_files = list(script_dir.glob("*.puml"))
    
    if not puml_files:
        print("No .puml files found in current directory!")
        return
    
    print(f"Found {len(puml_files)} PlantUML file(s):")
    for f in puml_files:
        print(f"  - {f.name}")
    print()
    
    # Generate each diagram
    success_count = 0
    for puml_file in sorted(puml_files):
        if generate_diagram(puml_file, output_dir):
            success_count += 1
        print()
    
    print("=" * 60)
    print(f"Generation complete: {success_count}/{len(puml_files)} diagrams generated")
    print(f"Output directory: {output_dir}")
    print("=" * 60)

if __name__ == "__main__":
    main()

