package com.hotel.events;

// observer interface for observer pattern
// objects that want to receive notifications implement this interface
public interface Observer {
    
    // called when the subject sends a notification
    void update(String message);
}



