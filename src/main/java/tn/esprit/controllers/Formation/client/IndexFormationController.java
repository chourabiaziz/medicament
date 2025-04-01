package tn.esprit.controllers.Formation.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.controllers.Formation.EditFormationController;
import tn.esprit.entities.Formation;
import tn.esprit.navigation.NavigationUtils;
import tn.esprit.services.FormationService;

import java.io.IOException;
import java.util.List;

public class IndexFormationController {

    private final FormationService formationService = new FormationService();
    private final ObservableList<Formation> formationList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 6; // Changed to 6 for 3 items per row (2 rows per page)

    @FXML private GridPane formationGridContainer;
    @FXML private Pagination pagination;
    @FXML private TextField searchField;

    @FXML
    public void initialize() {
        // Load initial data
        loadFormations();
        setupSearchListener();
        setupPaginationListener();
    }

    private void loadFormations() {
        List<Formation> formations = formationService.findAll();
        formationList.clear();
        formationList.addAll(formations);
        refreshList();
    }
    @FXML
    private void refreshList() {
        List<Formation> allFormations = formationList;
        int totalPages = (int) Math.ceil((double) allFormations.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(totalPages == 0 ? 1 : totalPages); // Ensure at least 1 page
        updateFormationList(currentPage);
    }

    private void updateFormationList(int page) {
        formationGridContainer.getChildren().clear();

        List<Formation> allFormations = formationList;
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allFormations.size());
        List<Formation> pageFormations = allFormations.subList(start, end);

        int row = 0;
        int col = 0;

        for (Formation formation : pageFormations) {
            VBox card = createFormationCard(formation);
            formationGridContainer.add(card, col, row);

            col++;
            if (col >= 3) { // 3 cards per row
                col = 0;
                row++;
            }
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5; -fx-border-color: #ddd; -fx-spacing: 10;");
        card.setPrefWidth(300);
        card.setMaxWidth(300);

        Label titleLabel = new Label(formation.getTitre());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-wrap-text: true;");

        Label descriptionLabel = new Label(formation.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555; -fx-wrap-text: true;");
        descriptionLabel.setMaxHeight(60);
        descriptionLabel.setPrefHeight(60);

        Label difficultyLabel = new Label("Difficulty: " + formation.getDifficulte());
        difficultyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");

        // View Details Button
        Button detailButton = new Button("Start");
        detailButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        detailButton.setOnAction(event -> goToDetails(formation));

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 5;");




        buttonBox.getChildren().addAll(detailButton);
        card.getChildren().addAll(titleLabel, descriptionLabel, difficultyLabel, buttonBox);

        return card;
    }

    private void handleDelete(Formation formation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this formation?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                formationService.supprimer(formation.getId());
                loadFormations(); // Reload all formations
            }
        });
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue.trim();
            List<Formation> filtered = formationService.searchByTitle(query);

            formationList.clear();
            formationList.addAll(filtered);

            currentPage = 0;
            refreshList();
        });
    }

    private void goToEdit(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditFormation.fxml"));
            Parent root = loader.load();

            EditFormationController controller = loader.getController();
            controller.setFormationData(formation);

            Stage stage = new Stage();
            stage.setTitle("Edit Formation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadFormations(); // Reload all formations after edit
        } catch (IOException e) {
            showAlert("Error", "Could not load the edit form", Alert.AlertType.ERROR);
        }
    }

    public static void goToDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource("/Formation/client/ShowFormation.fxml"));
            Scene scene = new Scene(loader.load());

            ShowFormationController controller = loader.getController();
            controller.setFormation(formation);

            Stage stage = new Stage();
            stage.setTitle("Formation Details");
            stage.setScene(scene);

            // Set to full screen
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("Press ESC to exit full screen");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable default ESC exit

            // Optional: Add your own key combination to exit full screen
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.setFullScreen(false);
                }
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        List<Formation> filtered = formationService.searchByTitle(query);
        formationList.clear();
        formationList.addAll(filtered);
        currentPage = 0;
        refreshList();
    }

    private void setupPaginationListener() {
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            currentPage = newValue.intValue();
            updateFormationList(currentPage);
        });
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}