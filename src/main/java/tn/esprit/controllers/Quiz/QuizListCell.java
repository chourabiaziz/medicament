package tn.esprit.controllers.Quiz;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import tn.esprit.entities.Quiz;

import java.io.InputStream;

public class QuizListCell extends ListCell<Quiz> {
    @Override
    protected void updateItem(Quiz quiz, boolean empty) {
        super.updateItem(quiz, empty);
        if (empty || quiz == null) {
            setText(null);
            setGraphic(null);
        } else {
            HBox card = new HBox(10);

            // Create image view if image exists
            if (quiz.getImage() != null) {
                try (InputStream is = getClass().getResourceAsStream(quiz.getImage())) {
                    if (is != null) {
                        Image image = new Image(is);
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(100);
                        imageView.setPreserveRatio(true);
                        card.getChildren().add(imageView);
                    }
                } catch (Exception e) {
                    card.getChildren().add(new Text("[Image not available]"));
                }
            }

            Text text = new Text(quiz.getIncorrect1() + " / " + quiz.getIncorrect2());
            card.getChildren().add(text);
            setGraphic(card);
        }
    }
}
