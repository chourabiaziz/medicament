package tn.esprit.entities;


public class Quiz {
    private int id;
    private int formation_id;  // Changed to match database column
    private String image;
    private String incorrect1;

    public Quiz(int id, int formationId, String image, String incorrect1, String incorrect2, String correct, boolean reponse) {
    }
    public Quiz(int id, int formationId, String image, String incorrect1, String incorrect2) {}

    public Quiz(int id, int formation_id, String image, String incorrect1, String incorrect2, String correct, Boolean reponse) {
        this.id = id;
        this.formation_id = formation_id;
        this.image = image;
        this.incorrect1 = incorrect1;
        this.incorrect2 = incorrect2;
        this.correct = correct;
        this.reponse = reponse;
    }

    public Quiz() {
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", formation_id=" + formation_id +
                ", image='" + image + '\'' +
                ", incorrect1='" + incorrect1 + '\'' +
                ", incorrect2='" + incorrect2 + '\'' +
                ", correct='" + correct + '\'' +
                ", reponse=" + reponse +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormation_id() {
        return formation_id;
    }

    public void setFormation_id(int formation_id) {
        this.formation_id = formation_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIncorrect1() {
        return incorrect1;
    }

    public void setIncorrect1(String incorrect1) {
        this.incorrect1 = incorrect1;
    }

    public String getIncorrect2() {
        return incorrect2;
    }

    public void setIncorrect2(String incorrect2) {
        this.incorrect2 = incorrect2;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public Boolean getReponse() {
        return reponse;
    }

    public void setReponse(Boolean reponse) {
        this.reponse = reponse;
    }

    private String incorrect2;
    private String correct;
    private Boolean reponse;


}