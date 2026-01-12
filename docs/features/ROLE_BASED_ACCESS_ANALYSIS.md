# Role-Based Access Control Analysis

## Summary
Based on the project instructions, **both ADMIN and MANAGER roles have access to the same features**, but with **different limits** for certain actions.

## Key Difference: Discount Limits

### Admin Role
- **Maximum Discount**: Up to **15%**
- Can apply discounts up to 15% on reservations

### Manager Role
- **Maximum Discount**: Up to **30%**
- Can apply discounts up to 30% on reservations

## Features Accessible to Both Roles

According to the project instructions, both ADMIN and MANAGER can access:

1. **Dashboard**
   - Search for guests and reservations
   - View paginated tables
   - Open detailed views for editing

2. **Reservations Management**
   - Create reservations (via phone)
   - Modify reservations
   - Cancel reservations
   - Perform conflict checks
   - Support group bookings

3. **Payments**
   - Process payments (Cash, Card, Loyalty points)
   - Handle deposits at booking time
   - Process partial payments during stay
   - Process refunds when required
   - Track paid and outstanding balances

4. **Loyalty Program**
   - Enroll guests in loyalty program
   - View loyalty dashboard
   - Manage loyalty points

5. **Checkout**
   - Generate final bill
   - Settle the balance
   - Mark rooms as available
   - Trigger room availability notifications

6. **Waitlist**
   - Add guests to waitlist
   - Convert waitlist entries to reservations
   - Receive notifications when availability changes

7. **Feedback Management**
   - View feedback entries (after guest checkout)
   - Filter feedback by rating, date, sentiment, guest
   - Export feedback summaries

8. **Reporting**
   - Generate revenue reports
   - Generate occupancy reports
   - View activity logs
   - Export reports (CSV, PDF, TXT)

## Role Checks Required (But Not Exclusive)

The instructions specify that role checks must be performed for sensitive actions:
- **Discounts** (with different limits per role)
- **Refunds** (both can process, but should log who did it)
- **Reporting** (both can access, but should log who generated reports)
- **User management** (both may access, but should log actions)

## Implementation Status

### ✅ Currently Implemented
- Discount limits enforced (Admin: 15%, Manager: 30%)
- Role-based discount validation in `DiscountPolicy`
- Role checks in `BillingService.applyDiscount()`
- Role enum (ADMIN, MANAGER) in `Role.java`
- AuthService methods for role checking

### ⚠️ Potential Gaps
- **User Management**: No explicit user management interface found (creating/editing admin users)
- **Refund Restrictions**: Both roles can process refunds, but no explicit limits mentioned
- **Reporting Access**: Both roles can access all reports, which seems correct
- **UI Restrictions**: No UI elements are hidden based on role (both see same interface)

## Conclusion

**According to the project instructions:**
- **No features are exclusive to ADMIN or MANAGER**
- **Both roles have the same access to all features**
- **The only difference is the discount limit:**
  - Admin: 15% maximum
  - Manager: 30% maximum

The system should ensure that:
1. Discount limits are enforced based on role
2. All actions are logged with the actor's role
3. Role information is displayed (e.g., "Welcome, username (ADMIN)")
4. Role checks are performed for sensitive actions (for audit purposes)

## Recommendations

If you want to add more role-based restrictions (beyond what's in the instructions), you could consider:
- **User Management**: Only ADMIN can create/edit/delete admin users
- **Refund Limits**: Similar to discounts, different limits per role
- **Report Access**: Restrict certain sensitive reports to MANAGER only

However, **the current implementation matches the project requirements**, which only specify different discount limits between roles.




