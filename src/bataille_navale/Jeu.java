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

    // Mode IA
    private boolean modeIA = false;
    private NiveauDifficulte niveauIA = NiveauDifficulte.FACILE;
    private IA ia;
    // Modèles de navires à placer (Nom + Taille)
    private List<Navire> naviresAPlacerJ1;
    private List<Navire> naviresAPlacerJ2;

    private boolean orientationHorizontale = true;

    // Référence vers la fenêtre graphique pour pouvoir la fermer
    private javax.swing.JFrame fenetreJeu;

    public Jeu() {
        this.grilleJ1 = new Grille();
        this.grilleJ2 = new Grille();
        this.joueurCourant = 1; // Initialisation du joueur 1
        this.phaseCourante = Phase.PLACEMENT;

        // Initialisation des flottes à placer avec NOMS et TAILLES officielles
        this.naviresAPlacerJ1 = initFlotte();
        this.naviresAPlacerJ2 = initFlotte();
    }

    public void setFenetreJeu(javax.swing.JFrame f) {
        this.fenetreJeu = f;
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
     * Helper pour l'interface : Renvoie le prochain navire à placer sans le
     * retirer.
     */
    public Navire getNavireCourant() {
        List<Navire> liste = (joueurCourant == 1) ? naviresAPlacerJ1 : naviresAPlacerJ2;
        if (liste.isEmpty())
            return null;
        return liste.get(0);
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
            // CORRECTION : On passe l'objet Navire entier
            grilleActuelle.placerNavire(navireModel, x, y, orientationHorizontale);
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

            // GESTION "COULÉ"
            Navire navireTouche = grilleCible.getNavireEn(x, y);
            if (navireTouche != null && navireTouche.estCoule()) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "BOUM ! Vous avez COULÉ le " + navireTouche.getNom() + " !");
            } else {
                // Juste touché
                // javax.swing.JOptionPane.showMessageDialog(null, "TOUCHÉ !"); // Optionnel,
                // peut être trop intrusif
            }

            // VERIFICATION VICTOIRE
            if (grilleCible.estDefaite()) {
                System.out.println("#########################################");
                System.out.println("VICTOIRE DU JOUEUR " + joueurCourant + " !");
                System.out.println("#########################################");

                // FIN DE PARTIE PROPRE
                gererFinDePartie(joueurCourant);
            }

            // Sinon, le joueur rejoue (on ne change pas joueurCourant)

        } else {
            System.out.println("  -> PLOUF (À l'eau). C'est au tour de l'adversaire.");
            // Changement de joueur
            joueurCourant = (joueurCourant == 1) ? 2 : 1;
        }
    }

    /**
     * Affiche le dialogue de fin et propose de rejouer ou quitter.
     */
    private void gererFinDePartie(int vainqueur) {
        phaseCourante = Phase.FIN;

        Object[] options = { "Rejouer", "Quitter" };
        int choix = javax.swing.JOptionPane.showOptionDialog(null,
                "FÉLICITATIONS !\n\nLe JOUEUR " + vainqueur + " a gagné la partie !",
                "Victoire",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choix == javax.swing.JOptionPane.YES_OPTION) {
            reinitialiserPartie();
        } else {
            // RETOUR AU MENU
            if (this.fenetreJeu != null) {
                this.fenetreJeu.dispose(); // Ferme la fenêtre de jeu
            }
            // On relance le menu
            MenuGraphique menu = new MenuGraphique();
            menu.setVisible(true);
        }
    }

    /**
     * Remet le jeu à zéro pour une nouvelle partie.
     */
    private void reinitialiserPartie() {
        System.out.println("[Moteur] Réinitialisation de la partie...");

        // 1. On vide les grilles
        grilleJ1.vider();
        grilleJ2.vider();

        // 2. On remet les paramètres par défaut
        joueurCourant = 1;
        phaseCourante = Phase.PLACEMENT;
        orientationHorizontale = true;

        // 3. On redonne une flotte neuve à chacun
        naviresAPlacerJ1 = initFlotte();
        naviresAPlacerJ2 = initFlotte();

        System.out.println("[Moteur] Partie relancée ! Au Joueur 1 de placer ses navires.");
    }
}