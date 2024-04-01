package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import com.app.cardsapplication.utils.Shake;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameSettingsController {

    private Collection gameCollection;

    private String gameModeName;

    @FXML
    private ToggleGroup gameModeToggleGroup = new ToggleGroup();


    @FXML
    private RadioButton commonGameMode;

    @FXML
    private RadioButton randomGameMode;

    @FXML
    private RadioButton fullGameMode;

    @FXML
    private Button startGameButton;

    @FXML
    private ListView<Collection> collectionsList;

    @FXML
    private ImageView returnButton;

    @FXML
    void initialize() {
        commonGameMode.setToggleGroup(gameModeToggleGroup);
        randomGameMode.setToggleGroup(gameModeToggleGroup);
        fullGameMode.setToggleGroup(gameModeToggleGroup);


        ObservableList<Collection> items = FXCollections.observableArrayList();

        List<Collection> collections = MongoUtil.getDatabase().getCollection("collections", Collection.class).find().into(new ArrayList<>());
        items.addAll(collections);

        collectionsList.setItems(items);

        collectionsList.setCellFactory(new Callback<ListView<Collection>, ListCell<Collection>>() {
            @Override
            public ListCell<Collection> call(ListView<Collection> listView) {
                return new ListCell<Collection>() {
                    @Override
                    protected void updateItem(Collection collection, boolean empty) {
                        super.updateItem(collection, empty);

                        if (empty || collection == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText("Collection name: " + collection.getName());
                        }
                    }
                };
            }
        });

        gameModeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (gameModeToggleGroup.getSelectedToggle() != null) {
                RadioButton selectedRadioButton = (RadioButton) gameModeToggleGroup.getSelectedToggle();
                String toggleId = selectedRadioButton.getText();
                switch (toggleId) {
                    case "Common":
                        gameModeName = "Common";
                        break;
                    case "Random":
                        gameModeName = "Random";
                        break;
                    case "Full coverage":
                        gameModeName = "Full";
                        break;
                }
            }
        });
        collectionsList.setOnMouseClicked(event -> {
            gameCollection = collectionsList.getSelectionModel().getSelectedItem();
        });

        startGameButton.setOnAction(event -> {
            if (gameCollection == null) {
                Shake collectionAnim = new Shake(collectionsList);
                collectionAnim.PlayAnim();
                return;
            }
            if (gameCollection.cards.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Notification");
                alert.setHeaderText("Error starting game");
                alert.setContentText("Selected collection is empty");
                alert.showAndWait();
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("game-process-view.fxml"));
            try {
                Parent view = fxmlLoader.load();

                GameProcessController controller = fxmlLoader.getController();
                controller.SetupGame(gameModeName, gameCollection);

                Scene scene = new Scene(view, 700, 400);
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setTitle("CardsApplication");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "main-menu-view.fxml"));
    }
}