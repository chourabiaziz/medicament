package tn.esprit.controllers.Formation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Formation;
import tn.esprit.entities.Quiz;
import tn.esprit.services.FormationService;
import tn.esprit.services.QuizService;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

public class ShowFormationController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label difficultyLabel;
    @FXML private VBox videoContainer;
    @FXML private FlowPane quizContainer;
    @FXML private ScrollPane scrollPane;

    private Formation formation;
    private final QuizService quizService = new QuizService();
    private final FormationService formationService = new FormationService();
    private MediaPlayer mediaPlayer;
    private File selectedImageFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        quizContainer.setPadding(new Insets(15));
        quizContainer.setHgap(20);
        quizContainer.setVgap(20);
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
                imageView.setFitWidth(700); // Set desired width
                imageView.setFitHeight(300); // Set desired height
                imageView.setPreserveRatio(true); // Preserve aspect ratio
                imageView.setSmooth(true); // Enable smooth scaling
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

        Label incorrectLabel = new Label("Incorrect Answers:");
        incorrectLabel.setStyle("-fx-font-weight: bold;");

        Label incorrect1 = new Label("- " + quiz.getIncorrect1());
        Label incorrect2 = new Label("- " + quiz.getIncorrect2());

        infoBox.getChildren().addAll(
                questionLabel, questionText,
                new Separator(),
                incorrectLabel, incorrect1, incorrect2
        );

        // Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 80;");
        editButton.setOnAction(e -> handleEditQuiz(quiz));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-min-width: 80;");
        deleteButton.setOnAction(e -> handleDeleteQuiz(quiz));

        buttonBox.getChildren().addAll(editButton, deleteButton);
        card.getChildren().addAll(imageContainer, infoBox, buttonBox);

        return card;
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

    @FXML
    private void handleAddQuiz() {
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Add New Quiz");

        // Form fields
        Button selectImageButton = new Button("Select Image");
        Label imageLabel = new Label("No image selected");

        TextField incorrect1Field = new TextField();
        incorrect1Field.setPromptText("Incorrect Answer 1");

        TextField incorrect2Field = new TextField();
        incorrect2Field.setPromptText("Incorrect Answer 2");

        TextField correctField = new TextField();
        correctField.setPromptText("Correct Answer");

        CheckBox responseCheckBox = new CheckBox("Is Correct?");

        selectImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Quiz Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            selectedImageFile = fileChooser.showOpenDialog(null);
            if (selectedImageFile != null) {
                imageLabel.setText(selectedImageFile.getName());
            }
        });

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Quiz Image:"),
                new HBox(10, selectImageButton, imageLabel),
                new Label("Incorrect Answers:"),
                new HBox(10, incorrect1Field, incorrect2Field),
                new Label("Correct Answer:"), correctField,
                responseCheckBox
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Quiz quiz = new Quiz();
                quiz.setFormation_id(formation.getId());

                if (selectedImageFile != null) {
                    try {
                        String imagePath = saveImageFile(selectedImageFile);
                        quiz.setImage(imagePath);
                    } catch (IOException e) {
                        showAlert("Error", "Failed to save image: " + e.getMessage(), Alert.AlertType.ERROR);
                        return null;
                    }
                }

                quiz.setIncorrect1(incorrect1Field.getText());
                quiz.setIncorrect2(incorrect2Field.getText());
                quiz.setCorrect(correctField.getText());
                quiz.setReponse(responseCheckBox.isSelected());
                return quiz;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(quiz -> {
            try {
                quizService.ajouter(quiz);
                loadQuizzes();
                selectedImageFile = null;
            } catch (Exception e) {
                showAlert("Error", "Failed to add quiz: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private String saveImageFile(File imageFile) throws IOException {
        // Ensure that the file name is preserved as it is
        Path targetDir = Paths.get("src/main/resources/quiz_images/");


        // Use the original file name without any additional modification
        String fileName = imageFile.getName();  // Keep the original file name (e.g., "1743359006444_x.png")
        Path destination = targetDir.resolve(fileName);

        // Copy the image to the target directory
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return fileName;  // Return the original file name (without modification)
    }


    private void handleDeleteQuiz(Quiz quiz) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this quiz?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    quizService.supprimer(quiz.getId());
                    loadQuizzes();
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete quiz: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleEditQuiz(Quiz quiz) {
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Edit Quiz");

        // Form fields with current values
        TextField incorrect1Field = new TextField(quiz.getIncorrect1());
        TextField incorrect2Field = new TextField(quiz.getIncorrect2());
        TextField correctField = new TextField(quiz.getCorrect());
        CheckBox responseCheckBox = new CheckBox("Is Correct?");
        responseCheckBox.setSelected(quiz.getReponse());

        Button selectImageButton = new Button("Change Image");
        Label imageLabel = new Label(quiz.getImage() != null ? quiz.getImage() : "No image selected");

        selectImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Quiz Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            selectedImageFile = fileChooser.showOpenDialog(null);
            if (selectedImageFile != null) {
                imageLabel.setText(selectedImageFile.getName());
            }
        });

        dialog.getDialogPane().setContent(new VBox(10,
                new Label("Current Image:"),
                new HBox(10, selectImageButton, imageLabel),
                new Label("Incorrect Answers:"),
                new HBox(10,
                        new VBox(5, new Label("Answer 1:"), incorrect1Field),
                        new VBox(5, new Label("Answer 2:"), incorrect2Field)
                ),
                new VBox(5, new Label("Correct Answer:"), correctField),
                responseCheckBox
        ));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                quiz.setIncorrect1(incorrect1Field.getText());
                quiz.setIncorrect2(incorrect2Field.getText());
                quiz.setCorrect(correctField.getText());
                quiz.setReponse(responseCheckBox.isSelected());

                if (selectedImageFile != null) {
                    try {
                        String newImagePath = saveImageFile(selectedImageFile);
                        quiz.setImage(newImagePath);
                    } catch (IOException e) {
                        showAlert("Error", "Failed to save image: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
                return quiz;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedQuiz -> {
            try {
                quizService.modifier(updatedQuiz);
                loadQuizzes();
                selectedImageFile = null;
            } catch (Exception e) {
                showAlert("Error", "Failed to update quiz: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
