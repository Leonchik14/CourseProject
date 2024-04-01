package com.app.cardsapplication.controller;

import com.app.cardsapplication.models.Card;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import com.app.cardsapplication.utils.Shake;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        cardsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cardsList.setCellFactory(new Callback<ListView<Card>, ListCell<Card>>() {
            @Override
            public ListCell<Card> call(ListView<Card> listView) {
                return new ListCell<Card>() {
                    @Override
                    protected void updateItem(Card card, boolean empty) {
                        super.updateItem(card, empty);

                        if (empty || card == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText("Front: "
                                    + (Objects.equals(card.getFrontText(), null) ? card.getFrontFile() : card.getFrontText())
                                    + "\n" + "Back: "
                                    + (Objects.equals(card.getBackText(), null) ? card.getBackFile() : card.getBackText()));
                        }
                    }
                };
            }
        });

        ObservableList<Card> items = FXCollections.observableArrayList();

        List<Card> cards =  Objects.requireNonNull(MongoUtil.getDatabase()
                .getCollection("cards", Card.class)
                .find()
                .into(new ArrayList<>()));

        items.addAll(cards);

        cardsList.setItems(items);

        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "collection-edit-view.fxml"));

        createButton.setOnAction(event -> {
            if (!collectionNameField.getText().isEmpty()) {
                MongoDatabase db = MongoUtil.getDatabase();
                MongoCollection<Collection> collections
                        = db.getCollection("collections", Collection.class);
                if (collections.find(Filters.eq("name", collectionNameField.getText())).first() != null) {
                    Shake nameAnim = new Shake(collectionNameField);
                    nameAnim.PlayAnim();
                    return;
                }
                collections.insertOne(new Collection(collectionNameField.getText(), cardsList.getSelectionModel().getSelectedItems()));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Notification");
                alert.setHeaderText("Collection was successfully created!");
                alert.setContentText("Your new collection was added to database!");
                alert.showAndWait();
            }
            else {
                Shake nameAnim = new Shake(collectionNameField);
                nameAnim.PlayAnim();
            }
        });
    }


}
