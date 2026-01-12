package com.hotel.model;

// result class for guest selection dialogs
// used to return selection results from customer selection dialogs
public class GuestSelectionResult {
    public final Guest guest;
    public final boolean createAccount;
    public final boolean proceedAsGuest;
    
    public GuestSelectionResult(Guest guest, boolean createAccount, boolean proceedAsGuest) {
        this.guest = guest;
        this.createAccount = createAccount;
        this.proceedAsGuest = proceedAsGuest;
    }
}


