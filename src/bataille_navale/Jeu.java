package bataille_navale;

public class Jeu {
    private Grille grilleJoueur;
    private Grille grilleOrdi;
    private Affichage affichage;

    public Jeu() {
        this.grilleJoueur = new Grille();
        this.grilleOrdi = new Grille();
        this.affichage = new Affichage();
    }

    public void demarrerPartie() {
        System.out.println("[Jeu] Initialisation de la partie...");

        // Phase 1 : Placement des navires
        grilleJoueur.placerNaviresAuto();
        grilleOrdi.placerNaviresAuto();

        // Phase 2 : Boucle de jeu
        boolean partieEnCours = true;
        while (partieEnCours) {
            affichage.afficherGrille(grilleOrdi);
            affichage.demanderCoordonnees();
            System.out.println("[Jeu] Fin du tour de test.");
            partieEnCours = false;
        }
        System.out.println("--- Fin du programme ---");
    }
}