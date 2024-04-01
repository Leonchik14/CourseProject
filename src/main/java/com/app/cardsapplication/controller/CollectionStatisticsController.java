package com.app.cardsapplication.controller;

import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.models.Statistics;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class CollectionStatisticsController  {

    @FXML
    private ImageView returnButton;

    @FXML
    private ListView<Statistics> statsList;

    @FXML
    void initialize() {
        returnButton.setOnMouseClicked(event -> SceneSwitcher.SwitchScene(event, "main-menu-view.fxml"));
        ObservableList<Statistics> items = FXCollections.observableArrayList();

        List<Statistics> stats = MongoUtil.getDatabase().getCollection("statistics", Statistics.class).find().into(new ArrayList<>());
        items.addAll(stats);

        statsList.setItems(items);


        statsList.setCellFactory(new Callback<ListView<Statistics>, ListCell<Statistics>>() {
            @Override
            public ListCell<Statistics> call(ListView<Statistics> listView) {
                return new ListCell<Statistics>() {
                    @Override
                    protected void updateItem(Statistics statistics, boolean empty) {
                        super.updateItem(statistics, empty);

                        if (empty || statistics == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText("Collection name: " + statistics.collectionName + ", Learning progress: " + statistics.progress + "%");
                            setStyle("-fx-font-size: 20px;");
                        }
                    }
                };
            }
        });
    }

}

