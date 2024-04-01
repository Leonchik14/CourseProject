package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private Button editCollectionsButton;

    @FXML
    private Button statisticsButton;

    @FXML
    private Button playButton;

    @FXML
    void initialize() {
        playButton.setOnAction(event -> SceneSwitcher.SwitchScene(event, "game-settings-view.fxml"));
        statisticsButton.setOnAction(event -> SceneSwitcher.SwitchScene(event, "collection-statistics-view.fxml"));
        editCollectionsButton.setOnAction(event -> SceneSwitcher.SwitchScene(event, "collection-edit-view.fxml"));
    }

}
