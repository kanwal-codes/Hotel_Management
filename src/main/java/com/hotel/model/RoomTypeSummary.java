package com.hotel.model;

// summary class for room type counts and capacities
// used for displaying room selections grouped by type
// extracted from AdminReservationController to reduce controller size
public class RoomTypeSummary {
    private final RoomType type;
    private int count;
    
    public RoomTypeSummary(RoomType type, int count) {
        this.type = type;
        this.count = count;
    }
    
    public RoomType getType() { 
        return type; 
    }
    
    public int getCount() { 
        return count; 
    }
    
    public void setCount(int count) { 
        this.count = count; 
    }
    
    public int getCapacity() {
        switch (type) {
            case SINGLE:
            case DELUXE:
            case PENTHOUSE:
                return 2;
            case DOUBLE:
                return 4;
            default:
                return 0;
        }
    }
    
    public int getTotalCapacity() {
        return count * getCapacity();
    }
}


