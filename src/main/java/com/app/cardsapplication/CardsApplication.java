package com.app.cardsapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.app.cardsapplication.utils.MongoUtil;

import java.io.IOException;

public class CardsApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("main-menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
        stage.setTitle("CardsApplication");
        stage.setScene(scene);
        stage.show();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (MongoUtil.getClient() != null) {
                MongoUtil.getClient().close();
            }
        }));
    }

    public static void main(String[] args) {
        launch();
    }

}