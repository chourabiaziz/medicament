package tn.esprit.services;

import tn.esprit.entities.Medicament;

import java.sql.Array;
import java.util.List;

public interface Imedicament {

    public void Ajout(Medicament medicament) ;  //ajout
    public List<Medicament> findall() ;  //find all


}
