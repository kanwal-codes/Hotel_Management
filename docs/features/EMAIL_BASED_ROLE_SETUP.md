# Email-Based Role Setup and Discount Constraints

## Summary
The system now supports email-based login with automatic role assignment and discount constraints based on email addresses.

## Admin Users Setup

### Email IDs Created:
1. **admin@hotel.com**
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`
   - **Max Discount: 15%**

2. **manager@hotel.com**
   - Username: `manager`
   - Password: `admin123`
   - Role: `MANAGER`
   - **Max Discount: 30%**

## How to Create/Update Admin Users

Run the Java utility:
```bash
java com.hotel.app.UpdateAdminPasswords
```

This will:
- Create or update `admin@hotel.com` with ADMIN role (15% max discount)
- Create or update `manager@hotel.com` with MANAGER role (30% max discount)
- Set passwords to `admin123` for both (hashed with BCrypt)

## Discount Constraints Implementation

### ✅ Enforced Constraints:
1. **Admin (admin@hotel.com)**: Cannot apply more than **15%** discount
2. **Manager (manager@hotel.com)**: Cannot apply more than **30%** discount

### Implementation Details:
- **DiscountPolicy.java**: Defines role-based discount caps
- **BillingService.applyDiscount()**: Validates discount against role limit before applying
- **AdminDiscountController**: 
  - Shows max discount limit in UI based on logged-in user's role
  - Validates discount amount before submission
  - Shows error if discount exceeds role limit

### UI Features:
- Discount application screen shows: "Maximum discount allowed: X% (based on your role: ROLE)"
- Error message displayed if user tries to exceed their role's limit
- Discount is automatically capped to role's maximum if validation passes

## Refund Constraints Check

### Current Implementation:
- **BillingService.processRefund()**: Processes refunds (negative payments)
- **No role-based constraints**: Both ADMIN and MANAGER can process refunds
- **No amount limits**: No maximum refund amount restrictions
- **Logging**: All refunds are logged with actor information

### Recommendation:
According to project instructions, refunds should have role checks (for audit purposes), but no explicit limits are specified. The current implementation:
- ✅ Logs all refunds with actor information
- ✅ Both roles can process refunds
- ⚠️ No maximum refund amount limits (could be added if needed)

## Login Methods

Users can login using:
1. **Email**: `admin@hotel.com` or `manager@hotel.com`
2. **Username**: `admin` or `manager`
3. **Password**: `admin123` (for both)

The system will:
- Find user by email or username
- Verify password with BCrypt
- Assign role-based permissions based on the user's role
- Enforce discount limits based on role

## Testing

To test the setup:
1. Run `UpdateAdminPasswords.java` to create/update admin users
2. Login with `admin@hotel.com` / `admin123`
   - Try applying 16% discount → Should fail (max 15%)
   - Try applying 15% discount → Should succeed
3. Login with `manager@hotel.com` / `admin123`
   - Try applying 31% discount → Should fail (max 30%)
   - Try applying 30% discount → Should succeed




