package tn.esprit.entities;

public class Formation {
    private int id;
    private String titre;
    private String description;
    private String difficulte;
    private int note;
    private boolean shown;
    private String video;

    public Formation() {}

    public Formation(int id, String titre, String description, String difficulte,
                     int note, boolean shown, String video) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.difficulte = difficulte;
        this.note = note;
        this.shown = shown;
        this.video = video;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", difficulte='" + difficulte + '\'' +
                ", note=" + note +
                ", shown=" + shown +
                ", video='" + video + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(String difficulte) {
        this.difficulte = difficulte;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}