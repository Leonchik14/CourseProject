package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Card;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardEditingController {

    @Setter
    private Collection collection;

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
        ObservableList<Card> items = FXCollections.observableArrayList();

        List<Card> cards = MongoUtil.getDatabase().getCollection("cards", Card.class).find().into(new ArrayList<>());
        items.addAll(cards);

        cardsList.setItems(items);

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
        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "collection-edit-view.fxml"));
    }

    public void initializeCollection(Collection collection) {
        this.collection = collection;
        collectionName.setText(collection.name);
    }

}
