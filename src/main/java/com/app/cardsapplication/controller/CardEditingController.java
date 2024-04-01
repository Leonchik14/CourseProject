package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Card;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import com.app.cardsapplication.utils.Shake;
import com.app.cardsapplication.utils.Tools;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
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
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardEditingController {

    @Setter
    private Collection collection;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button addCardButton;

    @FXML
    private ListView<Card> cardsList;

    @FXML
    private Label collectionName;

    @FXML
    private ImageView returnButton;

    @FXML
    void initialize() {


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


        addCardButton.setOnAction(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("card-creation-view.fxml"));
            try {
                Parent view = fxmlLoader.load();

                CardCreationController controller = fxmlLoader.getController();
                controller.setCollection(collection);

                Scene scene = new Scene(view, 700, 400);
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setTitle("CardsApplication");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        editButton.setOnAction(event -> {
            if (!cardsList.getSelectionModel().getSelectedItems().isEmpty()) {
                MongoDatabase db = MongoUtil.getDatabase();
                MongoCollection<Card> collections
                        = db.getCollection("card", Card.class);
                FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("card-creation-view.fxml"));
                try {
                    Parent view = fxmlLoader.load();

                    CardCreationController controller = fxmlLoader.getController();
                    controller.setCollection(collection);
                    controller.SetupEditing(cardsList.getSelectionModel().getSelectedItems().getFirst());

                    Scene scene = new Scene(view, 700, 400);
                    Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    primaryStage.setTitle("CardsApplication");
                    primaryStage.setScene(scene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Shake nameAnim = new Shake(cardsList);
                nameAnim.PlayAnim();
            }
        });

        deleteButton.setOnAction(event -> {
            if (!cardsList.getSelectionModel().getSelectedItems().isEmpty()) {
                Card toDelete = cardsList.getSelectionModel().getSelectedItem();
                MongoDatabase db = MongoUtil.getDatabase();
                MongoCollection<Card> cards
                        = db.getCollection("cards", Card.class);
                MongoCollection<Collection> collections = db.getCollection("collections", Collection.class);

                collection.cards.remove(toDelete);

                collections.deleteOne(Filters.eq("name", collection.getName()));
                collections.insertOne(collection);
                FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("card-edit-view.fxml"));
                try {
                    Parent view = fxmlLoader.load();

                    CardEditingController controller = fxmlLoader.getController();
                    controller.initializeCollection(collection);

                    Scene scene = new Scene(view, 700, 400);
                    Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    primaryStage.setTitle("CardsApplication");
                    primaryStage.setScene(scene);
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Shake nameAnim = new Shake(cardsList);
                nameAnim.PlayAnim();
            }
        });

        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "collection-edit-view.fxml"));
    }

    public void initializeCollection(Collection collection) {
        this.collection = collection;
        collectionName.setText(collection.name);
        ObservableList<Card> items = FXCollections.observableArrayList();


        List<Card> cards =  Objects.requireNonNull(MongoUtil.getDatabase()
                .getCollection("collections", Collection.class)
                .find(Filters.eq("name", collection.getName()))
                .first())
                .cards;

        items.addAll(cards);

        cardsList.setItems(items);
    }

}
