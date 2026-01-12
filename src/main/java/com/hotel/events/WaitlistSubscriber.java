package com.hotel.events;

import com.hotel.util.LoggerService;
import java.util.ArrayList;
import java.util.List;

// observer that receives notifications when rooms become available
// stores notifications so admins can see which rooms are now available for waitlist guests
public class WaitlistSubscriber implements Observer {
    
    private List<String> notifications = new ArrayList<>();
    private LoggerService logger;
    
    public WaitlistSubscriber() {
        this.logger = LoggerService.getInstance();
    }
    
    @Override
    public void update(String message) {
        notifications.add(message);
        logger.logInfo("WaitlistSubscriber received notification: " + message);
    }
    
    // gets all stored notifications
    // returns a copy so the original list can't be modified
    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }
    
    // clears all stored notifications
    public void clearNotifications() {
        notifications.clear();
    }
    
    // checks if there are any notifications waiting
    public boolean hasNotifications() {
        return !notifications.isEmpty();
    }
}


