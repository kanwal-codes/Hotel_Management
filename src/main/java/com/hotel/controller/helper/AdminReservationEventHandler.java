package com.hotel.controller.helper;

import com.hotel.app.AppConfig;
import com.hotel.model.AdminUser;
import com.hotel.model.Reservation;
import com.hotel.repository.AdminUserRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.util.LoggerService;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

//
 // Helper class for admin reservation event handling.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminReservationEventHandler {
    
    private AdminReservationEventHandler() {
        // Utility class - prevent instantiation
    }
    
    //
     // Opens payment screen for a reservation.
//
    public static void openPaymentScreen(
            Reservation reservation,
            AdminUser currentUser,
            LoggerService logger,
            Consumer<String> navigateToAdminScreen,
            Consumer<Object> initPaymentController) {
        
        if (reservation == null) {
            AlertHelper.showError("Error", "Please save the reservation first.");
            return;
        }
        
        AdminUser user = currentUser;
        if (user == null) {
            logger.logError("openPaymentScreen called but currentUser is null", new IllegalStateException("User not logged in"));
            // Try to get a default admin user as fallback
            try {
                EntityManager em = AppConfig.createEntityManager();
                try {
                    AdminUserRepository adminRepo = new AdminUserRepository(em);
                    Optional<AdminUser> defaultAdmin = adminRepo.findByEmail("admin@hotel.com");
                    if (defaultAdmin.isPresent()) {
                        user = defaultAdmin.get();
                        logger.logInfo("Using default admin user as fallback for payment screen");
                    } else {
                        AlertHelper.showError("Error", "User session expired. Please log in again.");
                        return;
                    }
                } finally {
                    em.close();
                }
            } catch (Exception e) {
                logger.logError("Failed to retrieve default admin user", e);
                AlertHelper.showError("Error", "User session expired. Please log in again.");
                return;
            }
        }
        
        navigateToAdminScreen.accept("/view/admin/PaymentProcessing.fxml");
    }
    
    //
     // Opens checkout screen for a reservation.
//
    public static void openCheckoutScreen(
            Reservation reservation,
            AdminUser currentUser,
            Consumer<String> navigateToAdminScreen) {
        
        if (reservation == null) {
            AlertHelper.showError("Error", "No reservation selected.");
            return;
        }
        navigateToAdminScreen.accept("/view/admin/CheckoutScreen.fxml");
    }
    
    //
     // Deletes a reservation with confirmation.
//
    public static boolean deleteReservation(
            Reservation reservation,
            ReservationRepository reservationRepository,
            LoggerService logger,
            Runnable onSuccess) {
        
        if (reservation == null) {
            AlertHelper.showError("Error", "No reservation selected");
            return false;
        }
        
        boolean confirmed = AlertHelper.showConfirmation("Delete Reservation",
            "Are you sure you want to permanently delete this reservation?");
        if (!confirmed) return false;
        
        try {
            reservationRepository.delete(reservation);
            AlertHelper.showInfo("Success", "Reservation deleted successfully");
            if (onSuccess != null) {
                onSuccess.run();
            }
            return true;
        } catch (Exception e) {
            logger.logError("Failed to delete reservation", e);
            AlertHelper.showError("Error", "Failed to delete reservation: " + e.getMessage());
            return false;
        }
    }
    
    //
     // Navigates back to dashboard.
//
    public static void backToDashboard(
            AdminUser currentUser,
            Stage stage,
            Consumer<Object> initDashboardController) {
        
        try {
            AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", controller -> {
                if (initDashboardController != null) {
                    initDashboardController.accept(controller);
                }
            });
        } catch (IOException e) {
            LoggerService.getInstance().logError("Failed to navigate to dashboard", e);
            AlertHelper.showError("Navigation Error", "Failed to load dashboard: " + e.getMessage());
        }
    }
}


