package bataille_navale;

public class Navire {
    private String nom;
    private int taille;
    private int touches; // Nombre de fois que le navire a été touché

    public Navire(String nom, int taille) {
        this.nom = nom;
        this.taille = taille;
        this.touches = 0;
    }

    public void estTouche() {
        this.touches++;
    }

    public boolean estCoule() {
        return this.touches >= this.taille;
    }

    public String getNom() {
        return nom;
    }

    public int getTaille() {
        return taille;
    }
}