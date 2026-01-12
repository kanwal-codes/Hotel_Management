# Video Demonstration Script - Hotel Reservation System

**Duration Target:** 7-10 minutes minimum  
**Format:** Screen recording with voiceover

---

## 🎬 Video Structure

### Introduction (1 minute)

**[Screen: Project running, show welcome screen]**

"Hello, I'm [Your Name], and today I'll be demonstrating the Hotel Reservation System I've developed for my Advanced Programming and Design course.

This is a complete desktop application built with JavaFX that replaces manual hotel reservation processes. The system includes:
- A self-service kiosk for guests to make bookings
- An admin module for staff to manage reservations and payments
- A feedback system for guest reviews
- Comprehensive reporting capabilities

The system uses a 3-tier architecture with JavaFX for the presentation layer, Java services for business logic, and JPA/Hibernate with MySQL for data persistence. I've implemented five design patterns: Strategy, Observer, Factory, Decorator, and Singleton.

Let me show you how it works."

---

### Part 1: Kiosk Module Demo (2-3 minutes)

**[Screen: Kiosk Welcome Screen]**

"Let's start with the kiosk module. This is the welcome screen where guests begin their booking journey."

**[Click: Start Booking]**

**[Screen: Guest Details Screen]**

"First, guests enter their details. Notice the inline validation - if I enter an invalid email, it shows an error immediately. The system validates phone numbers, names, and email addresses in real-time."

**[Enter valid guest details]**

**[Screen: Date Selection]**

"Next, guests select their check-in and check-out dates. The system validates that check-out is after check-in and that dates aren't in the past."

**[Select dates]**

**[Screen: Room Selection]**

"Now for room selection. For a group of 4 adults, the system suggests either one double room or two single rooms. This is based on our occupancy rules: single rooms hold 2 people, double rooms hold 4.

Guests can accept the suggestion or choose their own room types and quantities. If they choose custom, the system validates that their selection meets occupancy requirements."

**[Select rooms]**

**[Screen: Add-On Services]**

"Guests can select add-on services like Wi-Fi, breakfast, parking, and spa. Notice how the price updates dynamically as I select each service. This uses the Decorator pattern to add services to the base booking price."

**[Select add-ons]**

**[Screen: Booking Summary]**

"Before confirmation, guests see a complete summary including:
- Room subtotal with dynamic pricing
- Add-on services
- Tax calculation
- Total amount

The dynamic pricing applies a 20% weekend multiplier. You can see Saturday and Sunday nights are priced higher."

**[Screen: Confirmation]**

"After confirmation, the reservation is saved and the guest receives a confirmation number. The system clearly informs them that billing will be handled at the front desk."

---

### Part 2: Admin Module Demo (2-3 minutes)

**[Screen: Login Screen]**

"Now let's switch to the admin module. Administrators log in with username and password. All passwords are hashed using BCrypt for security."

**[Login: admin / admin123]**

**[Screen: Admin Dashboard]**

"This is the admin dashboard. Administrators can search for reservations by guest name, phone, date range, or status. The results are displayed in a paginated, sortable table."

**[Perform search]**

**[Screen: Reservation Details]**

"Clicking on a reservation shows full details. Administrators can modify reservations, process payments, apply discounts, and handle checkouts."

**[Screen: Payment Processing]**

"Let me process a payment. The system supports cash, card, and loyalty points. I'll process a partial payment of $100."

**[Process payment]**

"You can see the balance updates immediately. The system tracks all payments and maintains accurate balances."

**[Screen: Checkout]**

"Now for checkout. The system generates the final bill. Administrators can apply discounts - notice the role-based limits: Admin can apply up to 15%, Manager up to 30%.

After applying a discount and processing the final payment, I'll complete the checkout."

**[Complete checkout]**

"Notice what happened - when I completed the checkout, the system automatically freed the rooms and triggered a notification. This is the Observer pattern in action - the RoomAvailabilityPublisher notifies all waitlist subscribers when a room becomes available."

**[Screen: Waitlist Management]**

"Let me show you the waitlist. When rooms are unavailable, guests can be added to the waitlist. When a room becomes available - like we just saw - administrators receive a notification and can convert the waitlist entry to a reservation."

---

### Part 3: Design Patterns Demo (1-2 minutes)

**[Screen: Code view or diagram]**

"Let me explain the design patterns I've implemented:

**Strategy Pattern:** Used for billing calculations. I have three strategies - StandardBillingStrategy, DiscountBillingStrategy, and LoyaltyBillingStrategy. The BillingService selects the appropriate strategy based on the billing context."

**[Show code or diagram]**

"**Observer Pattern:** Implemented for waitlist notifications. When a room becomes available, the RoomAvailabilityPublisher notifies all WaitlistSubscribers. This decouples the room availability logic from the waitlist management."

"**Decorator Pattern:** Used for add-on services. The base booking is a RoomBookingComponent, and services like Wi-Fi and breakfast are added as AddOnDecorators, dynamically building up the total price."

"**Factory Pattern:** RoomFactory creates Room instances with configured attributes based on room type."

"**Singleton Pattern:** LoggerService and EntityManagerFactory are singletons, ensuring only one instance exists application-wide."

---

### Part 4: Additional Features (1 minute)

**[Screen: Reports]**

"The system includes comprehensive reporting. Administrators can generate:
- Revenue reports by day, week, or month
- Occupancy reports showing room utilization
- Activity logs of all system actions
- Feedback summaries

All reports can be exported to CSV, PDF, or TXT formats."

**[Generate and export a report]**

**[Screen: Feedback]**

"After checkout, guests can submit feedback with a 1-5 star rating and comments. The system only allows feedback after checkout and when the balance is settled."

**[Screen: Loyalty Program]**

"The loyalty program allows guests to earn points on payments - 1 point per $10 spent. Points can be redeemed for discounts, with a maximum of 1000 points per redemption."

---

### Part 5: Challenges & Solutions (1-2 minutes)

**[Screen: Code or diagrams]**

"Let me share some key challenges I faced and how I solved them:

**Challenge 1: EntityManager Lifecycle**
Initially, I tried to reuse a single EntityManager, which caused transaction issues. I solved this by implementing the EntityManager-per-transaction pattern, creating a new EntityManager for each operation and properly managing transactions with try-catch-finally blocks.

**Challenge 2: Observer Pattern Integration**
Implementing the Observer pattern for waitlist notifications required careful integration with the checkout process. I created Subject and Observer interfaces, implemented RoomAvailabilityPublisher and WaitlistSubscriber, and integrated the notification into the checkout method.

**Challenge 3: State Management in JavaFX**
Managing state across multiple screens in the kiosk flow was complex. I solved this by creating a KioskStateHelper class and using instance variables in the controller, with proper state validation before navigation.

**Challenge 4: Dynamic Pricing**
Calculating prices across date ranges with different multipliers required careful date iteration. I created a PricingPolicy class that iterates through each night in the date range and applies the appropriate multiplier.

These challenges taught me the importance of proper architecture, design patterns, and systematic problem-solving."

---

### Conclusion (30 seconds)

**[Screen: Application overview]**

"In conclusion, this Hotel Reservation System demonstrates:
- Complete 3-tier architecture implementation
- Five design patterns properly applied
- Full CRUD operations with JPA
- Comprehensive business logic
- Professional UI/UX design
- Security with BCrypt
- Comprehensive logging and reporting

The system is fully functional, well-documented, and ready for production use. Thank you for watching!"

---

## 📝 Recording Tips

1. **Before Recording:**
   - Test all features work
   - Have sample data ready
   - Close unnecessary applications
   - Test microphone audio
   - Prepare screen recording software

2. **During Recording:**
   - Speak clearly and at moderate pace
   - Pause between sections
   - Show actual functionality, not just UI
   - Explain what you're doing
   - Show code/diagrams when explaining patterns

3. **After Recording:**
   - Review the video
   - Check audio quality
   - Verify all features shown
   - Edit if needed (cut long pauses)
   - Export in good quality

4. **Technical Requirements:**
   - Resolution: 1080p minimum
   - Frame rate: 30fps
   - Audio: Clear, no background noise
   - Format: MP4 recommended
   - Duration: 7-10 minutes minimum

---

## ✅ Video Checklist

- [ ] Introduction (1 min)
- [ ] Kiosk demo (2-3 min)
- [ ] Admin demo (2-3 min)
- [ ] Design patterns explained (1-2 min)
- [ ] Challenges discussed (1-2 min)
- [ ] Conclusion (30 sec)
- [ ] Total duration: 7-10+ minutes
- [ ] Audio is clear
- [ ] Video quality is good
- [ ] All features demonstrated
- [ ] Code/diagrams shown for patterns

---

**Good luck with your video recording! 🎥**

