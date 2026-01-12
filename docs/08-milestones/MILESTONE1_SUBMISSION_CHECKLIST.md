# Milestone 1 Submission Checklist
**Due Date: November 12th, 2025**  
**Weight: 5%**

---

## 📋 Submission Requirements

### ✅ What You Need to Submit:

1. **Front-End Designs (Screenshots)**
   - [ ] Kiosk UI screenshots (all screens)
   - [ ] Admin UI screenshots (all screens)
   - [ ] Feedback UI screenshots (all screens)
   - [ ] Welcome screen
   - [ ] Booking flow screens
   - [ ] Dashboard screens
   - [ ] Login screen
   - [ ] All other UI screens

2. **UML Diagrams**
   - [ ] **Class Diagram** (complete with all entities, services, repositories, patterns)
   - [ ] **Sequence Diagrams**:
     - [ ] Group Booking at Kiosk
     - [ ] Deposit Payment at Admin
     - [ ] Partial Payment During Stay
     - [ ] Checkout with Discount + Loyalty Redemption
     - [ ] Feedback Submission
     - [ ] Waitlist Management (3 scenarios)
   - [ ] **Package Diagram** (showing 10 packages: app, config, controller, view, model, repository, service, security, util, events)
   - [ ] **Deployment/Architecture Diagram** (4-layer structure)

3. **Database Design**
   - [ ] **ER Diagram** (Entity Relationship Diagram)
   - [ ] **Database Schema** (tables, relationships, constraints)
   - [ ] **Table Descriptions** (all entities and their attributes)

4. **Other Supporting Diagrams**
   - [ ] **Data Flow Diagram** (if created)
   - [ ] **Component Diagram** (if created)
   - [ ] **State Diagram** (for Reservation status, Room status, etc.)
   - [ ] **Activity Diagram** (for key workflows, if created)

---

## 📝 Presentation Requirements (3-5 minutes)

### What to Present:
- [ ] Final front-end designs (walk through UI screens)
- [ ] Class diagram (explain structure)
- [ ] Sequence diagrams (explain key flows)
- [ ] Database design (explain entities and relationships)
- [ ] Any other diagrams that support project structure and data flow

---

## 📦 Submission Format

### Required Files:
- [ ] **PDF or DOCX** containing all diagrams
- [ ] **Separate folder** with all UI screenshots (organized by module)
- [ ] **Database design document** (ER diagram + schema)

### File Organization Suggestion:
```
Milestone1_Submission/
├── UML_Diagrams.pdf
│   ├── Class Diagram
│   ├── Sequence Diagrams (all scenarios)
│   ├── Package Diagram
│   └── Architecture Diagram
├── FrontEnd_Screenshots/
│   ├── Kiosk_UI/
│   ├── Admin_UI/
│   └── Feedback_UI/
├── Database_Design.pdf
│   ├── ER Diagram
│   ├── Database Schema
│   └── Table Descriptions
└── Other_Diagrams.pdf (if applicable)
```

---

## ⚠️ Important Notes

1. **Lab Approval Required**: Only submissions approved during lab time will be accepted
2. **Mandatory Attendance**: Attendance is mandatory for this milestone
3. **Early Presentation**: You can show your design in earlier labs as well
4. **3-5 Minutes**: Keep presentation concise and focused

---

## ✅ Pre-Submission Checklist

Before submitting, ensure:
- [ ] All diagrams are clear and readable
- [ ] All UI screenshots are labeled
- [ ] Diagrams match the approved designs from lab
- [ ] All required components are included
- [ ] Files are properly named and organized
- [ ] Submission format matches requirements

---

## 📊 What Should Be Covered in Diagrams

### Class Diagram Should Show:
- ✅ All entities (Guest, Room, Reservation, Billing, Payment, etc.)
- ✅ All services (ReservationService, BillingService, etc.)
- ✅ All repositories (GuestRepository, RoomRepository, etc.)
- ✅ Design patterns (Strategy, Observer, Factory, Decorator, Singleton)
- ✅ Relationships between classes
- ✅ Enumerations (RoomType, ReservationStatus, etc.)

### Sequence Diagrams Should Show:
- ✅ Actor interactions
- ✅ Controller → Service → Repository flow
- ✅ Database interactions
- ✅ Observer pattern implementation (for waitlist)
- ✅ Complete flow from start to finish

### Database Design Should Show:
- ✅ All tables/entities
- ✅ Primary keys and foreign keys
- ✅ Relationships (one-to-many, many-to-many)
- ✅ Constraints and validations
- ✅ Indexes (if applicable)

---

## 🎯 Quick Reference: What We Have Documented

Based on PROJECT_BLUEPRINT.md, you have:

1. ✅ **Architecture Diagram** (4-layer structure)
2. ✅ **Package Structure** (10 packages documented)
3. ✅ **Complete Class Diagram** (all entities, services, repositories)
4. ✅ **Sequence Diagrams** (6 scenarios documented)
5. ✅ **Business Rules** (occupancy, pricing, discounts, etc.)

**Next Steps:**
1. Create visual diagrams from the documented information
2. Design UI mockups/screenshots
3. Create ER diagram for database
4. Organize everything for submission
5. Get lab approval before final submission

---

**Good Luck! 🚀**

