package tn.esprit.services;

import tn.esprit.entities.Quiz;
import tn.esprit.utils.MyDataBase;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService {
    private Connection cnx = MyDataBase.getInstance().getCnx();

    public void ajouter(Quiz quiz) {
        String req = "INSERT INTO quiz(formation_id, image, incorrect1, incorrect2, correct, reponse) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement pstmt = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, quiz.getFormation_id());
            pstmt.setString(2, quiz.getImage()); // Store the original file name as is
            pstmt.setString(3, quiz.getIncorrect1());
            pstmt.setString(4, quiz.getIncorrect2());
            pstmt.setString(5, quiz.getCorrect());
            pstmt.setBoolean(6, quiz.getReponse());
            pstmt.executeUpdate();

            // Set the generated id for the quiz
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quiz.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add quiz: " + e.getMessage());
        }
    }

    public void modifier(Quiz quiz) {
        String req = "UPDATE quiz SET image=?, incorrect1=?, incorrect2=?, correct=?, reponse=? WHERE id=?";
        try {
            PreparedStatement pstmt = cnx.prepareStatement(req);
            pstmt.setString(1, quiz.getImage()); // Keep the original image name
            pstmt.setString(2, quiz.getIncorrect1());
            pstmt.setString(3, quiz.getIncorrect2());
            pstmt.setString(4, quiz.getCorrect());
            pstmt.setBoolean(5, quiz.getReponse());
            pstmt.setInt(6, quiz.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM quiz WHERE id=?";
        try {
            PreparedStatement pstmt = cnx.prepareStatement(req);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Quiz> getAll() {
        List<Quiz> quizzes = new ArrayList<>();
        String req = "SELECT * FROM quiz";
        try {
            Statement stmt = cnx.createStatement();
            ResultSet rs = stmt.executeQuery(req);
            while (rs.next()) {
                Quiz q = new Quiz(
                        rs.getInt("id"),
                        rs.getInt("formation_id"),
                        rs.getString("image"),
                        rs.getString("incorrect1"),
                        rs.getString("incorrect2"),
                        rs.getString("correct"),
                        rs.getBoolean("reponse")
                );
                quizzes.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public List<Quiz> getQuizzesFromFormationId(int formationId) {
        List<Quiz> quizzes = new ArrayList<>();
        String query = "SELECT * FROM quiz WHERE formation_id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, formationId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setFormation_id(rs.getInt("formation_id"));
                quiz.setImage(rs.getString("image"));  // Store the image name as is
                quiz.setIncorrect1(rs.getString("incorrect1"));
                quiz.setIncorrect2(rs.getString("incorrect2"));
                quiz.setCorrect(rs.getString("correct"));
                quiz.setReponse(rs.getBoolean("reponse"));

                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch quizzes: " + e.getMessage());
        }
        return quizzes;
    }
}
