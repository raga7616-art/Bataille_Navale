package bataille_navale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * GrillePanel : Composant graphique affichant la grille.
 * Gère les interactions souris en fonction de la phase de jeu.
 */
public class GrillePanel extends JPanel {
    private Grille grille;
    private Jeu jeu; // Référence vers le moteur de jeu
    private int idJoueurProprietaire; // 1 pour J1, 2 pour J2
    private final int TAILLE_CASE = 30; // Taille en pixels d'une case

    public GrillePanel(Grille grille, Jeu jeu, int idJoueurProprietaire) {
        this.grille = grille;
        this.jeu = jeu;
        this.idJoueurProprietaire = idJoueurProprietaire;

        this.setPreferredSize(new Dimension(Grille.TAILLE * TAILLE_CASE + 1, Grille.TAILLE * TAILLE_CASE + 1));

        // Ajout de l'écouteur de souris complet
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // On passe : X, Y, et le Bouton (1=Gauche, 3=Droit)
                gererClic(e.getX(), e.getY(), e.getButton());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < Grille.TAILLE; y++) {
            for (int x = 0; x < Grille.TAILLE; x++) {
                int etat = grille.getEtat(x, y);

                switch (etat) {
                    case Grille.EAU:
                        g.setColor(new Color(173, 216, 230)); // Bleu clair
                        break;
                    case Grille.NAVIRE:
                        // LOGIQUE DE VISIBILITÉ (ANTI-TRICHE / BROUILLARD DE GUERRE)
                        boolean visible = false;

                        // 1. En PHASE PLACEMENT : Seul le propriétaire voit sa grille QUAND C'EST SON
                        // TOUR
                        if (jeu.getPhase() == Jeu.Phase.PLACEMENT) {
                            if (jeu.getJoueurCourant() == idJoueurProprietaire) {
                                visible = true;
                            }
                        }
                        // 2. En PHASE JEU : On ne voit JAMAIS les bateaux intacts (sauf cheat code)
                        // (Ils deviendront rouges quand touchés, géré par le cas TOUCHE)

                        // Application de la couleur
                        if (visible) {
                            g.setColor(Color.GRAY);
                        } else {
                            g.setColor(new Color(173, 216, 230)); // Caché (comme de l'eau)
                        }
                        break;
                    case Grille.LOUPE:
                        g.setColor(Color.WHITE);
                        break;
                    case Grille.TOUCHE:
                        g.setColor(Color.RED);
                        break;
                }

                g.fillRect(x * TAILLE_CASE, y * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                g.setColor(Color.BLACK);
                g.drawRect(x * TAILLE_CASE, y * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
            }
        }

        // INDICATEUR VISUEL (Tour actif)
        // Si c'est à moi de jouer SUR CETTE GRILLE
        if (jeu != null) {
            boolean cestMonTour = (jeu.getJoueurCourant() == idJoueurProprietaire);

            // En phase placement, j'encadre MA grille en vert
            if (jeu.getPhase() == Jeu.Phase.PLACEMENT && cestMonTour) {
                g.setColor(Color.GREEN);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            }
        }
    }

    private void gererClic(int pixelX, int pixelY, int boutonSouris) {
        if (jeu == null)
            return;

        int x = pixelX / TAILLE_CASE;
        int y = pixelY / TAILLE_CASE;

        if (x >= 0 && x < Grille.TAILLE && y >= 0 && y < Grille.TAILLE) {

            // --- CAS 1 : PHASE DE PLACEMENT ---
            if (jeu.getPhase() == Jeu.Phase.PLACEMENT) {
                // Le joueur doit cliquer sur SA PROPRE grille
                if (jeu.getJoueurCourant() == idJoueurProprietaire) {

                    if (boutonSouris == MouseEvent.BUTTON3) { // Clic Droit = Rotation
                        jeu.basculerOrientation();
                        System.out.println(
                                "Orientation : " + (jeu.isOrientationHorizontale() ? "Horizontale" : "Verticale"));
                    } else { // Clic Gauche = Placer
                        jeu.placerNavireJoueur(x, y);
                    }
                    redessinerInterface();
                }
            }

            // --- CAS 2 : PHASE DE JEU (TIR) ---
            else if (jeu.getPhase() == Jeu.Phase.JEU) {
                // Le joueur doit cliquer sur la grille ADVERSE
                if (jeu.getJoueurCourant() != idJoueurProprietaire) {
                    jeu.jouerTour(x, y);
                    redessinerInterface();
                }
            }
        }
    }

    private void redessinerInterface() {
        this.repaint();
        Container parent = this.getParent();
        while (parent != null) {
            parent.repaint();
            parent = parent.getParent();
        }
    }
}
