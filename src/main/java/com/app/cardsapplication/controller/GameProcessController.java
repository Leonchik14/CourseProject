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

    private List<Boolean> answeredCards;

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

/*    @FXML
    private Button previousCardButton;*/

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
            if (newValue != null && !answeredCards.get(currentCardInd)) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                String toggleId = selectedRadioButton.getText();
                switch (toggleId) {
                    case "Wrong":
                        updateScoreAction = () -> wrong++;
                        break;
                    case "Mostly":
                        updateScoreAction = () -> mostly++;
                        break;
                    case "Right":
                        updateScoreAction = () -> right++;
                        break;
                }
                // Отметить текущую карточку как отвеченную
                answeredCards.set(currentCardInd, true);
            }
        });

        cardContentContainer.setOnMouseClicked(event -> {
            flipCard();
        });

/*        previousCardButton.setOnAction(event -> {
            updateScoreAction = () -> wrong++;
            answerToggleGroup.selectToggle(wrongButton);
            if (currentCardInd == 0) return;

            --currentCardInd;
            displayCard(gameCollection.cards.get(currentCardInd), true);
            isFrontVisible = true;
        });*/

        nextCardButton.setOnAction(event -> {
            updateScoreAction.run();
            wrongButton.setSelected(true);
            answerToggleGroup.selectToggle(wrongButton);
            if (currentCardInd == gameCollection.cards.size() - 1) {
                finishGameButton.fire();
            }
            if (Objects.equals(gameMode, "Full coverage") && !isCorrect) {
                gameCollection.cards.add(gameCollection.cards.get(currentCardInd));
            }
            ++currentCardInd;
            displayCard(gameCollection.cards.get(currentCardInd), true);
            updateScoreAction = () -> wrong++;
            isFrontVisible = true;
            isCorrect = false;
        });

        finishGameButton.setOnAction(event -> {
            updateScoreAction = () -> wrong++;
            FXMLLoader fxmlLoader = new FXMLLoader(CardsApplication.class.getResource("statistics-view.fxml"));
            try {
                // Сначала загружаем view
                Parent view = fxmlLoader.load();

                // Теперь получаем контроллер и передаем в него данные
                StatisticsController controller = fxmlLoader.getController();
                controller.setStatistics(wrong, mostly,right, gameCollection, gameMode);

                // Теперь устанавливаем сцену
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
                if (card.getFrontFile().endsWith(".mp3")) {

                    Label textLabel = new Label("File was corrupted or deleted");
                    textLabel.setFont(new Font("System", 18));
                    textLabel.setTextFill(Color.WHITE);
                    cardContentContainer.getChildren().add(textLabel);
                }
                else {
                    ImageView imageView = null;
                    try {
                        imageView = new ImageView(new Image(new FileInputStream(card.getFrontFile())));
                        imageView.setFitWidth(435);
                        imageView.setFitHeight(182);
                        imageView.setPreserveRatio(true);
                    } catch (FileNotFoundException e) {
                        Label textLabel = new Label("File was corrupted or deleted");
                        textLabel.setFont(new Font("System", 18));
                        textLabel.setTextFill(Color.WHITE);
                        cardContentContainer.getChildren().add(textLabel);
                    }
                    cardContentContainer.getChildren().add(imageView);
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
                if (card.getBackFile().endsWith(".mp3")) {
                    Label textLabel = new Label("File was corrupted or deleted");
                    textLabel.setFont(new Font("System", 18));
                    textLabel.setTextFill(Color.WHITE);
                    cardContentContainer.getChildren().add(textLabel);
                }
                else {
                    ImageView imageView = null;
                    try {
                        imageView = new ImageView(new Image(new FileInputStream(card.getBackFile())));
                        imageView.setFitWidth(435);
                        imageView.setFitHeight(182);
                        imageView.setPreserveRatio(true);
                    } catch (FileNotFoundException e) {
                        Label textLabel = new Label("File was corrupted or deleted");
                        textLabel.setFont(new Font("System", 18));
                        textLabel.setTextFill(Color.WHITE);
                        cardContentContainer.getChildren().add(textLabel);
                    }
                    cardContentContainer.getChildren().add(imageView);
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
        answeredCards = new ArrayList<>(Collections.nCopies(gameCollection.cards.size(), false));
        if (!Objects.equals(gameMode, "Common")) {
            Collections.shuffle(gameCollection.cards);
        }
        displayCard(gameCollection.cards.get(currentCardInd), isFrontVisible);

    }
}
