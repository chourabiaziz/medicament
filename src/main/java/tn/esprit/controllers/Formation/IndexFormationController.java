package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Formation;
import tn.esprit.navigation.NavigationUtils;
import tn.esprit.services.FormationService;

import java.io.IOException;
import java.util.List;

public class IndexFormationController {

    private final FormationService formationService = new FormationService();
    private final ObservableList<Formation> formationList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 5;

    @FXML private VBox formationListContainer;
    @FXML private Pagination pagination;
    @FXML private TextField searchField;
    @FXML private Button addBtn;

    @FXML
    public void initialize() {
        setupSearchListener();
        setupPaginationListener();
        refreshList();
    }
    @FXML
    private void refreshList() {
        List<Formation> allFormations = formationList;  // Use the filtered formation list
        int totalPages = (int) Math.ceil((double) allFormations.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(totalPages);  // Update pagination count based on filtered results
        updateFormationList(currentPage);  // Update the formation list for the first page
    }



    @FXML
    public void goToAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddFormation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Formation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshList(); // Refresh after the add window closes
        } catch (IOException e) {
            showAlert("Error", "Could not load the add form", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void updateFormationList(int page) {
        // Get the filtered list instead of the full list
        List<Formation> allFormations = formationList;  // Use the filtered list

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allFormations.size());
        List<Formation> pageFormations = allFormations.subList(start, end);

        formationListContainer.getChildren().clear();

        for (Formation formation : pageFormations) {
            VBox card = createFormationCard(formation);
            formationListContainer.getChildren().add(card);
        }
    }

    private VBox createFormationCard(Formation formation) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #ddd;");

        Label titleLabel = new Label(formation.getTitre());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(formation.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Label difficultyLabel = new Label("Difficulty: " + formation.getDifficulte());
        difficultyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");

        // View Details Button
        Button detailButton = new Button("View Details");
        detailButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        detailButton.setOnAction(event -> goToDetails(formation));

        // Edit Button
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
        editButton.setOnAction(event -> goToEdit(formation));

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
        deleteButton.setOnAction(event -> handleDelete(formation));

        // Add buttons in a horizontal row
        HBox buttonBox = new HBox(10, editButton, deleteButton);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 5;");

        // Add elements to the card
        card.getChildren().addAll(titleLabel, descriptionLabel, difficultyLabel, detailButton, buttonBox);
        return card;
    }
    private void handleDelete(Formation formation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Are you sure you want to delete this formation?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                formationService.supprimer(formation.getId()); // Call the service to delete the formation
                refreshList(); // Refresh the list after deletion
            }
        });
    }
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue.trim();
            // Perform the search and filter the list
            List<Formation> filtered = formationService.searchByTitle(query);

            // Update the list of formations
            formationList.clear();
            formationList.addAll(filtered);

            // Reset pagination and update the formation list
            currentPage = 0; // Reset to the first page after each search
            updateFormationList(currentPage);
        });
    }


    private void goToEdit(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditFormation.fxml"));
            Parent root = loader.load();

            EditFormationController controller = loader.getController();
            controller.setFormationData(formation); // Pass the formation data to the edit form

            Stage stage = new Stage();
            stage.setTitle("Edit Formation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshList(); // Refresh the list after editing
        } catch (IOException e) {
            showAlert("Error", "Could not load the edit form", Alert.AlertType.ERROR);
        }
    }

    public static void goToDetails(Formation formation) {
            try {
                FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource("/ShowFormation.fxml"));
                Scene scene = new Scene(loader.load());

                ShowFormationController controller = loader.getController();
                controller.setFormation(formation); // Pass the formation to the controller
                System.out.println(formation);
                // Create a new Stage and show it
                Stage stage = new Stage();
                stage.setTitle("Formation Details");
                stage.setScene(scene);
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

        // Reset pagination and update the list of formations
        currentPage = 0;
        refreshList();  // Refresh list and pagination based on filtered results
    }


    private void setupPaginationListener() {
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            currentPage = newValue.intValue();
            updateFormationList(currentPage);
        });
    }

    @FXML
    private void goToAdd() {
        NavigationUtils.navigateToAddPage();
    }
}
