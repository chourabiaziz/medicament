package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

       //pouurr clientt

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formation/client/IndexFormation.fxml"));

        //pourrr adminnnn

    //    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Formation/IndexFormation.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root );

            primaryStage.setTitle("Sigh");
            primaryStage.setScene(scene);
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();

            // Set stage width and height to screen dimensions
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());



            primaryStage.show();



        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }
}
