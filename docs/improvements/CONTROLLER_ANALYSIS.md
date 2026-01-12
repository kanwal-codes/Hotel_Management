# Controller Code Analysis & Improvement Plan

**Date:** 2025-12-03
**Status:** Draft

## Executive Summary
The `com.hotel.controller` package contains 21 files. While the core functionality is working, there are significant opportunities for refactoring to reduce code duplication, improve maintainability, and adhere to the Single Responsibility Principle (SRP).

## Detailed Controller Analysis

| Controller | Size | Responsibilities | Issues | Recommendation |
| :--- | :--- | :--- | :--- | :--- |
| **KioskController** | 140KB | Entire booking wizard | "God Class", mixed UI/Business logic | Split into `KioskGuest`, `KioskDates`, `KioskRooms`, `KioskAddons`. Use `BookingContext`. |
| **AdminReservationController** | 84KB | Admin booking/editing | Complex UI logic (Room Suggestion), Validation | Move room suggestion to `RoomSelectionHelper`. Delegate validation to Service. |
| **ReportController** | 34KB | Generating reports | Table setup duplication, Export logic mixed in | Extract `ReportTableHelper`. Move export logic to `ExportService`. |
| **AdminCheckoutController** | 31KB | Checkout & Final Bill | Payment history logic duplicated from PaymentController | Extract `PaymentHistoryHelper`. |
| **AdminPaymentController** | 23KB | Payment processing | Loyalty logic duplication | Merge common payment logic with `KioskPaymentController` into `BasePaymentController`. |
| **LoyaltyController** | 20KB | Loyalty management | Guest search logic duplication | Use shared `GuestSearchHelper`. |
| **KioskPaymentController** | 21KB | Kiosk payment | Duplicates AdminPaymentController | Merge/Inherit from `BasePaymentController`. |
| **AdminWaitlistController** | 21KB | Waitlist management | - | Review for minor simplifications. |
| **AdminDashboardController** | 15KB | Main menu | - | Keep as is. |
| **FeedbackController** | 10KB | Feedback submission | - | Keep as is. |
| **UnifiedLoginController** | 7KB | Login (Admin/Customer) | - | **Keep as primary login controller.** |
| **AdminLoginController** | 3KB | Admin Login | Redundant | **Delete/Deprecate.** |
| **CustomerLoginController** | 3KB | Customer Login | Redundant | **Delete/Deprecate.** |

## Key Findings & Recommendations

### 1. "God Class" Decomposition
**Issue:** `KioskController` is too large and manages too many distinct steps.
**Action:** Break it down into step-specific controllers. This will reduce file size and make state management clearer.

### 2. Logic Duplication
-   **Payment Logic:** `AdminPaymentController` and `KioskPaymentController` share significant logic for calculating totals, handling loyalty points, and processing transactions.
    -   *Recommendation:* Create `BasePaymentController` or `PaymentHelper` to house shared logic.
-   **Room Suggestions:** `AdminReservationController` contains complex logic for suggesting rooms that partially duplicates `ReservationService`.
    -   *Recommendation:* Move ALL suggestion logic to `ReservationService` or `RoomSelectionHelper`.
-   **Navigation:** Every controller implements its own `goBack()` and `getCurrentStage()` methods.
    -   *Recommendation:* Create a `BaseController` class that provides these common utility methods.

### 3. Login Redundancy
**Issue:** Three controllers handle login.
**Action:** Consolidate everything into `UnifiedLoginController`. Remove `AdminLoginController` and `CustomerLoginController`.

### 4. Helper Usage
**Observation:** `AlertHelper` exists but isn't used everywhere.
**Action:** Enforce usage of `AlertHelper` for all dialogs to ensure consistent UI.

### 5. Empty Directories
**Observation:** `com.hotel.controller.util` is empty.
**Action:** Removed as part of cleanup.



## Action Plan

1.  **Immediate Cleanup**:
    -   Deprecate `AdminLoginController` and `CustomerLoginController`.
    -   Verify `UnifiedLoginController` covers all use cases.

2.  **Refactoring Phase 1 (Admin)**:
    -   Extract room selection logic from `AdminReservationController` into `RoomSelectionHelper`.

3.  **Refactoring Phase 2 (Kiosk)**:
    -   Split `KioskController` into step-based controllers.
    -   Implement a shared `BookingContext` for state management.

## Notes for Developers
- Always check `com.hotel.controller.helper` before implementing new UI logic.
- Avoid adding more logic to `KioskController` or `AdminReservationController`.
