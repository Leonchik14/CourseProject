package com.app.cardsapplication.utils;

import com.app.cardsapplication.CardsApplication;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void SwitchScene(Event event, String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource(fxml));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 700, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        primaryStage.setTitle("CardsApplication");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
