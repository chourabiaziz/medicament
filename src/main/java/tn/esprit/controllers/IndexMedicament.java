package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tn.esprit.entities.Medicament;
import tn.esprit.services.MedicamentService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IndexMedicament implements Initializable {



    private final MedicamentService service = new MedicamentService();
    private final ObservableList<Medicament> medicamentList = FXCollections.observableArrayList();

    @FXML
    private ListView<Medicament> listview;



    @FXML
    private Button add;






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
         listview.setItems(medicamentList);
        add.setOnAction(this::gotoadd);
         refreshList();
    }

    public void refreshList() {
        medicamentList.clear();
        medicamentList.addAll(service.findall());
    }




    void gotoadd(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addMedicament.fxml"));
        Parent root ;
        try {
            Node source = (Node) event.getSource();
            root = loader.load();
            System.out.println("FXML file loaded successfully.");
            //change controller name
            AddMedicament controller = loader.getController();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle("List des médicament");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}