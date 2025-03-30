package tn.esprit.controllers.Formation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Formation;
import tn.esprit.services.FormationService;

public class EditFormationController {

    @FXML private TextField idField;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private Slider ratingSlider;
    @FXML private TextField videoField;
    @FXML private CheckBox visibleCheck;
    @FXML private Button updateBtn;
    @FXML private Button cancelBtn;

    private Formation currentFormation;
    private final FormationService formationService = new FormationService();

    public void setFormationData(Formation formation) {
        this.currentFormation = formation;
        populateForm();
    }

    @FXML
    public void initialize() {
        difficultyCombo.getItems().addAll("Beginner", "Intermediate", "Advanced");

        updateBtn.setOnAction(e -> handleUpdate());
        cancelBtn.setOnAction(e -> handleCancel());
    }

    private void populateForm() {
        if (currentFormation != null) {
             titleField.setText(currentFormation.getTitre());
            descriptionField.setText(currentFormation.getDescription());
            difficultyCombo.setValue(currentFormation.getDifficulte());
            ratingSlider.setValue(currentFormation.getNote());
            videoField.setText(currentFormation.getVideo());
            visibleCheck.setSelected(currentFormation.isShown());
        }
    }

    private void handleUpdate() {
        try {
            // Update the current formation object
            currentFormation.setTitre(titleField.getText());
            currentFormation.setDescription(descriptionField.getText());
            currentFormation.setDifficulte(difficultyCombo.getValue());
            currentFormation.setNote((int) ratingSlider.getValue());
            currentFormation.setVideo(videoField.getText());
            currentFormation.setShown(visibleCheck.isSelected());

            // Call service to update in database
            formationService.modifier(currentFormation);

            // Close the window
            ((Stage) updateBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Error", "Failed to update formation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleCancel() {
        ((Stage) cancelBtn.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}