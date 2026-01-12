package com.hotel.controller.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

//
 // Simple navigation helper to swap admin scenes and run controller initialization.
//
public final class AdminNavigationHelper {

    private AdminNavigationHelper() {
    }

    public static void switchScene(Stage stage,
                                   String fxmlPath,
                                   Consumer<Object> controllerConsumer) throws IOException {
        FXMLLoader loader = new FXMLLoader(AdminNavigationHelper.class.getResource(fxmlPath));
        Parent root = loader.load();
        if (controllerConsumer != null) {
            controllerConsumer.accept(loader.getController());
        }
        stage.setScene(new Scene(root, 1200, 800));
    }
}

