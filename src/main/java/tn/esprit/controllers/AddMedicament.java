package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Medicament;
import tn.esprit.services.MedicamentService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class AddMedicament implements Initializable {

    @FXML
    private TextField description;

    @FXML
    private DatePicker expireat;

    @FXML
    private TextField nom;

    @FXML
    private TextField prix;

    @FXML
    private TextField quantite;

    @FXML
    private Button submit;

    @FXML
    private TextField type;

    @FXML
    private Button retour;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        submit.setOnAction(this::addMedicament);
        retour.setOnAction(this::gotolist);


    }


    public void addMedicament(ActionEvent event) {
        Medicament medicament = new Medicament();
        MedicamentService service = new MedicamentService();
        medicament.setNom(nom.getText());
        medicament.setDescription(description.getText());
        medicament.setPrix(Double.parseDouble(prix.getText()));
        medicament.setQuantite(Integer.parseInt(quantite.getText()));
        medicament.setType(type.getText());
        medicament.setExpireAt(Date.valueOf(expireat.getValue()));
        medicament.setImage("default.png");
        medicament.setFournisseurId(1);

        service.Ajout(medicament);
        System.out.println("Médicament ajouté avec succès !");
        gotolist(event);

    }


    void gotolist(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/IndexMedicament.fxml"));
        Parent root ;
        try {
            Node source = (Node) event.getSource();
            root = loader.load();
            System.out.println("FXML file loaded successfully.");
            IndexMedicament controller = loader.getController();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle("Modifier Contrat");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
