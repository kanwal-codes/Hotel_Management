package com.hotel.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the hotel reservation system.
 * This is where the JavaFX application starts.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set up all the services and database connections before showing UI
        AppConfig.initialize();
        
        // Load the first screen users see - the kiosk welcome screen
        Parent root = FXMLLoader.load(getClass().getResource("/view/kiosk/KioskWelcome.fxml"));
        
        // Set window size and title, then show it
        primaryStage.setTitle("Hotel Reservation System");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        // Close database connections when app closes to prevent memory leaks
        AppConfig.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




