package tn.esprit.controllers.Formation.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Formation;
import tn.esprit.entities.Quiz;
import tn.esprit.services.FormationService;
import tn.esprit.services.QuizService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class ShowFormationController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label difficultyLabel;
    @FXML private VBox videoContainer;
    @FXML private FlowPane quizContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Button submitButton;

    private Formation formation;
    private final QuizService quizService = new QuizService();
    private final FormationService formationService = new FormationService();
    private MediaPlayer mediaPlayer;
    private File selectedImageFile;
    private Map<Quiz, ToggleGroup> quizToggleGroups = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        quizContainer.setPadding(new Insets(15));
        quizContainer.setHgap(20);
        quizContainer.setVgap(20);

        submitButton.setOnAction(event -> handleSubmit());
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        loadFormationData();
        loadQuizzes();
    }

    private ImageView loadQuizImage(String imageName) {
        try {
            Path imagePath = Paths.get("src/main/resources/quiz_images", imageName);
            if (Files.exists(imagePath)) {
                Image image = new Image(imagePath.toUri().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(700);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                return imageView;
            } else {
                System.err.println("Image file not found: " + imagePath.toString());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    private void loadQuizzes() {
        quizContainer.getChildren().clear();
        quizToggleGroups.clear();
        List<Quiz> quizzes = quizService.getQuizzesFromFormationId(formation.getId());

        for (Quiz quiz : quizzes) {
            quizContainer.getChildren().add(createQuizCard(quiz));
        }
    }

    private VBox createQuizCard(Quiz quiz) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 15; -fx-border-color: #ddd; -fx-border-radius: 5; " +
                "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        // Image Container
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(150);
        imageContainer.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 3;");

        if (quiz.getImage() != null && !quiz.getImage().isEmpty()) {
            try {
                ImageView imageView = loadQuizImage(quiz.getImage());
                if (imageView != null) {
                    imageContainer.getChildren().add(imageView);
                } else {
                    addPlaceholderLabel(imageContainer, "Image Not Found");
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                addPlaceholderLabel(imageContainer, "Image Load Error");
            }
        } else {
            addPlaceholderLabel(imageContainer, "No Image Available");
        }

        // Quiz Info
        VBox infoBox = new VBox(5);
        infoBox.setPadding(new Insets(5));

        Label questionLabel = new Label("Question:");
        questionLabel.setStyle("-fx-font-weight: bold;");

        Label questionText = new Label(quiz.getCorrect());
        questionText.setWrapText(true);
        questionText.setMaxWidth(280);

        // Create answers list and shuffle them
        List<String> answers = new ArrayList<>();
        answers.add(quiz.getCorrect());
        answers.add(quiz.getIncorrect1());
        answers.add(quiz.getIncorrect2());
        Collections.shuffle(answers);

        // Create radio buttons for answers
        ToggleGroup toggleGroup = new ToggleGroup();
        VBox answersBox = new VBox(5);
        for (String answer : answers) {
            RadioButton radioButton = new RadioButton(answer);
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setWrapText(true);
            radioButton.setMaxWidth(280);
            answersBox.getChildren().add(radioButton);
        }

        // Store the toggle group for later use
        quizToggleGroups.put(quiz, toggleGroup);

        infoBox.getChildren().addAll(
                questionLabel, questionText,
                new Separator(),
                new Label("Answers:"), answersBox
        );

        card.getChildren().addAll(imageContainer, infoBox);
        return card;
    }

    private void handleSubmit() {
        // Check if all questions are answered
        boolean allAnswered = quizToggleGroups.values().stream()
                .allMatch(tg -> tg.getSelectedToggle() != null);

        if (!allAnswered) {
            showAlert("Incomplete Quiz", "Please answer all questions before submitting.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Calculate score
        int correctAnswers = 0;
        int totalQuestions = quizToggleGroups.size();

        for (Map.Entry<Quiz, ToggleGroup> entry : quizToggleGroups.entrySet()) {
            Quiz quiz = entry.getKey();
            RadioButton selected = (RadioButton) entry.getValue().getSelectedToggle();

            if (selected != null && selected.getText().equals(quiz.getCorrect())) {
                correctAnswers++;
            }
        }

        // Calculate score out of 20
        double score = (double) correctAnswers / totalQuestions * 20;

        try {
            // Load the results view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/Formation/client/QuizResults.fxml"));
            Parent root = loader.load();

            // Pass results to the controller
            QuizResultsController resultsController = loader.getController();
            resultsController.setResults(correctAnswers, totalQuestions, score);

            // Create new stage for results
            Stage resultsStage = new Stage();
            resultsStage.setScene(new Scene(root));
            resultsStage.setTitle("Quiz Results");
            resultsStage.centerOnScreen();

            // Close current quiz window
            Stage currentStage = (Stage) submitButton.getScene().getWindow();
            currentStage.close();

            // Show results window
            resultsStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to show results: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void addPlaceholderLabel(StackPane container, String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #666;");
        container.getChildren().add(label);
    }

    private void loadFormationData() {
        titleLabel.setText(formation.getTitre());
        descriptionLabel.setText(formation.getDescription());
        difficultyLabel.setText("Difficulty: " + formation.getDifficulte());

        if (formation.getVideo() != null && !formation.getVideo().isEmpty()) {
            loadVideo(formation.getVideo());
        }
    }

    private void loadVideo(String videoPath) {
        try {
            videoContainer.getChildren().clear();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            Path videoFullPath = Paths.get("src/main/resources/images_videos/", videoPath);
            if (!Files.exists(videoFullPath)) {
                showAlert("Video Not Found", "Video file not found at: " + videoFullPath, Alert.AlertType.ERROR);
                return;
            }

            Media media = new Media(videoFullPath.toUri().toString());
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            mediaView.setFitWidth(600);
            mediaView.setPreserveRatio(true);

            // Video controls
            HBox controls = new HBox(10);
            controls.setStyle("-fx-alignment: center; -fx-padding: 10;");

            Button playButton = new Button("Play");
            playButton.setOnAction(e -> mediaPlayer.play());

            Button pauseButton = new Button("Pause");
            pauseButton.setOnAction(e -> mediaPlayer.pause());

            Button stopButton = new Button("Stop");
            stopButton.setOnAction(e -> mediaPlayer.stop());

            controls.getChildren().addAll(playButton, pauseButton, stopButton);
            videoContainer.getChildren().addAll(mediaView, controls);

        } catch (Exception e) {
            showAlert("Error", "Failed to load video: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}