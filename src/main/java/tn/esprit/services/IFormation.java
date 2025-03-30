package tn.esprit.services;

import tn.esprit.entities.Formation;
import java.util.List;

public interface IFormation {
    void ajouter(Formation formation);
    void modifier(Formation formation);
    void supprimer(int id);
    List<Formation> findAll();
    Formation findById(int id);
    List<Formation> searchByTitle(String title);

}