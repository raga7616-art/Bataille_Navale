package bataille_navale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGraphique extends JFrame {

    public MenuGraphique() {
        // 1. Configuration de la fenêtre
        setTitle("Bataille Navale - G5");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre à l'écran

        // --- MENU BAR ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuOptions = new JMenu("Fichier");
        JMenuItem itemReglages = new JMenuItem("Réglages Dossier");

        // Connexion Contrôleur pour le menu
        ControleurMenu controleur = new ControleurMenu(this);
        itemReglages.addActionListener(controleur);

        menuOptions.add(itemReglages);
        menuBar.add(menuOptions);
        this.setJMenuBar(menuBar);

        // --- CONTENU PRINCIPAL ---
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel lblTitre = new JLabel("BATAILLE NAVALE", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitre.setForeground(new Color(25, 25, 112)); // Bleu nuit

        JButton btnPvIA = new JButton("Joueur vs Ordinateur");
        JButton btnPvP = new JButton("Joueur vs Joueur (PvP)");
        JButton btnQuitter = new JButton("Quitter");

        styleButton(btnPvIA);
        styleButton(btnPvP);
        styleButton(btnQuitter);

        // ACTIONS
        btnPvIA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuGraphique.this, "Mode PvE : À venir (Partie de vos collègues !)");
            }
        });

        // C'est ici qu'on lance le vrai jeu
        btnPvP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                demarrerPartieAvecJeu();
            }
        });

        btnQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        panel.add(lblTitre);
        panel.add(btnPvIA);
        panel.add(btnPvP);
        panel.add(btnQuitter);
        this.add(panel);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(240, 240, 240));
    }

    /**
     * Lance l'affichage du jeu en utilisant le moteur Jeu.java
     */
    private void demarrerPartieAvecJeu() {
        System.out.println("[UI] Lancement de la partie PvP...");

        // 1. On vide la fenêtre
        this.getContentPane().removeAll();

        // 2. Configuration pour le jeu
        this.setLayout(new BorderLayout());
        this.setSize(1000, 600); // Grande fenêtre pour jouer

        // 3. Création du moteur
        Jeu monJeu = new Jeu();

        // 4. Création des Vues
        GrillePanel panelJ1 = new GrillePanel(monJeu.getGrilleJ1(), monJeu, 1);
        GrillePanel panelJ2 = new GrillePanel(monJeu.getGrilleJ2(), monJeu, 2);

        // Conteneurs avec Titres
        JPanel zoneJ1 = new JPanel(new BorderLayout());
        JLabel titreJ1 = new JLabel("FLOTTE JOUEUR 1", SwingConstants.CENTER);
        titreJ1.setFont(new Font("Arial", Font.BOLD, 16));
        zoneJ1.add(titreJ1, BorderLayout.NORTH);
        zoneJ1.add(panelJ1, BorderLayout.CENTER);
        // On entoure un peu la grille pour qu'elle respire
        panelJ1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JPanel zoneJ2 = new JPanel(new BorderLayout());
        JLabel titreJ2 = new JLabel("FLOTTE JOUEUR 2", SwingConstants.CENTER);
        titreJ2.setFont(new Font("Arial", Font.BOLD, 16));
        zoneJ2.add(titreJ2, BorderLayout.NORTH);
        zoneJ2.add(panelJ2, BorderLayout.CENTER);
        panelJ2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Panneau central pour les grilles
        JPanel centre = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        centre.add(zoneJ1);
        centre.add(zoneJ2);

        this.add(centre, BorderLayout.CENTER);

        // Bandeau d'aide au sud
        JLabel aide = new JLabel("PHASE DE PLACEMENT : Joueur 1, placez vos navires. (Clic Droit pour tourner)",
                SwingConstants.CENTER);
        aide.setFont(new Font("Arial", Font.ITALIC, 14));
        aide.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(aide, BorderLayout.SOUTH);

        // Un petit filou pour mettre à jour le texte du bas selon le jeu
        // (En vrai MVC strict, on utiliserait un Observer, mais ici on va tricher un
        // peu
        // en ajoutant un thread qui met à jour le texte, ou on laisse statique pour
        // l'instant)

        this.revalidate();
        this.repaint();
    }
}
