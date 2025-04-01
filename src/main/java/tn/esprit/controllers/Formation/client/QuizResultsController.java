package tn.esprit.controllers.Formation.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class QuizResultsController implements Initializable {

    @FXML private Label scoreLabel;
    @FXML private Label totalLabel;
    @FXML private Label gradeLabel;
    @FXML private Label messageLabel;

    private int correctAnswers;
    private int totalQuestions;
    private double score;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization if needed
    }

    public void setResults(int correctAnswers, int totalQuestions, double score) {
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.score = score;

        updateUI();
    }

    private void updateUI() {
        scoreLabel.setText(String.valueOf(correctAnswers));
        totalLabel.setText(String.valueOf(totalQuestions));
        gradeLabel.setText(String.format("%.1f/20", score));

        double percentage = (double) correctAnswers / totalQuestions * 100;
        if (percentage >= 80) {
            messageLabel.setText("Excellent work!");
        } else if (percentage >= 60) {
            messageLabel.setText("Good job!");
        } else if (percentage >= 40) {
            messageLabel.setText("Not bad, but you can do better!");
        } else {
            messageLabel.setText("Keep practicing!");
        }
    }
}