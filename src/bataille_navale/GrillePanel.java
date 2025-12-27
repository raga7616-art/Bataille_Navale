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

    // Pour l'effet "Ghost" (Prévisualisation)
    private int sourisX = -1;
    private int sourisY = -1;

    public GrillePanel(Grille grille, Jeu jeu, int idJoueurProprietaire) {
        this.grille = grille;
        this.jeu = jeu;
        this.idJoueurProprietaire = idJoueurProprietaire;

        this.setPreferredSize(new Dimension(Grille.TAILLE * TAILLE_CASE + 1, Grille.TAILLE * TAILLE_CASE + 1));

        // Ajout de l'écouteur de souris pour les CLICS
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gererClic(e.getX(), e.getY(), e.getButton());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Quand la souris sort, on cache le fantôme
                sourisX = -1;
                sourisY = -1;
                repaint();
            }
        });

        // Ajout de l'écouteur de mouvement pour le GHOST EFFECT
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                sourisX = e.getX() / TAILLE_CASE;
                sourisY = e.getY() / TAILLE_CASE;
                repaint(); // Rafraîchissement pour l'animation
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Dessin de la grille et des états fixes
        for (int y = 0; y < Grille.TAILLE; y++) {
            for (int x = 0; x < Grille.TAILLE; x++) {
                int etat = grille.getEtat(x, y);

                switch (etat) {
                    case Grille.EAU:
                        g.setColor(new Color(173, 216, 230)); // Bleu clair
                        break;
                    case Grille.NAVIRE:
                        // VISIBILITÉ (BROUILLARD DE GUERRE)
                        boolean visible = false;
                        if (jeu.getPhase() == Jeu.Phase.PLACEMENT) {
                            if (jeu.getJoueurCourant() == idJoueurProprietaire) {
                                visible = true;
                            }
                        }

                        if (visible) {
                            g.setColor(Color.GRAY);
                        } else {
                            g.setColor(new Color(173, 216, 230)); // Caché
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

        // 2. Dessin du GHOST EFFECT (Par-dessus la grille)
        dessinerGhost(g);

        // 3. Cadre vert si c'est mon tour de placer
        if (jeu != null) {
            boolean cestMonTour = (jeu.getJoueurCourant() == idJoueurProprietaire);
            if (jeu.getPhase() == Jeu.Phase.PLACEMENT && cestMonTour) {
                g.setColor(Color.GREEN);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            }
        }
    }

    private void dessinerGhost(Graphics g) {
        // Prévisualisation uniquement en phase de placement sur sa propre grille
        if (jeu.getPhase() == Jeu.Phase.PLACEMENT &&
                jeu.getJoueurCourant() == idJoueurProprietaire &&
                sourisX >= 0 && sourisX < Grille.TAILLE && sourisY >= 0 && sourisY < Grille.TAILLE) {

            Navire navireCourant = jeu.getNavireCourant();

            if (navireCourant != null) {
                int taille = navireCourant.getTaille();
                boolean horizontal = jeu.isOrientationHorizontale();

                // On vérifie si le placement serait valide
                boolean valide = grille.peutPlacer(taille, sourisX, sourisY, horizontal);

                // Vert transparent si OK, Rouge transparent si Collision
                g.setColor(valide ? new Color(0, 255, 0, 100) : new Color(255, 0, 0, 100));

                for (int i = 0; i < taille; i++) {
                    int cx = horizontal ? sourisX + i : sourisX;
                    int cy = horizontal ? sourisY : sourisY + i;

                    // On ne dessine que ce qui dépasse pas trop (ou on coupe)
                    if (cx < Grille.TAILLE && cy < Grille.TAILLE) {
                        g.fillRect(cx * TAILLE_CASE, cy * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                    }
                }
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
                if (jeu.getJoueurCourant() == idJoueurProprietaire) {
                    if (boutonSouris == MouseEvent.BUTTON3) { // Clic Droit = Rotation
                        jeu.basculerOrientation();
                        // Feedback Console
                        System.out.println(
                                "Orientation : " + (jeu.isOrientationHorizontale() ? "Horizontale" : "Verticale"));
                        repaint(); // Mettre à jour le fantôme immédiatement
                    } else { // Clic Gauche = Placer
                        jeu.placerNavireJoueur(x, y);
                    }
                    redessinerInterface();
                }
            }

            // --- CAS 2 : PHASE DE JEU (TIR) ---
            else if (jeu.getPhase() == Jeu.Phase.JEU) {
                if (jeu.getJoueurCourant() != idJoueurProprietaire) {
                    if (boutonSouris == MouseEvent.BUTTON1) {
                        // ANTI-GASPI : Vérification avant de tirer
                        if (grille.estDejaJoue(x, y)) {
                            System.out.println("[UI] Clic inutile : case déjà jouée.");
                            return; // On ne fait rien
                        }

                        jeu.jouerTour(x, y);
                        redessinerInterface();
                    }
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
