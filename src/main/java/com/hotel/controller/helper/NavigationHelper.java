package com.hotel.controller.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

//
 // Unified navigation helper for all controllers (admin and kiosk).
 // Provides consistent navigation patterns across the application.
//
 // This class reduces code duplication by centralizing navigation logic
 // that was previously duplicated in multiple controllers.
//
public final class NavigationHelper {
    
    private NavigationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Navigate to a screen with basic scene loading.
//
     // @param stage The current stage
     // @param fxmlPath The path to the FXML file (e.g., "/view/admin/Dashboard.fxml")
     // @throws IOException if the FXML file cannot be loaded
//
    public static void navigate(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1200, 800));
    }
    
    //
     // Navigate to a screen with controller callback for initialization.
//
     // @param stage The current stage
     // @param fxmlPath The path to the FXML file
     // @param controllerCallback Callback to initialize the controller (can be null)
     // @throws IOException if the FXML file cannot be loaded
//
    public static void navigateWithController(Stage stage, String fxmlPath, Consumer<Object> controllerCallback) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
        Parent root = loader.load();
        
        if (controllerCallback != null) {
            Object controller = loader.getController();
            controllerCallback.accept(controller);
        }
        
        stage.setScene(new Scene(root, 1200, 800));
    }
    
    //
     // Navigate to a screen using a node to get the stage.
     // Convenience method that extracts the stage from a node.
//
     // @param node Any node in the current scene (used to get the stage)
     // @param fxmlPath The path to the FXML file
     // @throws IOException if the FXML file cannot be loaded
     // @throws IllegalStateException if the stage cannot be determined from the node
//
    public static void navigateFromNode(Node node, String fxmlPath) throws IOException {
        if (node == null || node.getScene() == null) {
            throw new IllegalStateException("Cannot determine stage from node");
        }
        Stage stage = (Stage) node.getScene().getWindow();
        navigate(stage, fxmlPath);
    }
    
    //
     // Navigate to a screen using a node with controller callback.
//
     // @param node Any node in the current scene (used to get the stage)
     // @param fxmlPath The path to the FXML file
     // @param controllerCallback Callback to initialize the controller (can be null)
     // @throws IOException if the FXML file cannot be loaded
     // @throws IllegalStateException if the stage cannot be determined from the node
//
    public static void navigateFromNodeWithController(Node node, String fxmlPath, Consumer<Object> controllerCallback) throws IOException {
        if (node == null || node.getScene() == null) {
            throw new IllegalStateException("Cannot determine stage from node");
        }
        Stage stage = (Stage) node.getScene().getWindow();
        navigateWithController(stage, fxmlPath, controllerCallback);
    }
    
    //
     // Get the current stage from a node.
//
     // @param node Any node in the current scene
     // @return The stage containing the node
     // @throws IllegalStateException if the stage cannot be determined
//
    public static Stage getCurrentStage(Node node) {
        if (node == null || node.getScene() == null) {
            throw new IllegalStateException("Cannot determine stage from node");
        }
        return (Stage) node.getScene().getWindow();
    }
    
    //
     // Get the current stage from a node with fallback options.
     // Tries multiple nodes in order until one provides a valid stage.
//
     // @param primaryNode The primary node to try
     // @param fallbackNodes Additional nodes to try if primary fails
     // @return The stage
     // @throws IllegalStateException if no stage can be determined
//
    public static Stage getCurrentStage(Node primaryNode, Node... fallbackNodes) {
        if (primaryNode != null && primaryNode.getScene() != null) {
            return (Stage) primaryNode.getScene().getWindow();
        }
        
        for (Node node : fallbackNodes) {
            if (node != null && node.getScene() != null) {
                return (Stage) node.getScene().getWindow();
            }
        }
        
        throw new IllegalStateException("Unable to determine current stage from any provided node");
    }
}

