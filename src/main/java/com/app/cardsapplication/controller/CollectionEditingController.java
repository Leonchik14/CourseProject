package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Card;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectionEditingController {

    @FXML
    private Button createButton;

    @FXML
    private ImageView returnButton;

    @FXML
    private ListView<Collection> collectionsList;

    @FXML
    void initialize() {

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
                            setStyle("-fx-font-size: 20px;");
                        }
                    }
                };
            }
        });
        collectionsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Collection selectedCollection = collectionsList.getSelectionModel().getSelectedItem();
                if (selectedCollection != null) {
                    FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("card-edit-view.fxml"));
                    try {
                        // Сначала загружаем view
                        Parent view = fxmlLoader.load();

                        // Теперь получаем контроллер и передаем в него данные
                        CardEditingController controller = fxmlLoader.getController();
                        controller.initializeCollection(selectedCollection);

                        // Теперь устанавливаем сцену
                        Scene scene = new Scene(view, 700, 400);
                        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        primaryStage.setTitle("CardsApplication");
                        primaryStage.setScene(scene);
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace(); // Лучше здесь использовать логгирование или другие методы обработки ошибок
                    }
                }
            }
        });
        createButton.setOnAction(event -> SceneSwitcher.SwitchScene(event, "collection-creation-view.fxml"));

        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "main-menu-view.fxml"));
    }

}
