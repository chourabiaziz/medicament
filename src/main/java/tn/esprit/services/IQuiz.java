package tn.esprit.services;

import tn.esprit.entities.Quiz;
import java.util.List;

public interface IQuiz {
    void ajouter(Quiz quiz);
    void modifier(Quiz quiz);
    void supprimer(int id);
    List<Quiz> findAll();
    Quiz findById(int id);
}