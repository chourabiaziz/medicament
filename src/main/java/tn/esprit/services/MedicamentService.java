package tn.esprit.services;

import tn.esprit.entities.Medicament;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentService  implements Imedicament {

    Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void Ajout(Medicament medicament) {
        String req = "INSERT INTO `medicament`(`id`, `fournisseur_id`, `nom_medicament`, `description`, `quantite`, `prix`, `type`, `expireat`, `image`, `isshown`) VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
        pstmt.setInt(1, medicament.getId());
        pstmt.setInt(2, medicament.getFournisseurId());
        pstmt.setString(3, medicament.getNom());
        pstmt.setString(4, medicament.getDescription());
        pstmt.setInt(5, medicament.getQuantite());
        pstmt.setDouble(6, medicament.getPrix());
        pstmt.setString(7, medicament.getType());
        pstmt.setDate(8, new java.sql.Date(medicament.getExpireAt().getTime())); // Convert java.util.Date to java.sql.Date
        pstmt.setString(9, medicament.getImage());
        pstmt.setBoolean(10, medicament.isShown());

        pstmt.executeUpdate(); // Execute the INSERT query
    } catch (SQLException e) {
        e.printStackTrace();
    }
        }


    @Override
    public List<Medicament> findall() {
        List<Medicament> Medicaments = new ArrayList<>(); // declaration d'une liste des medicamments

        String req = "SELECT * FROM medicament";  // recupération des données depuis DB

        try (PreparedStatement ps = cnx.prepareStatement(req)) { // preparation du requéte
            ResultSet res = ps.executeQuery(); // query database results
            while (res.next()) {
                int id = res.getInt("id");
                int fournisseurId = res.getInt("fournisseur_id");
                String nom = res.getString("nom_medicament");
                String description = res.getString("description");
                int quantite = res.getInt("quantite");
                double prix = res.getDouble("prix");
                String type = res.getString("type");
                Date expireAt = res.getDate("expireat");
                String image = res.getString("image");
                boolean isShown = res.getBoolean("isshown");
                Medicament medicament = new Medicament(id, fournisseurId, nom, description, quantite, prix, type, expireAt, image, isShown);
                Medicaments.add(medicament);


            }
        } catch ( SQLException e) {
            e.printStackTrace();
        }
        return Medicaments;
    }
}
