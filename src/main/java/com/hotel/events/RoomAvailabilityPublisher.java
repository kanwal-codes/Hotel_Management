package com.hotel.events;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.util.LoggerService;
import java.util.ArrayList;
import java.util.List;

/**
 * Observer pattern - this is the Subject/Publisher.
 * When a room becomes available, this notifies all subscribed observers (like waitlist).
 */
public class RoomAvailabilityPublisher implements Subject {
    
    private List<Observer> observers = new ArrayList<>();
    private LoggerService logger;
    
    public RoomAvailabilityPublisher() {
        this.logger = LoggerService.getInstance();
    }
    
    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            logger.logInfo("Observer attached to RoomAvailabilityPublisher");
        }
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
        logger.logInfo("Observer detached from RoomAvailabilityPublisher");
    }
    
    @Override
    public void notifyObservers(String message) {
        logger.logInfo("RoomAvailabilityPublisher notifying " + observers.size() + " observers: " + message);
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    
    /**
     * Called when a room becomes available (usually after checkout).
     * Sends notification to all observers.
     */
    public void publishRoomAvailable(Room room) {
        String message = String.format("Room %s (%s) is now available", 
            room.getRoomNumber(), room.getType());
        notifyObservers(message);
    }
    
    /**
     * Publishes that a room of a specific type is available.
     * Used when we know the type but not the specific room.
     */
    public void publishRoomTypeAvailable(RoomType roomType) {
        String message = String.format("A %s room is now available", roomType);
        notifyObservers(message);
    }
}



