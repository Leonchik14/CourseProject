package com.app.cardsapplication.controller;

import com.app.cardsapplication.CardsApplication;
import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.models.Card;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameProcessController {

    @Setter
    private String gameMode;

    private Runnable updateScoreAction;

    private boolean isFrontVisible;


    private Integer wrong = 0;

    private boolean isCorrect = false;

    private Integer mostly = 0;

    private Integer right = 0;

    private Integer currentCardInd;

    @Setter
    private Collection gameCollection;

    @FXML
    private ToggleGroup answerToggleGroup = new ToggleGroup();

    @FXML
    private StackPane cardContentContainer;

    @FXML
    private Button finishGameButton;

    @FXML
    private Button nextCardButton;

    @FXML
    private RadioButton mostlyButton;

    @FXML
    private RadioButton rightButton;

    @FXML
    private RadioButton wrongButton;

    @FXML
    void initialize() {
        currentCardInd = 0;
        updateScoreAction = () -> wrong++;
        wrongButton.setToggleGroup(answerToggleGroup);
        mostlyButton.setToggleGroup(answerToggleGroup);
        rightButton.setToggleGroup(answerToggleGroup);
        isFrontVisible = true;

        answerToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                String toggleId = selectedRadioButton.getText();
                switch (toggleId) {
                    case "Wrong":
                        updateScoreAction = () -> wrong++;
                        isCorrect = false;
                        break;
                    case "Mostly":
                        updateScoreAction = () -> mostly++;
                        isCorrect = true;
                        break;
                    case "Right":
                        updateScoreAction = () -> right++;
                        isCorrect = true;
                        break;
                }
            }
        });

        cardContentContainer.setOnMouseClicked(event -> {
            flipCard();
        });


        nextCardButton.setOnAction(event -> {
            updateScoreAction.run();

            if (Objects.equals(gameMode, "Full coverage") && !isCorrect) {
                gameCollection.cards.add(gameCollection.cards.get(currentCardInd));
            }

            if (currentCardInd < gameCollection.cards.size() - 1) {
                ++currentCardInd;
            } else {
                finishGameButton.fire();
                return;
            }

            displayCard(gameCollection.cards.get(currentCardInd), isFrontVisible);

            updateScoreAction = () -> wrong++;
            isFrontVisible = true;
            isCorrect = false;

            answerToggleGroup.selectToggle(wrongButton);
        });

        finishGameButton.setOnAction(event -> {
            updateScoreAction = () -> wrong++;
            FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("statistics-view.fxml"));
            try {
                Parent view = fxmlLoader.load();

                StatisticsController controller = fxmlLoader.getController();
                controller.setStatistics(wrong, mostly,right, gameCollection, gameMode);

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

    public void displayCard(Card card, boolean front) {
        cardContentContainer.getChildren().clear();
        if (front) {
            if (card.getFrontText() != null && !card.getFrontText().isEmpty()) {
                Label textLabel = new Label(card.getFrontText());
                textLabel.setFont(new Font("System", 18));
                textLabel.setTextFill(Color.WHITE);
                cardContentContainer.getChildren().add(textLabel);
            } else {
                ImageView imageView = null;
                try {
                    imageView = new ImageView(new Image(new FileInputStream(card.getFrontFile())));
                    imageView.setFitWidth(435);
                    imageView.setFitHeight(182);
                    imageView.setPreserveRatio(true);
                    cardContentContainer.getChildren().add(imageView);
                } catch (Exception e) {
                    Label textLabel = new Label("File was corrupted or deleted");
                    textLabel.setFont(new Font("System", 18));
                    textLabel.setTextFill(Color.WHITE);
                    cardContentContainer.getChildren().add(textLabel);
                }

            }
        }
        else{
            if (card.getBackText() != null && !card.getBackText().isEmpty()) {
                Label textLabel = new Label(card.getBackText());
                textLabel.setFont(new Font("System", 18));
                textLabel.setTextFill(Color.WHITE);
                cardContentContainer.getChildren().add(textLabel);
            } else {
                ImageView imageView = null;
                try {
                    imageView = new ImageView(new Image(new FileInputStream(card.getBackFile())));
                    imageView.setFitWidth(435);
                    imageView.setFitHeight(182);
                    imageView.setPreserveRatio(true);
                    cardContentContainer.getChildren().add(imageView);
                } catch (Exception e) {
                    Label textLabel = new Label("File was corrupted or deleted");
                    textLabel.setFont(new Font("System", 18));
                    textLabel.setTextFill(Color.WHITE);
                    cardContentContainer.getChildren().add(textLabel);
                }
            }
        }
    }

    public void flipCard() {
        ScaleTransition stHide = new ScaleTransition(Duration.millis(300), cardContentContainer);
        stHide.setFromX(1.0);
        stHide.setToX(0.0);
        stHide.setOnFinished(e -> {
            isFrontVisible = !isFrontVisible;
            displayCard(gameCollection.cards.get(currentCardInd), isFrontVisible);
            ScaleTransition stShow = new ScaleTransition(Duration.millis(300), cardContentContainer);
            stShow.setFromX(0.0);
            stShow.setToX(1.0);
            stShow.play();
        });
        stHide.play();
    }

    public void SetupGame(String mode, Collection collection) {
        this.gameCollection = collection;
        this.gameMode = mode;
        if (!Objects.equals(gameMode, "Common")) {
            Collections.shuffle(gameCollection.cards);
        }
        displayCard(gameCollection.cards.get(currentCardInd), isFrontVisible);
    }
}
