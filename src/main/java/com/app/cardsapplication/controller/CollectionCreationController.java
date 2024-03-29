package com.app.cardsapplication.controller;

import com.app.cardsapplication.models.Card;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
public class CollectionCreationController {


    @FXML
    private ListView<Card> cardsList;

    @FXML
    private TextField collectionNameField;

    @FXML
    private Button createButton;

    @FXML
    private ImageView returnButton;


    @FXML
    void initialize() {
        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "collection-edit-view.fxml"));

        createButton.setOnAction(event -> {
            if (!collectionNameField.getText().isEmpty()) {
                MongoDatabase db = MongoUtil.getDatabase();
                MongoCollection<Collection> collections
                        = db.getCollection("collections", Collection.class);
                collections.insertOne(new Collection(collectionNameField.getText(), cardsList.getItems()));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Notification");
                alert.setHeaderText("Collection was successfully created!");
                alert.setContentText("Your new collection was added to database!");
                alert.showAndWait();
            }
        });
    }


}
