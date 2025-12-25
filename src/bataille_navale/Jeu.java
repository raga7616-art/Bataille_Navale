package bataille_navale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Jeu {
    // Les phases du jeu
    public enum Phase {
        PLACEMENT, JEU, FIN
    }

    private Grille grilleJ1;
    private Grille grilleJ2;
    private int joueurCourant; // 1 ou 2
    private Phase phaseCourante;

    // Pour le placement : liste des tailles de navires à placer
    // Standard : 1x4, 2x3, 3x2, 4x1
    private List<Integer> naviresAPlacerJ1;
    private List<Integer> naviresAPlacerJ2;

    // Orientation actuelle pour le placement (true = horizontal, false = vertical)
    private boolean orientationHorizontale = true;

    public Jeu() {
        this.grilleJ1 = new Grille();
        this.grilleJ2 = new Grille();
        this.joueurCourant = 1; // Le joueur 1 commence par placer
        this.phaseCourante = Phase.PLACEMENT;

        // Initialisation des flottes à placer
        this.naviresAPlacerJ1 = new ArrayList<>(Arrays.asList(4, 3, 3, 2, 2, 2, 1, 1, 1, 1));
        this.naviresAPlacerJ2 = new ArrayList<>(Arrays.asList(4, 3, 3, 2, 2, 2, 1, 1, 1, 1));
    }

    public Grille getGrilleJ1() {
        return grilleJ1;
    }

    public Grille getGrilleJ2() {
        return grilleJ2;
    }

    public int getJoueurCourant() {
        return joueurCourant;
    }

    public Phase getPhase() {
        return phaseCourante;
    }

    public boolean isOrientationHorizontale() {
        return orientationHorizontale;
    }

    public void basculerOrientation() {
        this.orientationHorizontale = !this.orientationHorizontale;
        System.out.println("[Moteur] Orientation modifiée : " + (orientationHorizontale ? "Horizontale" : "Verticale"));
    }

    /**
     * Tente de placer le prochain navire du joueur courant.
     */
    public boolean placerNavireJoueur(int x, int y) {
        if (phaseCourante != Phase.PLACEMENT)
            return false;

        Grille grilleActuelle = (joueurCourant == 1) ? grilleJ1 : grilleJ2;
        List<Integer> naviresRestants = (joueurCourant == 1) ? naviresAPlacerJ1 : naviresAPlacerJ2;

        if (naviresRestants.isEmpty())
            return false; // Plus rien à placer

        int tailleNavire = naviresRestants.get(0); // On prend le prochain navire de la liste

        // On vérifie si on peut le placer
        if (grilleActuelle.peutPlacer(tailleNavire, x, y, orientationHorizontale)) {
            grilleActuelle.placerNavire(tailleNavire, x, y, orientationHorizontale);
            naviresRestants.remove(0); // On retire le navire de la liste
            System.out.println("[Moteur] Navire taille " + tailleNavire + " placé pour Joueur " + joueurCourant);

            verifierFinPlacement();
            return true;
        } else {
            System.out.println("[Moteur] Placement impossible ici !");
            return false;
        }
    }

    private void verifierFinPlacement() {
        // Si le joueur 1 a fini, c'est au joueur 2
        if (joueurCourant == 1 && naviresAPlacerJ1.isEmpty()) {
            System.out.println("[Moteur] Joueur 1 a fini de placer. Au tour du Joueur 2.");
            joueurCourant = 2;
        }
        // Si le joueur 2 a fini, on lance le jeu
        else if (joueurCourant == 2 && naviresAPlacerJ2.isEmpty()) {
            System.out.println("[Moteur] Tous les navires sont placés. LA BATAILLE COMMENCE !");
            phaseCourante = Phase.JEU;
            joueurCourant = 1; // Le J1 commence à tirer
        }
    }

    /**
     * Gère un tour de jeu complet (TIR) avec vérification de victoire.
     */
    public void jouerTour(int x, int y) {
        if (phaseCourante != Phase.JEU)
            return;

        // Définir qui est la cible (l'adversaire du joueur courant)
        Grille grilleCible = (joueurCourant == 1) ? grilleJ2 : grilleJ1;

        System.out.println("[Moteur] Joueur " + joueurCourant + " tire en (" + x + ", " + y + ")");

        // On effectue le tir
        boolean resultat = grilleCible.recevoirTir(x, y);

        if (resultat) {
            System.out.println("  -> TOUCHÉ ! Le Joueur " + joueurCourant + " garde la main.");

            // VERIFICATION VICTOIRE
            if (grilleCible.estDefaite()) {
                System.out.println("#########################################");
                System.out.println("VICTOIRE DU JOUEUR " + joueurCourant + " !");
                System.out.println("#########################################");

                // POP-UP VISUEL POUR LE JOUEUR
                javax.swing.JOptionPane.showMessageDialog(null, "VICTOIRE DU JOUEUR " + joueurCourant + " !");

                phaseCourante = Phase.FIN;
            }
            // Sinon, le joueur rejoue (on ne change pas joueurCourant)

        } else {
            System.out.println("  -> PLOUF (À l'eau). C'est au tour de l'adversaire.");
            // Changement de joueur
            joueurCourant = (joueurCourant == 1) ? 2 : 1;
        }
    }
}