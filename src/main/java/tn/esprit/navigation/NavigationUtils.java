package tn.esprit.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.controllers.Formation.ShowFormationController;
import tn.esprit.entities.Formation;

import java.io.IOException;

public class NavigationUtils {

    public static void navigateToShowPage(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource("/Formation/ShowFormation.fxml"));
            Scene scene = new Scene(loader.load());

            ShowFormationController controller = loader.getController();
            controller.setFormation(formation); // Pass the formation to the controller

            Stage stage = (Stage) scene.getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void navigateToAddPage() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource("/Formation/AddFormation.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) scene.getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
