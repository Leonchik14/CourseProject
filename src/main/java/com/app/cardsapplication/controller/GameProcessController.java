package com.app.cardsapplication.controller;

import com.app.cardsapplication.models.Collection;
import com.app.cardsapplication.models.Card;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameProcessController {


    @Setter
    private String gameMode;

    @Setter
    private Collection gameCollection;

    @FXML
    private StackPane cardContentContainer;

    @FXML
    private Button finishGameButton;

    @FXML
    private RadioButton mostlyButton;

    @FXML
    private Button nextCardButton;

    @FXML
    private Button previousCardButton;

    @FXML
    private RadioButton rightButton;

    @FXML
    private RadioButton wrongButton;

    @FXML
    void initialize() {

    }

    public void displayCard(Card card, boolean front) {
        cardContentContainer.getChildren().clear();
        if (front) {
            if (card.getFrontText() != null && !card.getFrontText().isEmpty()) {
                Label textLabel = new Label(card.getFrontText());
                cardContentContainer.getChildren().add(textLabel);
            } else {
                if (card.getFrontFile().endsWith(".mp3")) {
                    /*Media audio = new Media(new File(card.getFrontFile()).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(audio);
*/
                }
                else {
                    ImageView imageView = null;
                    try {
                        imageView = new ImageView(new Image(new FileInputStream(card.getFrontFile())));
                    } catch (FileNotFoundException e) {
                        Label textLabel = new Label("File was corrupted or deleted");
                        cardContentContainer.getChildren().add(textLabel);
                    }
                    cardContentContainer.getChildren().add(imageView);
                }
            }
        }

    }
}
