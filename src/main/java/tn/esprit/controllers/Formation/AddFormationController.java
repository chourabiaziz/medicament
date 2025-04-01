package tn.esprit.controllers.Formation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Formation;
import tn.esprit.services.FormationService;

import java.io.File;

public class AddFormationController {


    
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextField videoField;
    @FXML private CheckBox visibleCheck;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final FormationService formationService = new FormationService();

    @FXML
    public void initialize() {
        difficultyCombo.getItems().addAll("Beginner", "Intermediate", "Advanced");
        difficultyCombo.getSelectionModel().selectFirst();

        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
    }

    // Handle the save button click
    private void handleSave() {
        try {
            // Gather the input data
            Formation formation = new Formation();
            formation.setTitre(titleField.getText());
            formation.setDescription(descriptionField.getText());
            formation.setDifficulte(difficultyCombo.getValue());
            formation.setVideo(videoField.getText());  // Set the video file path
            formation.setShown(visibleCheck.isSelected());

            // Save the formation to the database
            formationService.ajouter(formation);

            // Close the window
            ((Stage) saveBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Error", "Failed to save formation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Handle the cancel button click
    private void handleCancel() {
        // Close the current window
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }

    // Method to show file chooser for selecting video
    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi", "*.mov"));
        fileChooser.setTitle("Select Video File");

        // Open the file chooser and wait for the user to select a file
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Set the path of the selected video file to the text field
            videoField.setText(selectedFile.getAbsolutePath());
        }
    }

    // Method to show alert messages
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
