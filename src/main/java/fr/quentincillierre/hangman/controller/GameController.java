package fr.quentincillierre.hangman.controller;

import fr.quentincillierre.hangman.model.HangmanModel;
import fr.quentincillierre.hangman.model.WordRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

public class GameController {

    @FXML
    private ImageView hangmanImageView;

    @FXML
    private Label wordLabel;

    @FXML
    private Label clueLabel;

    @FXML
    private Label resultLabel;

    @FXML
    private AnchorPane gamePane;

    @FXML
    private Label roundLabel;

    @FXML
    private Label winsLabel;

    @FXML
    private Label lossesLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Button restartButton;

    @FXML
    private GridPane keyboardGrid;

    private HangmanModel model;
    private final WordRepository wordRepository = new WordRepository();
    
    private final Map<Character, Button> keyboardButtons = new HashMap<>();

    // Tournament
    private final int TOTAL_ROUNDS = 10;
    private int currentRound = 1;
    private int wins = 0;
    private int losses = 0;

    // Timer
    private final int START_TIME = 60;
    private int timeLeft = START_TIME;
    private Timeline timeline;
    private boolean roundFinished = false;

    @FXML
    public void initialize() {
    String wordToGuess = wordRepository.getRandomWord();
    this.model = new HangmanModel(wordToGuess);

    resultLabel.setText("");
    restartButton.setVisible(false);
    keyboardButtons.clear();

    clueLabel.setText(" " + wordRepository.getClueForWord(wordToGuess));

    generateKeyboard();

    styleRestartButton();

    updateScoreBoard();

    makeGameResizable();

    startTimer();

    refreshUI();
}

private void refreshUI() {
   if (model.isLose()) {
    wordLabel.setText(formatWordRepresentation(model.getWordToGuess()));
} else {
    wordLabel.setText(formatWordRepresentation(model.getHiddenWord()));
}
    int wrongs = model.getCurrentWrongs();
    int imageLevel = Math.max(0, wrongs); 
    
    try {
        String imagePath = "/pictures/" + imageLevel + "-hangman.png";        
        java.io.InputStream imgStream = getClass().getResourceAsStream(imagePath);
        if (imgStream != null) {
            Image img = new Image(imgStream);
            hangmanImageView.setImage(img);
        } else {
            System.err.println("Could not find image resource: " + imagePath);
        }
    } catch (Exception e) {
        System.err.println("Could not load image for level: " + imageLevel);
    }

    if (model.isWin() && !roundFinished) {
        
        roundFinished = true;

        if (timeline != null) {
        timeline.stop();
        }

        wins++;
        updateScoreBoard();

        resultLabel.setText("YOU WIN! 😃🎉");
        resultLabel.setStyle("-fx-text-fill: green;");
        
        restartButton.setVisible(true);
       
        disableAllButtons();

    }else if (model.isLose() && !roundFinished) {

        roundFinished = true;

        if (timeline != null) {
        timeline.stop();
        }

        losses++;
        updateScoreBoard();

        resultLabel.setText("GAME OVER! 😢 ");
        resultLabel.setStyle("-fx-text-fill: red;");

        restartButton.setVisible(true);
     
        disableAllButtons();
    }
}
private void generateKeyboard() {

    keyboardGrid.getChildren().clear();

    keyboardGrid.setHgap(12);
    keyboardGrid.setVgap(12);
    keyboardGrid.setAlignment(Pos.CENTER);

    keyboardButtons.clear();

    int[] lettersInRow = {7, 7, 7, 5};

    char currentLetter = 'A';

    for (int row = 0; row < lettersInRow.length; row++) {

        int numberOfLetters = lettersInRow[row];

        int startColumn = (7 - numberOfLetters) / 2;

        for (int col = 0; col < numberOfLetters; col++) {

            final char letter = currentLetter;

            Button btn = new Button(String.valueOf(letter));

            btn.setPrefSize(65, 55);

            String normalStyle =
                    "-fx-background-color: linear-gradient(#D8B078,#8B5A2B);" +
                    "-fx-background-radius:12;" +
                    "-fx-border-color:#5C3A1E;" +
                    "-fx-border-width:3;" +
                    "-fx-border-radius:12;" +
                    "-fx-font-size:18px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-text-fill:#3B220D;";

            String hoverStyle =
                    "-fx-background-color: linear-gradient(#E8C58A,#A66B35);" +
                    "-fx-background-radius:12;" +
                    "-fx-border-color:#6B421F;" +
                    "-fx-border-width:3;" +
                    "-fx-border-radius:12;" +
                    "-fx-font-size:18px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-text-fill:#3B220D;";

            btn.setStyle(normalStyle);

            btn.setOnMouseEntered(e -> {
                btn.setStyle(hoverStyle);
                btn.setScaleX(1.08);
                btn.setScaleY(1.08);
            });

            btn.setOnMouseExited(e -> {
                btn.setStyle(normalStyle);
                btn.setScaleX(1);
                btn.setScaleY(1);
            });

            btn.setOnAction(event -> handleKeyboardInput(String.valueOf(letter)));

            keyboardButtons.put(letter, btn);

            keyboardGrid.add(btn, startColumn + col, row);

            currentLetter++;
        }
    }
}

private void styleRestartButton() {

    String normalStyle =
            "-fx-background-color: #e6aa6f;" +
            "-fx-background-radius:15;" +
            "-fx-border-color:#5C3A1E;" +
            "-fx-border-width:3;" +
            "-fx-border-radius:15;" +
            "-fx-font-size:18px;" +
            "-fx-font-weight:bold;" +
            "-fx-text-fill:#3B220D;" +
            "-fx-cursor:hand;";

    String hoverStyle =
            "-fx-background-color: linear-gradient(#E8C58A,#A66B35);" +
            "-fx-background-radius:15;" +
            "-fx-border-color:#6B421F;" +
            "-fx-border-width:3;" +
            "-fx-border-radius:15;" +
            "-fx-font-size:18px;" +
            "-fx-font-weight:bold;" +
            "-fx-text-fill:#3B220D;" +
            "-fx-cursor:hand;";

    restartButton.setStyle(normalStyle);

    restartButton.setOnMouseEntered(e -> {
        restartButton.setStyle(hoverStyle);
        restartButton.setScaleX(1.08);
        restartButton.setScaleY(1.08);
    });

    restartButton.setOnMouseExited(e -> {
        restartButton.setStyle(normalStyle);
        restartButton.setScaleX(1);
        restartButton.setScaleY(1);
    });

    restartButton.setOnMousePressed(e -> {
        restartButton.setScaleX(0.95);
        restartButton.setScaleY(0.95);
    });

    restartButton.setOnMouseReleased(e -> {
        restartButton.setScaleX(1.08);
        restartButton.setScaleY(1.08);
    });
}

    public void handleKeyboardInput(String character) {
        if (model.isWin() || model.isLose()) {
        return; 
    }



        if (character == null || character.isEmpty() || model.isWin() || model.isLose()) {
            return;
        }

        char inputChar = Character.toUpperCase(character.charAt(0));

        if (inputChar >= 'A' && inputChar <= 'Z') {
            model.tryLetter(inputChar);

            Button btn = keyboardButtons.get(inputChar);
            if (btn != null) {
                btn.setDisable(true);
            }

            refreshUI();
        }
    }

    private void disableAllButtons() {
        for (Button btn : keyboardButtons.values()) {
            btn.setDisable(true);
        }
    }

    private String formatWordRepresentation(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append(word.charAt(i));
            if (i < word.length() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private void updateScoreBoard() {

    roundLabel.setText("Round: " + currentRound + "/" + TOTAL_ROUNDS);

    winsLabel.setText("Wins: " + wins);

    lossesLabel.setText("Losses: " + losses);

    timerLabel.setText("Time: " + timeLeft);
    }

    private void startTimer() {

    if (timeline != null) {
        timeline.stop();
    }

    timeLeft = START_TIME;

    updateScoreBoard();


    timeline = new Timeline(

        new KeyFrame(Duration.seconds(1), e -> {

            timeLeft--;

            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 10) {

                timerLabel.setStyle("-fx-text-fill:red; -fx-font-size:15px; -fx-font-weight:bold;");
            }

            if (timeLeft <= 0) {

                timeline.stop();

                resultLabel.setText("TIME'S UP!");

                resultLabel.setStyle("-fx-text-fill:red;");

                restartButton.setVisible(true);

                wordLabel.setText(formatWordRepresentation(model.getWordToGuess()));

                disableAllButtons();

                updateScoreBoard();
            }

        })

    );

    timeline.setCycleCount(Timeline.INDEFINITE);

    timeline.play();
}
private void endTournament() {

    if (timeline != null) {
        timeline.stop();
    }

    wordLabel.setText("");

    String champion;

    if (wins > losses) {
        champion = "🏆 YOU WON THE TOURNAMENT!";
    } else if (losses > wins) {
        champion = "💀 YOU LOST THE TOURNAMENT!";
    } else {
        champion = "🤝 TOURNAMENT ENDED IN A DRAW!";
    }

    resultLabel.setText(
            champion +
            "\n\nWins: " + wins +
            "\nLosses: " + losses
    );

    resultLabel.setStyle(
            "-fx-text-fill: green;" +
            "-fx-font-size:20px;" +
            "-fx-font-weight:bold;" +
            "-fx-alignment:center;"
    );

    restartButton.setText("New Tournament");
    restartButton.setVisible(true);

    disableAllButtons();
}

private void makeGameResizable() {

    Scale scale = new Scale();
    gamePane.getTransforms().add(scale);

    gamePane.sceneProperty().addListener((obs, oldScene, newScene) -> {

        if (newScene != null) {

            scale.xProperty().bind(
                    newScene.widthProperty().divide(1024));

            scale.yProperty().bind(
                    newScene.heightProperty().divide(1024));
        }
    });
}

    private void startNewGame() {
    String newWord = wordRepository.getRandomWord(); 
    this.model = new HangmanModel(newWord); 
    roundFinished = false;

    clueLabel.setText("Clue: " + wordRepository.getClueForWord(newWord));
    
    resultLabel.setText(""); 

    restartButton.setVisible(false);
    

    
    keyboardGrid.getChildren().forEach(node -> {
        if (node instanceof javafx.scene.control.Button) {
            node.setDisable(false);
        }
    });

refreshUI();

startTimer();

updateScoreBoard();
    }

    @FXML
private void handleRestart() {

    // If the tournament has finished, start a new tournament
    if (restartButton.getText().equals("New Tournament")) {

        currentRound = 1;
        wins = 0;
        losses = 0;

        restartButton.setText("Next...");
        restartButton.setVisible(false);

        startNewGame();
        updateScoreBoard();

        return;
    }

    // Continue to the next round
    if (currentRound < TOTAL_ROUNDS) {

        currentRound++;
        startNewGame();

    } else {

        endTournament();

    }
}
}