package tn.esprit.services;

import tn.esprit.entities.Formation;
import tn.esprit.entities.Quiz;
import tn.esprit.utils.MyDataBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormationService implements IFormation {

    private Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void ajouter(Formation formation) {
        String req = "INSERT INTO formation(titre, description, difficulte, note, shown, video) VALUES (?,?,?,?,?,?)";

        try {
            // Assuming 'formation.getVideo()' holds the path to the selected video file
            String videoFileName = Paths.get(formation.getVideo()).getFileName().toString();
            Path videoDestination = Paths.get("src/main/resources/images_videos/", videoFileName);

            // Copy the video file to the 'images_videos' folder
            Files.copy(Paths.get(formation.getVideo()), videoDestination, StandardCopyOption.REPLACE_EXISTING);

            // Now save the relative path of the video in the database (store only the filename or relative path)
            String videoPath = "" + videoFileName;

            try (PreparedStatement pstmt = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, formation.getTitre());
                pstmt.setString(2, formation.getDescription());
                pstmt.setString(3, formation.getDifficulte());
                pstmt.setInt(4, formation.getNote());
                pstmt.setBoolean(5, formation.isShown());
                pstmt.setString(6, videoPath);  // Save the relative path to the database
                pstmt.executeUpdate();

                // Récupérer l'ID auto-généré
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        formation.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Formation formation) {
        String req = "UPDATE formation SET titre=?, description=?, difficulte=?, note=?, shown=?, video=? WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, formation.getTitre());
            pstmt.setString(2, formation.getDescription());
            pstmt.setString(3, formation.getDifficulte());
            pstmt.setInt(4, formation.getNote());
            pstmt.setBoolean(5, formation.isShown());
            pstmt.setString(6, formation.getVideo());
            pstmt.setInt(7, formation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int id) {
        String req = "DELETE FROM formation WHERE id=?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Formation> findAll() {
        List<Formation> formations = new ArrayList<>();
        String req = "SELECT * FROM formation";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                Formation f = new Formation(
                        res.getInt("id"),
                        res.getString("titre"),
                        res.getString("description"),
                        res.getString("difficulte"),
                        res.getInt("note"),
                        res.getBoolean("shown"),
                        res.getString("video")
                );
                formations.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formations;
    }

    @Override
    public Formation findById(int id) {
        String req = "SELECT * FROM formation WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return new Formation(
                        res.getInt("id"),
                        res.getString("titre"),
                        res.getString("description"),
                        res.getString("difficulte"),
                        res.getInt("note"),
                        res.getBoolean("shown"),
                        res.getString("video")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Formation> searchByTitle(String title) {
        List<Formation> formations = new ArrayList<>();
        String req = "SELECT * FROM formation WHERE titre LIKE ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, "%" + title + "%");
            ResultSet res = ps.executeQuery();

            while (res.next()) {
                Formation f = new Formation(
                        res.getInt("id"),
                        res.getString("titre"),
                        res.getString("description"),
                        res.getString("difficulte"),
                        res.getInt("note"),
                        res.getBoolean("shown"),
                        res.getString("video")
                );
                formations.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formations;
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
                quiz.setFormation_id(rs.getInt("formationId"));
                quiz.setImage(rs.getString("image"));
                quiz.setIncorrect1(rs.getString("incorrect1"));
                quiz.setIncorrect2(rs.getString("incorrect2"));
                quiz.setCorrect(rs.getString("correct"));
                quiz.setReponse(rs.getBoolean("reponse"));

                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching quizzes: " + e.getMessage());
        }

        return quizzes;
    }
}