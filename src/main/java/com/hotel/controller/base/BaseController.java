package com.hotel.controller.base;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

//
 // Base controller class providing common functionality for all controllers.
 // Contains shared methods for navigation, error handling, and stage management.
//
 // This class reduces code duplication across controllers by centralizing
 // common patterns like error display, stage retrieval, and basic navigation.
//
public abstract class BaseController {
    
    //
     // Gets the current Stage from a node's scene.
     // Tries multiple fallback nodes if the primary node is not available.
//
     // @param fallbackNodes Optional nodes to try if common fields are not available
     // @return The current Stage
     // @throws IllegalStateException if no stage can be determined
//
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try fallback nodes first (if provided)
        for (Node node : fallbackNodes) {
            if (node != null && node.getScene() != null) {
                return (Stage) node.getScene().getWindow();
            }
        }
        
        // Try to get from common field names (using reflection would be complex,
        // so we'll rely on subclasses to pass the right node)
        // This is a template method - subclasses can override if needed
        
        throw new IllegalStateException("Unable to determine current stage. " +
            "Override getCurrentStage() or pass a node as parameter.");
    }
    
    //
     // Gets the current Stage from a specific node.
//
     // @param node The node to get the stage from
     // @return The current Stage
     // @throws IllegalStateException if node or scene is null
//
    protected Stage getCurrentStageFromNode(Node node) {
        if (node != null && node.getScene() != null) {
            return (Stage) node.getScene().getWindow();
        }
        throw new IllegalStateException("Unable to determine current stage from node");
    }
    
    //
     // Displays an error message in the specified error label.
//
     // @param errorLabel The label to display the error in (can be null)
     // @param message The error message to display
//
    protected void showError(Label errorLabel, String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }
    
    //
     // Hides the error message in the specified error label.
//
     // @param errorLabel The label to hide (can be null)
//
    protected void hideError(Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }
    
    //
     // Template method for back navigation.
     // Subclasses should override this to implement their specific back navigation logic.
//
    protected void goBack() {
        // Default implementation - subclasses should override
        throw new UnsupportedOperationException("goBack() must be implemented by subclass");
    }
}

