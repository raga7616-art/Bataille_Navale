package bataille_navale;

import java.util.ArrayList;
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

    // Modèles de navires à placer (Nom + Taille)
    private List<Navire> naviresAPlacerJ1;
    private List<Navire> naviresAPlacerJ2;

    // Orientation actuelle pour le placement (true = horizontal, false = vertical)
    private boolean orientationHorizontale = true;

    public Jeu() {
        this.grilleJ1 = new Grille();
        this.grilleJ2 = new Grille();
        this.joueurCourant = 1; // Initialisation du joueur 1
        this.phaseCourante = Phase.PLACEMENT;

        // Initialisation des flottes à placer avec NOMS et TAILLES officielles
        this.naviresAPlacerJ1 = initFlotte();
        this.naviresAPlacerJ2 = initFlotte();
    }

    private List<Navire> initFlotte() {
        List<Navire> flotte = new ArrayList<>();
        // 1x4 Cuirassé
        flotte.add(new Navire("Cuirassé", 4));
        // 2x3 Croiseurs
        flotte.add(new Navire("Croiseur", 3));
        flotte.add(new Navire("Croiseur", 3));
        // 3x2 Destroyers
        flotte.add(new Navire("Destroyer", 2));
        flotte.add(new Navire("Destroyer", 2));
        flotte.add(new Navire("Destroyer", 2));
        // 4x1 Torpilleurs
        flotte.add(new Navire("Torpilleur", 1));
        flotte.add(new Navire("Torpilleur", 1));
        flotte.add(new Navire("Torpilleur", 1));
        flotte.add(new Navire("Torpilleur", 1));
        return flotte;
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

    /**
     * Renvoie le message d'instruction pour le joueur courant
     */
    public String getInstructionPlacement() {
        if (phaseCourante != Phase.PLACEMENT)
            return "Bataille en cours !";

        List<Navire> liste = (joueurCourant == 1) ? naviresAPlacerJ1 : naviresAPlacerJ2;
        if (liste.isEmpty())
            return "Placement terminé !";

        Navire prochain = liste.get(0);
        String orientation = orientationHorizontale ? "HORIZONTALE" : "VERTICALE";

        // Message clair pour l'utilisateur avec indication du Clic Droit
        return "Placez le " + prochain.getNom() + " (" + prochain.getTaille() + " cases) - Orientation : " + orientation
                + " (Clic Droit pour changer)";
    }

    public void basculerOrientation() {
        this.orientationHorizontale = !this.orientationHorizontale;
    }

    /**
     * Tente de placer le prochain navire du joueur courant.
     */
    public boolean placerNavireJoueur(int x, int y) {
        if (phaseCourante != Phase.PLACEMENT)
            return false;

        Grille grilleActuelle = (joueurCourant == 1) ? grilleJ1 : grilleJ2;
        List<Navire> naviresRestants = (joueurCourant == 1) ? naviresAPlacerJ1 : naviresAPlacerJ2;

        if (naviresRestants.isEmpty())
            return false; // Plus rien à placer

        Navire navireModel = naviresRestants.get(0);
        int taille = navireModel.getTaille();

        // On vérifie si on peut le placer
        if (grilleActuelle.peutPlacer(taille, x, y, orientationHorizontale)) {
            grilleActuelle.placerNavire(taille, x, y, orientationHorizontale);
            naviresRestants.remove(0); // On retire le navire de la liste
            System.out.println("[Moteur] " + navireModel.getNom() + " placé pour Joueur " + joueurCourant);

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
            // POP-UP DE TRANSITION (Fair-Play)
            javax.swing.JOptionPane.showMessageDialog(null,
                    "JOUEUR 1 A TERMINÉ !\n\nAu tour du JOUEUR 2.\n(Ne regardez pas l'écran du Joueur 1 !)");

            joueurCourant = 2;
        }
        // Si le joueur 2 a fini, on lance le jeu
        else if (joueurCourant == 2 && naviresAPlacerJ2.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "TOUS LES NAVIRES SONT PLACÉS !\n\nLA BATAILLE COMMENCE !");

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