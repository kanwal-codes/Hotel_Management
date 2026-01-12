# How to Generate All PlantUML Diagrams

This guide will help you generate all UML diagrams for the project documentation using the PlantUML Online Server.

## Quick Start

1. **Visit PlantUML Online Server**: https://www.plantuml.com/plantuml/uml/
2. **For each diagram file** in `docs/diagrams/`:
   - Open the `.puml` file
   - Copy all contents
   - Paste into the PlantUML editor
   - Click "Submit"
   - Download as PNG or SVG
   - Save to `docs/diagrams/images/` folder

## Available Diagrams

All PlantUML source files are in `docs/diagrams/`:

1. **ClassDiagram.puml** - Complete class diagram with all entities, services, and relationships
2. **SequenceDiagram_Booking.puml** - Booking flow sequence diagram
3. **SequenceDiagram_Payment.puml** - Payment processing sequence diagram
4. **SequenceDiagram_Observer.puml** - Observer pattern implementation
5. **DeploymentDiagram.puml** - System deployment architecture
6. **PackageDiagram.puml** - Package structure and dependencies
7. **ComponentDiagram.puml** - Component architecture diagram
8. **ActivityDiagram_Booking.puml** - Booking process activity flow
9. **UseCaseDiagram.puml** - System use cases

## Step-by-Step Instructions

### Method 1: Using PlantUML Online Server (Recommended)

1. Open your browser and go to: **https://www.plantuml.com/plantuml/uml/**

2. For each diagram:
   - Open the corresponding `.puml` file from `docs/diagrams/`
   - Select all text (Ctrl+A / Cmd+A)
   - Copy (Ctrl+C / Cmd+C)
   - Go back to PlantUML online server
   - Click in the text area
   - Select all existing text (Ctrl+A / Cmd+A)
   - Paste your diagram code (Ctrl+V / Cmd+V)
   - Click "Submit" button
   - Wait for diagram to render
   - Right-click on the diagram image → "Save image as..." → Save as PNG
   - Or click "PNG" link to download

3. Save all diagrams to: `docs/diagrams/images/` folder with descriptive names:
   - `ClassDiagram.png`
   - `SequenceDiagram_Booking.png`
   - `SequenceDiagram_Payment.png`
   - `SequenceDiagram_Observer.png`
   - `DeploymentDiagram.png`
   - `PackageDiagram.png`
   - `ComponentDiagram.png`
   - `ActivityDiagram_Booking.png`
   - `UseCaseDiagram.png`

### Method 2: Using PlantUML Locally

If you have PlantUML installed locally:

```bash
# Install PlantUML (requires Java)
# Download from: https://plantuml.com/download

# Generate all diagrams
cd docs/diagrams
for file in *.puml; do
    java -jar plantuml.jar "$file" -o images
done
```

### Method 3: Using VS Code Extension

1. Install "PlantUML" extension in VS Code
2. Open any `.puml` file
3. Press `Alt+D` to preview
4. Right-click on preview → Export diagram → Choose format (PNG/SVG)

## Including Diagrams in Documentation

After generating the diagrams, update `ProjectDocumentation.md` to reference the images:

```markdown
### 3.1 Class Diagram

![Class Diagram](docs/diagrams/images/ClassDiagram.png)

*Complete class diagram showing all entities, services, repositories, and their relationships*
```

## Diagram Descriptions

### Class Diagram
- Shows all entity models with attributes
- Service classes and their dependencies
- Repository interfaces
- Design pattern implementations
- Relationships between all classes

### Sequence Diagrams
- **Booking**: Complete guest booking flow from date selection to confirmation
- **Payment**: Payment processing with loyalty points redemption
- **Observer**: Waitlist notification mechanism using Observer pattern

### Deployment Diagram
- Desktop application components
- Database connections
- File system interactions
- System architecture layers

### Package Diagram
- Complete package structure
- Dependencies between packages
- Layer organization

### Component Diagram
- System components by layer
- Cross-cutting concerns
- Component interactions

### Activity Diagram
- Booking process flow
- Decision points
- Error handling paths

### Use Case Diagram
- Guest use cases
- Admin use cases
- Manager use cases
- Role-based permissions

## Tips

1. **Theme Selection**: You can change themes in PlantUML online server using the theme dropdown
2. **Export Formats**: 
   - PNG for documentation (recommended)
   - SVG for scalable graphics
   - PDF for high-quality printing
3. **Layout**: If diagram looks cluttered, try different layout engines in PlantUML
4. **Size**: Large diagrams may need to be split or use smaller font sizes

## Troubleshooting

- **Diagram too large**: Split into multiple diagrams or use smaller fonts
- **Relationships not showing**: Check PlantUML syntax, ensure proper arrow notation
- **Layout issues**: Try different themes or layout engines
- **Missing classes**: Verify all classes are defined in the PlantUML file

## Next Steps

After generating all diagrams:
1. Save all PNG files to `docs/diagrams/images/`
2. Update `ProjectDocumentation.md` to include image references
3. Verify all diagrams are readable and properly formatted
4. Include diagrams in final PDF documentation





