package com.hotel.controller.helper;

import com.hotel.model.Billing;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationAddon;
import com.hotel.model.Room;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.BillingService;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//
 // Helper class for loading reservation data into the admin reservation form.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminReservationLoaderHelper {
    
    private AdminReservationLoaderHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Loads reservation data and populates pending collections.
//
    public static ReservationLoadResult loadReservationData(
            Long reservationId,
            ReservationRepository reservationRepository,
            BillingService billingService) {
        
        Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
        if (resOpt.isEmpty()) {
            return null;
        }
        
        Reservation reservation = resOpt.get();
        
        // Extract rooms
        List<Room> rooms = new ArrayList<>();
        if (reservation.getReservationRooms() != null) {
            rooms.addAll(
                reservation.getReservationRooms().stream()
                    .map(rr -> rr.getRoom())
                    .toList()
            );
        }
        
        // Extract addons (create new objects to avoid detached entity issues)
        List<ReservationAddon> addons = new ArrayList<>();
        if (reservation.getReservationAddons() != null && !reservation.getReservationAddons().isEmpty()) {
            for (ReservationAddon existing : reservation.getReservationAddons()) {
                ReservationAddon newRa = new ReservationAddon();
                newRa.setReservation(reservation);
                newRa.setAddon(existing.getAddon());
                newRa.setQuantity(existing.getQuantity());
                addons.add(newRa);
            }
        }
        
        // Get billing
        Optional<Billing> billingOpt = billingService.getBillingForReservation(reservation);
        Billing billing = billingOpt.orElse(null);
        
        return new ReservationLoadResult(reservation, rooms, addons, billing);
    }
    
    //
     // Result class for reservation loading.
//
    public static class ReservationLoadResult {
        public final Reservation reservation;
        public final List<Room> rooms;
        public final List<ReservationAddon> addons;
        public final Billing billing;
        
        public ReservationLoadResult(Reservation reservation, List<Room> rooms, 
                                   List<ReservationAddon> addons, Billing billing) {
            this.reservation = reservation;
            this.rooms = rooms;
            this.addons = addons;
            this.billing = billing;
        }
    }
}


