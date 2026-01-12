# PlantUML Diagrams for Hotel Reservation System

This directory contains PlantUML source files for all UML diagrams used in the project documentation.

## Available Diagrams

1. **ClassDiagram.puml** - Complete class diagram showing all entities, services, repositories, and their relationships
2. **SequenceDiagram_Booking.puml** - Sequence diagram for the booking flow
3. **SequenceDiagram_Payment.puml** - Sequence diagram for payment processing
4. **SequenceDiagram_Observer.puml** - Sequence diagram showing Observer pattern implementation
5. **DeploymentDiagram.puml** - Deployment diagram showing system architecture
6. **PackageDiagram.puml** - Package structure and dependencies
7. **ComponentDiagram.puml** - Component diagram showing system layers
8. **ActivityDiagram_Booking.puml** - Activity diagram for booking process
9. **UseCaseDiagram.puml** - Use case diagram for system actors

## How to Generate Diagrams

### Option 1: Online (Recommended)
1. Visit https://plantuml.com/
2. Click on "Online Server" or use the online editor
3. Copy the contents of any `.puml` file
4. Paste into the editor
5. Click "Submit" to generate the diagram
6. Download as PNG, SVG, or PDF

### Option 2: Local Installation
1. Install PlantUML: https://plantuml.com/starting
2. Install Graphviz (required for layout): https://graphviz.org/download/
3. Run from command line:
   ```bash
   java -jar plantuml.jar docs/diagrams/ClassDiagram.puml
   ```

### Option 3: VS Code Extension
1. Install "PlantUML" extension in VS Code
2. Open any `.puml` file
3. Press `Alt+D` to preview
4. Right-click to export as PNG/SVG

## Including in Documentation

After generating the diagrams, you can:
- Include PNG images in your documentation
- Embed SVG for scalable graphics
- Export to PDF for professional presentation

## Diagram Descriptions

### Class Diagram
Shows the complete object model including:
- Entity relationships (One-to-Many, Many-to-One, One-to-One)
- Service classes and their dependencies
- Design pattern implementations
- Repository interfaces

### Sequence Diagrams
Illustrate interaction flows:
- **Booking**: Complete guest booking process
- **Payment**: Payment processing with loyalty points
- **Observer**: Waitlist notification mechanism

### Deployment Diagram
Shows the physical deployment:
- Desktop application components
- Database connections
- File system interactions

### Package Diagram
Displays package organization:
- Package structure
- Dependencies between packages
- Layer separation

### Component Diagram
Shows system components:
- Presentation layer
- Application layer
- Data layer
- Cross-cutting concerns

### Activity Diagram
Flowchart of booking process:
- Decision points
- Process steps
- Error handling

### Use Case Diagram
System use cases:
- Guest use cases
- Admin use cases
- Manager use cases
- Role-based permissions





