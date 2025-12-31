package bataille_navale;

import java.util.ArrayList;
import java.util.List;

public class Jeu {
    // Les phases du jeu
    public enum Phase {
        PLACEMENT, JEU, FIN
    }

    public enum NiveauDifficulte {
        FACILE, MOYENNE, DIFFICILE
    }

    private Grille grilleJ1;
    private Grille grilleJ2;
    private int joueurCourant; // 1 ou 2
    private Phase phaseCourante;

    // Mode IA
    private boolean modeIA = false;
    private NiveauDifficulte niveauIA = NiveauDifficulte.FACILE;

    // Modèles de navires à placer (Nom + Taille)
    private List<Navire> naviresAPlacerJ1;
    private List<Navire> naviresAPlacerJ2;

    private boolean orientationHorizontale = true;

    // Référence vers la fenêtre graphique pour pouvoir la fermer
    private javax.swing.JFrame fenetreJeu;

    // Cibles potentielles pour l'IA (Stratégie "Touche-Touche")
    // Note: Stocke des tableaux int[2] -> {x, y}
    private List<int[]> ciblesIA = new ArrayList<>();

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
        if (phaseCourante != Phase.PLACEMENT) {
            if (phaseCourante == Phase.FIN)
                return "Partie terminée.";

            // Phase de JEU
            if (modeIA && joueurCourant == 2) {
                return "C'est au tour de l'IA...";
            } else {
                return "C'est la GUERRE ! Joueur " + joueurCourant + ", à vous de tirer !";
            }
        }

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
        // Si le joueur 1 a fini
        if (joueurCourant == 1 && naviresAPlacerJ1.isEmpty()) {
            if (modeIA) {
                // PLACEMENT AUTOMATIQUE IA
                grilleJ2.placerNaviresAleatoirement(naviresAPlacerJ2);
                naviresAPlacerJ2.clear(); // On vide pour marquer comme fini

                javax.swing.JOptionPane.showMessageDialog(null,
                        "L'IA a placé ses navires !\n\nLA BATAILLE COMMENCE !");

                phaseCourante = Phase.JEU;
                joueurCourant = 1; // Le J1 commence à tirer
            } else {
                // POP-UP DE TRANSITION (Fair-Play)
                javax.swing.JOptionPane.showMessageDialog(null,
                        "JOUEUR 1 A TERMINÉ !\n\nAu tour du JOUEUR 2.\n(Ne regardez pas l'écran du Joueur 1 !)");

                joueurCourant = 2;
            }
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

        // Mise à jour visuelle immédiate
        if (fenetreJeu != null)
            fenetreJeu.repaint();

        if (resultat) {
            System.out.println("  -> TOUCHÉ ! Le Joueur " + joueurCourant + " garde la main.");

            // IA HARD/MEDIUM : On ajoute les voisins à la liste des cibles
            if (modeIA && joueurCourant == 2
                    && (niveauIA == NiveauDifficulte.DIFFICILE || niveauIA == NiveauDifficulte.MOYENNE)) {
                ajouterCiblesVoisines(x, y, grilleCible);
            }

            // GESTION "COULÉ"
            Navire navireTouche = grilleCible.getNavireEn(x, y);
            if (navireTouche != null && navireTouche.estCoule()) {
                String nomAttaquant;
                if (joueurCourant == 1) {
                    nomAttaquant = "Joueur 1";
                } else {
                    // Joueur 2 ou IA
                    if (modeIA) {
                        nomAttaquant = "L'IA (" + niveauIA + ")";
                    } else {
                        nomAttaquant = "Joueur 2";
                    }
                }

                javax.swing.JOptionPane.showMessageDialog(null,
                        "BOUM ! " + nomAttaquant + " a COULÉ le " + navireTouche.getNom() + " !");

                // IA HARD : On marque tout autour comme joué
                if (modeIA && joueurCourant == 2 && niveauIA == NiveauDifficulte.DIFFICILE) {
                    marquerAutourCoule(navireTouche, grilleCible);
                }
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
            // SAUF SI C'EST LA FIN, qui est gérée au dessus.
            // On vérifie phaseCourante != FIN pour éviter que l'IA rejoue après avoir gagné
            if (phaseCourante == Phase.JEU && modeIA && joueurCourant == 2) {
                // L'IA rejoue si elle touche
                jouerTourIA();
            }

        } else {
            System.out.println("  -> PLOUF (À l'eau). C'est au tour de l'adversaire.");
            // Changement de joueur
            joueurCourant = (joueurCourant == 1) ? 2 : 1;

            // Mise à jour visuelle (important si thread)
            if (fenetreJeu != null)
                fenetreJeu.repaint();

            // Si c'est au tour de l'IA (Joueur 2) et que le mode IA est actif
            if (modeIA && joueurCourant == 2) {
                // IMPORTANT : On lance l'IA dans un Thread séparé pour ne pas figer l'interface
                // pendant le Thread.sleep()
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        jouerTourIA();
                    }
                }).start();
            }
        }
    }

    /**
     * Ajoute les voisins (Haut, Bas, Gauche, Droite) à la liste des cibles de l'IA
     * si valides.
     */
    private void ajouterCiblesVoisines(int cx, int cy, Grille grilleCible) {
        int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } }; // Haut, Bas, Gauche, Droite

        for (int[] d : directions) {
            int nx = cx + d[0];
            int ny = cy + d[1];

            // 1. Est dans la grille ?
            // On utilise une méthode de Grille si elle est publique, sinon on check
            // manuellement
            // Grille.estDansGrille est private, donc on check manuellement ici ou on
            // suppose Grille.TAILLE = 10
            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                // 2. N'a pas déjà été joué ?
                if (!grilleCible.estDejaJoue(nx, ny)) {
                    // 2b. Est-ce un coup utile ? (Diagonales)
                    // Si on est en mode difficile, on vérifie ça aussi avant d'ajouter
                    if (niveauIA == NiveauDifficulte.DIFFICILE && estCoupInutile(nx, ny, grilleCible)) {
                        continue; // On ne l'ajoute pas
                    }

                    // 3. N'est pas déjà dans la liste ciblesIA ?
                    boolean dejaPrevu = false;
                    for (int[] cible : ciblesIA) {
                        if (cible[0] == nx && cible[1] == ny) {
                            dejaPrevu = true;
                            break;
                        }
                    }
                    if (!dejaPrevu) {
                        ciblesIA.add(new int[] { nx, ny });
                        System.out.println("[IA Target] Ajout cible prioritaire : " + nx + "," + ny);
                    }
                }
            }
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
        ciblesIA.clear(); // Reset IA

        // 3. On redonne une flotte neuve à chacun
        naviresAPlacerJ1 = initFlotte();
        naviresAPlacerJ2 = initFlotte();

        System.out.println("[Moteur] Partie relancée ! Au Joueur 1 de placer ses navires.");
    }

    public void setModeIA(NiveauDifficulte niveau) {
        this.modeIA = true;
        this.niveauIA = niveau;
        System.out.println("[Moteur] Mode IA activé : " + niveau);
    }

    /**
     * Vérifie si un coup est inutile (diagonale d'un navire touché).
     * Règle : Les bateaux ne peuvent pas se toucher, même en diagonale.
     * Donc si une case (x,y) a un voisin diagonal qui est TOUCHE, (x,y) est
     * forcément de l'eau.
     */
    private boolean estCoupInutile(int x, int y, Grille grille) {
        int[][] diagonales = { { -1, -1 }, { 1, -1 }, { -1, 1 }, { 1, 1 } }; // NO, NE, SO, SE

        for (int[] d : diagonales) {
            int nx = x + d[0];
            int ny = y + d[1];

            // On regarde si la diagonale est dans la grille
            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                if (grille.getEtat(nx, ny) == Grille.TOUCHE) {
                    return true; // Impossible qu'il y ait un bateau ici
                }
            }
        }
        return false;
    }

    /**
     * Marque les cases autour d'un navire coulé comme "Loupé" (ou visité)
     * pour éviter que l'IA ne tape dedans.
     */
    private void marquerAutourCoule(Navire navire, Grille grille) {
        // On doit scanner la grille pour trouver les morceaux de ce navire
        // car le navire ne connait pas ses coordonnées.
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (grille.getNavireEn(x, y) == navire) {
                    // Pour chaque case du navire, on marque les 8 voisins
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int nx = x + dx;
                            int ny = y + dy;

                            if (nx >= 0 && nx < 10 && ny >= 0 && ny < 10) {
                                // Si c'est de l'eau, on la révèle (marque comme Loupé)
                                if (grille.getEtat(nx, ny) == Grille.EAU) {
                                    grille.recevoirTir(nx, ny);
                                    System.out.println("[IA Smart] Zone autour du coulé marquée : " + nx + "," + ny);

                                    // On nettoie aussi la liste de cibles si jamais
                                    // (bien que estDejaJoue filtre déjà)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Joue le tour de l'IA selon la difficulté.
     */
    private void jouerTourIA() {
        if (phaseCourante != Phase.JEU || joueurCourant != 2)
            return;

        System.out.println("\n--- Tour de l'IA ---");
        System.out.println("Votre Grille :");
        grilleJ1.afficherConsole();

        // Visualisation : Pause de 1 seconde
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[IA] C'est mon tour ! Niveau : " + niveauIA);

        Grille grilleAdverse = grilleJ1;
        int tirX = -1;
        int tirY = -1;

        // STRATÉGIE
        if ((niveauIA == NiveauDifficulte.DIFFICILE || niveauIA == NiveauDifficulte.MOYENNE) && !ciblesIA.isEmpty()) {
            // MODE CHASSE (HUNT)
            int index = ciblesIA.size() - 1;
            int[] cible = ciblesIA.remove(index);
            tirX = cible[0];
            tirY = cible[1];

            if (grilleAdverse.estDejaJoue(tirX, tirY)) {
                jouerTourIA();
                return;
            }
        } else {
            // MODE RANDOM (recherche aléatoire)
            boolean valide = false;
            int essais = 0;
            while (!valide && essais < 200) {
                tirX = (int) (Math.random() * 10);
                tirY = (int) (Math.random() * 10);
                essais++;

                if (!grilleAdverse.estDejaJoue(tirX, tirY)) {
                    // Pour l'IA Difficile, on évite les zones diagonales inutiles
                    if (niveauIA == NiveauDifficulte.DIFFICILE && estCoupInutile(tirX, tirY, grilleAdverse)) {
                        continue; // On rejette ce coup aléatoire car impossible
                    }
                    valide = true;
                }
            }
            // Fallback si on ne trouve pas (très rare)
            if (!valide) {
                for (int y = 0; y < 10; y++) {
                    for (int x = 0; x < 10; x++) {
                        if (!grilleAdverse.estDejaJoue(x, y)) {
                            tirX = x;
                            tirY = y;
                            valide = true;
                            break;
                        }
                    }
                    if (valide)
                        break;
                }
            }
        }

        jouerTour(tirX, tirY);
    }
}