package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.models.Statistics;
import com.app.cardsapplication.utils.MongoUtil;
import com.app.cardsapplication.utils.SceneSwitcher;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StatisticsController {

    private Integer wrongAns;
    private Integer mostlyAns;
    private Integer rightAns;

    private String gameMode;

    private Collection collection;


    @FXML
    private Label differenceLabel;

    @FXML
    private Button mainMenuButton;

    @FXML
    private Label mostlyAnsLabel;

    @FXML
    private Label previousLabel;

    @FXML
    private Button restartGameButton;

    @FXML
    private Label resultLabel;

    @FXML
    private Label rightAnsLabel;

    @FXML
    private Label wrongAnsLabel;

    @FXML
    void initialize() {
        mainMenuButton.setOnAction(event -> SceneSwitcher.SwitchScene(event, "main-menu-view.fxml"));
        restartGameButton.setOnAction(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("game-process-view.fxml"));
            try {
                Parent view = fxmlLoader.load();

                GameProcessController controller = fxmlLoader.getController();
                controller.SetupGame(gameMode, collection);

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

    public void setStatistics(Integer wrong, Integer mostly, Integer right, Collection collection, String gameMode) {
        mostlyAns = mostly;
        rightAns = right;
        this.collection = collection;
        this.gameMode = gameMode;
        wrongAns = collection.cards.size() - mostly - right;
        mostlyAnsLabel.setText(mostlyAns.toString());
        wrongAnsLabel.setText(wrongAns.toString());
        rightAnsLabel.setText(rightAns.toString());
        Statistics stat = MongoUtil.getDatabase()
                .getCollection("statistics", Statistics.class)
                .find(Filters.eq("collectionName", collection.getName()))
                .first();
        double progress = (double) (right * 2 + mostly) / (collection.cards.isEmpty() ? 1 : collection.cards.size()) * 100;
        resultLabel.setText( String.format("%.2f",progress)+ "%");
        previousLabel.setText(String.format("%.2f",stat == null ? 0 : stat.progress)+ "%");
        differenceLabel.setText(String.format("%.2f",progress - (stat == null ? 0 : stat.progress)) + "%");
        MongoDatabase db = MongoUtil.getDatabase();
        MongoCollection<Statistics> stats
                = db.getCollection("statistics", Statistics.class);
        if (stat == null) {
            stats.insertOne(new Statistics(collection.name, progress));
        }
        else {
            stats.updateOne(
                    Filters.eq("collectionName", collection.name),
                    Updates.set("progress", progress)
            );
        }

    }

}



