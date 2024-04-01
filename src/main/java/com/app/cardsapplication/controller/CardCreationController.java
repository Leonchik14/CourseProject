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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Objects;
import java.util.UUID;

public class CardCreationController {

    @Setter
    private Collection collection;
    private File frontFile;

    private Card toEdit;

    private boolean isEditing = false;

    private File backFile;

    @FXML
    private Label frontFilePath;

    @FXML
    private Label backFilePath;

    @FXML
    private Button chooseFileBack;

    @FXML
    private Button chooseFileFront;

    @FXML
    private Button createButton;

    @FXML
    private ImageView returnButton;

    @FXML
    private TextField textBack;

    @FXML
    private TextField textFront;

    @FXML
    void initialize() {

        chooseFileFront.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "Media Files", "*.jpg", "*.jpeg", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            frontFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            frontFilePath.setText(frontFile.getAbsolutePath());
        });

        chooseFileBack.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "Media Files", "*.jpg", "*.jpeg", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            backFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            backFilePath.setText(backFile.getAbsolutePath());
        });

        createButton.setOnAction(event -> {
            Card toInsert = new Card();
            if (!textFront.getText().isEmpty()) {
                toInsert.setFrontText(textFront.getText());
            } else if (frontFile != null) {
                toInsert.setFrontFile(frontFile.getAbsolutePath());
            } else {
                Shake frontTextAnim = new Shake(textFront);
                Shake frontFileAnim = new Shake(chooseFileFront);
                frontFileAnim.PlayAnim();
                frontTextAnim.PlayAnim();
                return;
            }

            if (!textBack.getText().isEmpty()) {
                toInsert.setBackText(textBack.getText());
            } else if (backFile != null) {
                toInsert.setBackFile(backFile.getAbsolutePath());
            } else {
                Shake backTextAnim = new Shake(textBack);
                Shake backFileAnim = new Shake(chooseFileBack);
                backFileAnim.PlayAnim();
                backTextAnim.PlayAnim();
                return;
            }
            MongoDatabase db = MongoUtil.getDatabase();
            MongoCollection<Card> cards
                    = db.getCollection("cards", Card.class);
            MongoCollection<Collection> collections = db.getCollection("collections", Collection.class);
            if (isEditing) {
                ObjectId cardId = Tools.GetCardId(toEdit);

                cards.deleteOne(Filters.eq("_id", cardId));

                Document newCardDocument = new Document()
                        .append("frontText", toInsert.getFrontText())
                        .append("backText", toInsert.getBackText())
                        .append("frontFile", toInsert.getFrontFile())
                        .append("backFile", toInsert.getBackFile());
                cards.insertOne(toInsert);

                collection.cards.remove(toEdit);
                collection.cards.add(toInsert);

                collections.deleteOne(Filters.eq("name", collection.getName()));
                collections.insertOne(collection);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Notification");
                alert.setHeaderText("Card was successfully changed!");
                alert.setContentText("Your card was updated in the database!");
                alert.showAndWait();
            } else {
                cards.insertOne(toInsert);
                collections.updateOne(
                        Filters.eq("name", collection.name),
                        Updates.push("cards", toInsert)
                );
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Notification");
                alert.setHeaderText("Card was successfully created!");
                alert.setContentText("Your new card was added to database!");
                alert.showAndWait();
            }
        });


        returnButton.setOnMouseClicked(event -> {
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
        });
    }

    public void SetupEditing(Card card) {
        toEdit = card;
        isEditing = true;
        if (card.getFrontText() != null) {
            textFront.setText(card.getFrontText());
        }
        else {
            frontFilePath.setText(card.getFrontFile());
            frontFile = new File(card.getFrontFile());
        }
        if (card.getBackText() != null) {
            textBack.setText(card.getBackText());
        }
        else {
            backFilePath.setText(card.getBackFile());
            backFile = new File(card.getBackFile());
        }
    }
}
