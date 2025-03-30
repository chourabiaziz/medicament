package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import tn.esprit.entities.Formation;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

public class ShowFormationController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label noteLabel;
    @FXML private VBox videoContainer;  // A VBox to hold the MediaView

    private Formation formation;

    @FXML
    public void initialize() {
        // Initialize the labels with the formation's details
        if (formation != null) {
            titleLabel.setText(formation.getTitre());
            descriptionLabel.setText(formation.getDescription());
            difficultyLabel.setText("Difficulty: " + formation.getDifficulte());
            noteLabel.setText("Rating: " + formation.getNote());

            // Load the video if it's available
            loadVideo(formation.getVideo());
        }
    }

    // Set the formation object from the navigation
    public void setFormation(Formation formation) {
        this.formation = formation;
        initialize();  // Reinitialize the UI with the new data
    }

    // Method to load and display the video
    private void loadVideo(String videoPath) {
        try {
            // Manually construct the full path for the video file under the 'resources' directory
            String videoFullPath = "src/main/resources/images_videos/" + videoPath;
            File videoFile = new File(videoFullPath);

            // Check if the file exists
            if (!videoFile.exists()) {
                showError("Video File Not Found", "The video file could not be found at: " + videoFullPath);
                return;
            }

            // Convert the file to a URI for the Media object
            URI videoURI = videoFile.toURI();

            // Create a Media object using the URI of the video
            Media media = new Media(videoURI.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            // Set the fit width and height for the video display
            mediaView.setFitWidth(640);
            mediaView.setFitHeight(360);

            // Add the MediaView to the video container (VBox or whatever container you're using)
            videoContainer.getChildren().add(mediaView);

            // Play the video
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error Loading Video", "An error occurred while trying to load the video.");
        }
    }

    // Method to display error messages
    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to go back to the previous window
    @FXML
    public void goBack() {
        // Close the current window
        if (titleLabel.getScene() != null) {
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.close();  // Close the current stage
        }
    }
}
