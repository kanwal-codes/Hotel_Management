package com.hotel.events;

// subject interface for observer pattern
// objects that can notify observers implement this interface
public interface Subject {
    
    // adds an observer to receive notifications
    void attach(Observer observer);
    
    // removes an observer so it stops receiving notifications
    void detach(Observer observer);
    
    // sends a notification message to all attached observers
    void notifyObservers(String message);
}



