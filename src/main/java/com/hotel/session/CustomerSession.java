package com.hotel.session;

import com.hotel.model.Guest;

/**
 * Simple session holder for kiosk/customer login flow.
 */
public final class CustomerSession {

    private static Guest authenticatedGuest;

    private CustomerSession() {
    }

    public static void setAuthenticatedGuest(Guest guest) {
        authenticatedGuest = guest;
    }

    public static Guest getAuthenticatedGuest() {
        return authenticatedGuest;
    }

    public static boolean isAuthenticated() {
        return authenticatedGuest != null;
    }

    public static void clear() {
        authenticatedGuest = null;
    }
}

