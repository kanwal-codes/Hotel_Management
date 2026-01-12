# Milestone 1 Submission Guide - Complete Checklist

**Due Date: November 12th, 2025**  
**Weight: 5%**  
**Presentation Time: 3-5 minutes**

---

## 📋 Pre-Submission Checklist

### ✅ Documents Created
- [x] PROJECT_BLUEPRINT.md - Complete project documentation
- [x] DATABASE_DESIGN.md - ER diagram and schema
- [x] UML_DIAGRAMS_SUMMARY.md - All UML diagrams specifications
- [x] UI_DESIGN_SPECIFICATIONS.md - All UI screens described
- [x] MILESTONE1_SUBMISSION_CHECKLIST.md - Initial checklist
- [x] MILESTONE1_SUBMISSION_GUIDE.md - This document

---

## 📦 What You Need to Create/Prepare

### 1. Visual UML Diagrams (Create using UML tool)

**Required Diagrams:**

#### A. Architecture/Deployment Diagram
- [ ] Create visual 4-layer architecture diagram
- [ ] Show: User Desktop Layer, Application Layer, Persistence Layer, File System Layer
- [ ] Label all components clearly
- [ ] Show connections between layers

**Reference:** See UML_DIAGRAMS_SUMMARY.md Section 1

#### B. Package Diagram
- [ ] Create visual package diagram showing `com.hotel` structure
- [ ] Show all 10 packages: app, config, controller, view, model, repository, service, security, util, events
- [ ] Show dependencies between packages
- [ ] Label each package

**Reference:** See UML_DIAGRAMS_SUMMARY.md Section 2

#### C. Class Diagram
- [ ] Create complete class diagram with all entities
- [ ] Show all relationships (1:N, N:M, 1:1)
- [ ] Include all attributes for key classes
- [ ] Show design patterns (Strategy, Observer, Factory, Decorator, Singleton)
- [ ] Label relationships clearly

**Reference:** See PROJECT_BLUEPRINT.md "UML Class Diagram - Detailed Class Structure"

#### D. Sequence Diagrams (6 scenarios)
- [ ] Group Booking at Kiosk
- [ ] Deposit Payment at Admin
- [ ] Partial Payment During Stay
- [ ] Checkout with Discount + Loyalty Redemption
- [ ] Feedback Submission
- [ ] Waitlist Management (3 parts)

**Reference:** See PROJECT_BLUEPRINT.md "Sequence Diagrams" section

#### E. State Diagrams (Optional but Recommended)
- [ ] Reservation Status State Machine
- [ ] Room Status State Machine

**Reference:** See UML_DIAGRAMS_SUMMARY.md Section 5

---

### 2. Database Design Documents

#### A. ER Diagram
- [ ] Create visual Entity Relationship Diagram
- [ ] Show all 13 tables/entities
- [ ] Show all relationships (1:N, N:M, 1:1)
- [ ] Label cardinalities
- [ ] Show primary keys and foreign keys

**Reference:** See DATABASE_DESIGN.md

#### B. Database Schema
- [ ] SQL script file (`hotel_reservation_schema.sql`)
- [ ] All CREATE TABLE statements
- [ ] All indexes
- [ ] All constraints
- [ ] Sample data (optional)

**Reference:** See DATABASE_DESIGN.md for complete schema

---

### 3. UI Screenshots/Mockups

#### A. Kiosk UI Screenshots (9 screens)
- [ ] Welcome Screen
- [ ] Occupancy Selection
- [ ] Date Selection
- [ ] Room Selection (Suggested Plan)
- [ ] Room Selection (Custom Selection)
- [ ] Guest Details
- [ ] Add-On Services
- [ ] Booking Summary
- [ ] Confirmation Screen

**Reference:** See UI_DESIGN_SPECIFICATIONS.md Section 1

#### B. Admin UI Screenshots (10 screens)
- [ ] Login Screen
- [ ] Admin Dashboard
- [ ] Reservation Details/Edit
- [ ] Payment Processing
- [ ] Checkout Screen
- [ ] Discount Application
- [ ] Waitlist Management
- [ ] Loyalty Program
- [ ] Feedback Management
- [ ] Reports

**Reference:** See UI_DESIGN_SPECIFICATIONS.md Section 2

#### C. Feedback UI Screenshots (2 screens)
- [ ] Feedback Submission
- [ ] Feedback Confirmation

**Reference:** See UI_DESIGN_SPECIFICATIONS.md Section 3

**Total: 21 screenshots required**

---

## 🛠️ Tools You Can Use

### UML Diagram Tools
- **Draw.io (diagrams.net)** - Free, web-based, recommended
- **Lucidchart** - Professional, free tier available
- **PlantUML** - Text-based, good for version control
- **Visual Paradigm** - Professional UML tool
- **StarUML** - Free UML tool

### UI Mockup Tools
- **Figma** - Free, web-based, collaborative (recommended)
- **Adobe XD** - Professional, free tier
- **Balsamiq** - Quick wireframes
- **Draw.io** - Simple mockups
- **JavaFX Scene Builder** - For actual FXML (later)

### Database Design Tools
- **Draw.io** - ER diagrams
- **dbdiagram.io** - Database diagrams
- **MySQL Workbench** - ER diagrams
- **Lucidchart** - ER diagrams

---

## 📁 Submission Folder Structure

Create the following folder structure:

```
Milestone1_Submission/
│
├── 1_UML_Diagrams/
│   ├── Architecture_Diagram.png/pdf
│   ├── Package_Diagram.png/pdf
│   ├── Class_Diagram.png/pdf
│   ├── Sequence_Diagrams.pdf (all 6 scenarios)
│   └── State_Diagrams.png/pdf (optional)
│
├── 2_Database_Design/
│   ├── ER_Diagram.png/pdf
│   ├── hotel_reservation_schema.sql
│   └── Database_Design_Document.pdf (from DATABASE_DESIGN.md)
│
├── 3_UI_Designs/
│   ├── Kiosk_UI/
│   │   ├── 01_Welcome_Screen.png
│   │   ├── 02_Occupancy_Selection.png
│   │   ├── 03_Date_Selection.png
│   │   ├── 04_Room_Selection_Suggested.png
│   │   ├── 05_Room_Selection_Custom.png
│   │   ├── 06_Guest_Details.png
│   │   ├── 07_AddOn_Services.png
│   │   ├── 08_Booking_Summary.png
│   │   └── 09_Confirmation.png
│   │
│   ├── Admin_UI/
│   │   ├── 01_Login.png
│   │   ├── 02_Dashboard.png
│   │   ├── 03_Reservation_Details.png
│   │   ├── 04_Payment_Processing.png
│   │   ├── 05_Checkout.png
│   │   ├── 06_Discount_Application.png
│   │   ├── 07_Waitlist_Management.png
│   │   ├── 08_Loyalty_Program.png
│   │   ├── 09_Feedback_Management.png
│   │   └── 10_Reports.png
│   │
│   └── Feedback_UI/
│       ├── 01_Feedback_Submission.png
│       └── 02_Feedback_Confirmation.png
│
└── 4_Combined_Document.pdf
    (Optional: All diagrams and screenshots in one PDF)
```

---

## 🎯 Presentation Script (3-5 minutes)

### Introduction (30 seconds)
- "I'm presenting the design for the Hotel Reservation System"
- "This includes UML diagrams, database design, and UI mockups"

### Architecture Overview (1 minute)
- Show Architecture Diagram
- Explain 4-layer structure
- Mention JavaFX, JPA, BCrypt

### Package Structure (30 seconds)
- Show Package Diagram
- Explain 10 packages and their purposes

### Class Diagram (1 minute)
- Show Class Diagram
- Highlight key entities (Guest, Reservation, Room, Billing)
- Point out design patterns (Strategy, Observer, Factory, Decorator, Singleton)

### Sequence Diagrams (1 minute)
- Show 1-2 key sequence diagrams
- Explain the flow (e.g., Booking flow, Checkout flow)
- Mention Observer pattern for waitlist

### Database Design (30 seconds)
- Show ER Diagram
- Explain key relationships
- Mention 13 tables

### UI Design (1 minute)
- Walk through Kiosk UI screens (key screens)
- Show Admin Dashboard
- Highlight key features (validation, search, reports)

### Conclusion (30 seconds)
- Summarize key design decisions
- Mention patterns used
- Ready for implementation

**Total: ~5 minutes**

---

## ✅ Final Checklist Before Submission

### Diagrams
- [ ] All UML diagrams are clear and readable
- [ ] All relationships are labeled
- [ ] Design patterns are clearly indicated
- [ ] Diagrams match the documented architecture

### Database
- [ ] ER diagram shows all entities and relationships
- [ ] SQL schema file is complete and valid
- [ ] All constraints are included

### UI Screenshots
- [ ] All 21 screenshots are included
- [ ] Screenshots are clear and high resolution
- [ ] Screenshots are properly labeled
- [ ] UI flow is logical and complete

### Documentation
- [ ] All documents are complete
- [ ] File names are clear and consistent
- [ ] Folder structure is organized

### Lab Approval
- [ ] All designs shown in lab
- [ ] Got approval from instructor
- [ ] Made any requested changes

### Presentation
- [ ] Presentation script prepared
- [ ] Can explain all diagrams clearly
- [ ] Can answer questions about design
- [ ] Presentation fits in 3-5 minutes

---

## 📝 Submission Format

### Option 1: Separate Files
- Submit as ZIP file containing:
  - UML_Diagrams folder
  - Database_Design folder
  - UI_Designs folder

### Option 2: Combined PDF
- Create one PDF with:
  - Table of contents
  - All UML diagrams
  - Database design
  - All UI screenshots
  - Organized by sections

**Check submission platform requirements!**

---

## 🚨 Important Reminders

1. **Lab Approval Required**: Only submissions approved during lab will be accepted
2. **Mandatory Attendance**: Must attend the lab session
3. **Early Presentation**: Can show designs in earlier labs
4. **3-5 Minutes**: Keep presentation concise
5. **Be Prepared**: Know your design inside and out

---

## 💡 Tips for Success

1. **Start Early**: Don't wait until the last minute
2. **Use Consistent Tools**: Stick to one tool for each type of diagram
3. **Get Feedback**: Show designs to classmates/instructor early
4. **Practice Presentation**: Rehearse your 3-5 minute presentation
5. **Be Clear**: Use clear labels and consistent naming
6. **Show Patterns**: Clearly indicate where design patterns are used
7. **Explain Flow**: Be ready to explain user flows and system flows

---

## 📚 Reference Documents

All information is documented in:
- `PROJECT_BLUEPRINT.md` - Complete project documentation
- `DATABASE_DESIGN.md` - Database schema and ER diagram
- `UML_DIAGRAMS_SUMMARY.md` - UML diagrams specifications
- `UI_DESIGN_SPECIFICATIONS.md` - UI screen descriptions

---

**Good Luck with Milestone 1! 🎉**

You have all the information you need. Now create the visual diagrams and UI mockups based on these specifications.

