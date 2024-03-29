package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Card;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import com.app.cardsapplication.utils.Shake;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.bson.BsonValue;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class CardCreationController {

    @Setter
    private Collection collection;
    private File frontFile;

    private File backFile;


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

            // Установка фильтра расширений
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "Media Files", "*.mp3", "*.jpg", "*.jpeg", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            // Показываем проводник файлов
            frontFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        });

        chooseFileBack.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");

            // Установка фильтра расширений
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "Media Files", "*.mp3", "*.jpg", "*.jpeg", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            // Показываем проводник файлов
            backFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        });

        createButton.setOnAction(event -> {
            Card toInsert = new Card();
            if (!textFront.getText().isEmpty()) {
                toInsert.setFrontText(textFront.getText());
            }
            else if (frontFile != null) {
                toInsert.setFrontFile(frontFile.getAbsolutePath());
            }
            else {
                Shake frontTextAnim = new Shake(textFront);
                Shake frontFileAnim = new Shake(chooseFileFront);
                frontFileAnim.PlayAnim();
                frontTextAnim.PlayAnim();
                return;
            }

            if (!textBack.getText().isEmpty()) {
                toInsert.setBackText(textBack.getText());
            }
            else if (backFile != null) {
                toInsert.setBackFile(backFile.getAbsolutePath());
            }
            else {
                Shake backTextAnim = new Shake(textBack);
                Shake backFileAnim = new Shake(chooseFileBack);
                backFileAnim.PlayAnim();
                backTextAnim.PlayAnim();
                return;
            }
            MongoDatabase db = MongoUtil.getDatabase();
            MongoCollection<Card> cards
                    = db.getCollection("cards", Card.class);
            cards.insertOne(toInsert).getInsertedId();
            MongoCollection<Collection> collections = db.getCollection("collections", Collection.class);
            collections.updateOne(
                    Filters.eq("name", collection.name),
                    Updates.push("cards", toInsert)
            );
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Notification");
            alert.setHeaderText("Card was successfully created!");
            alert.setContentText("Your new card was added to database!");
            alert.showAndWait();
        });


        returnButton.setOnMouseClicked(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("card-edit-view.fxml"));
            try {
                // Сначала загружаем view
                Parent view = fxmlLoader.load();

                // Теперь получаем контроллер и передаем в него данные
                CardEditingController controller = fxmlLoader.getController();
                controller.initializeCollection(collection);

                // Теперь устанавливаем сцену
                Scene scene = new Scene(view, 700, 400);
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setTitle("CardsApplication");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Лучше здесь использовать логгирование или другие методы обработки ошибок
            }
        });
    }
}
