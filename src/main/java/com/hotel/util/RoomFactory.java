package com.hotel.util;

import com.hotel.model.Hotel;
import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;

// factory pattern - creates room objects with the right defaults based on type
// instead of creating rooms manually everywhere, use this to ensure consistency
public class RoomFactory {
    
    // creates a room with default settings based on room type
    // sets beds and base price automatically
    public static Room createRoom(RoomType type, String roomNumber, Hotel hotel) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setHotel(hotel);
        room.setStatus(RoomStatus.AVAILABLE);
        
        // set default beds and prices based on room type
        switch (type) {
            case SINGLE:
                room.setBeds(1);
                room.setBasePrice(100.0);
                break;
            case DOUBLE:
                room.setBeds(2);
                room.setBasePrice(150.0);
                break;
            case DELUXE:
                room.setBeds(1);
                room.setBasePrice(200.0);
                break;
            case PENTHOUSE:
                room.setBeds(1);
                room.setBasePrice(500.0);
                break;
            default:
                room.setBeds(1);
                room.setBasePrice(100.0);
        }
        
        return room;
    }
    
    // creates a room with custom price and bed count
    // useful when you need different prices than the defaults
    public static Room createRoom(RoomType type, String roomNumber, Hotel hotel, double basePrice, int beds) {
        Room room = createRoom(type, roomNumber, hotel);
        room.setBasePrice(basePrice);
        room.setBeds(beds);
        return room;
    }
}



