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
                afficherMenuDifficulte();
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
     * Affiche le menu de sélection de la difficulté pour le mode PvE.
     */
    private void afficherMenuDifficulte() {
        this.getContentPane().removeAll();
        this.setLayout(new BorderLayout());

        JPanel panelDifficulte = new JPanel();
        panelDifficulte.setLayout(new GridLayout(5, 1, 15, 15));
        panelDifficulte.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel lblTitre = new JLabel("CHOIX DE LA DIFFICULTÉ", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitre.setForeground(new Color(25, 25, 112));

        JButton btnFacile = new JButton("Facile");
        JButton btnMoyen = new JButton("Moyenne");
        JButton btnDifficile = new JButton("Difficile");
        JButton btnRetour = new JButton("Retour");

        styleButton(btnFacile);
        styleButton(btnMoyen);
        styleButton(btnDifficile);
        styleButton(btnRetour);

        // Couleur spécifique pour la difficulté
        btnFacile.setBackground(new Color(200, 255, 200));
        btnMoyen.setBackground(new Color(255, 255, 200));
        btnDifficile.setBackground(new Color(255, 200, 200));

        // ACTIONS
        btnFacile.addActionListener(e -> demarrerPartiePvE(Jeu.NiveauDifficulte.FACILE));
        btnMoyen.addActionListener(e -> demarrerPartiePvE(Jeu.NiveauDifficulte.MOYENNE));
        btnDifficile.addActionListener(e -> demarrerPartiePvE(Jeu.NiveauDifficulte.DIFFICILE));

        btnRetour.addActionListener(e -> {
            this.dispose();
            new MenuGraphique().setVisible(true);
        });

        panelDifficulte.add(lblTitre);
        panelDifficulte.add(btnFacile);
        panelDifficulte.add(btnMoyen);
        panelDifficulte.add(btnDifficile);
        panelDifficulte.add(btnRetour);

        this.add(panelDifficulte, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void demarrerPartiePvE(Jeu.NiveauDifficulte niveau) {
        System.out.println("[UI] Lancement de la partie PvE - Niveau : " + niveau);
        afficherRegles(true, niveau);
    }

    /**
     * Lance l'affichage du jeu en utilisant le moteur Jeu.java (PvP par défaut)
     */
    private void demarrerPartieAvecJeu() {
        afficherRegles(false, null);
    }

    /**
     * Affiche l'écran des règles avant de lancer la partie.
     */
    private void afficherRegles(boolean isIA, Jeu.NiveauDifficulte niveau) {
        this.getContentPane().removeAll();
        this.setLayout(new BorderLayout());

        JPanel panelRegles = new JPanel();
        panelRegles.setLayout(new BorderLayout(20, 20));
        panelRegles.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel lblTitre = new JLabel("RÈGLES DU JEU", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitre.setForeground(new Color(25, 25, 112));

        JTextArea txtRegles = new JTextArea();
        txtRegles.setText(
                "● Plateau 10×10 par joueur.\n\n" +
                        "● Flotte : 1×4 (cuirassé), 2×3 (croiseurs), 3×2 (destroyers), 4×1 (torpilleurs).\n\n" +
                        "● Placement horizontal/vertical, sans contact (y compris en diagonale).\n\n" +
                        "● Tour à tour, un joueur tire sur une case adverse ;\n" +
                        "   touché ⇒ rejoue, à l’eau ⇒ main à l’adversaire.\n\n" +
                        "● Un navire est coulé quand toutes ses cases sont touchées.\n\n" +
                        "● Victoire : tous les navires adverses détruits.");
        txtRegles.setFont(new Font("Arial", Font.PLAIN, 14));
        txtRegles.setEditable(false);
        txtRegles.setLineWrap(true);
        txtRegles.setWrapStyleWord(true);
        txtRegles.setBackground(new Color(245, 245, 250));
        txtRegles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Panel de boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton btnRetour = new JButton("Retour");
        styleButton(btnRetour);
        btnRetour.setBackground(new Color(255, 200, 200));

        JButton btnCompris = new JButton("Compris !");
        styleButton(btnCompris);
        btnCompris.setBackground(new Color(200, 255, 200)); // Vert clair

        // ACTIONS
        btnRetour.addActionListener(e -> {
            this.dispose();
            new MenuGraphique().setVisible(true);
        });

        btnCompris.addActionListener(e -> {
            demarrerPartieGenerique(isIA, niveau);
        });

        panelBoutons.add(btnRetour);
        panelBoutons.add(btnCompris);

        panelRegles.add(lblTitre, BorderLayout.NORTH);
        panelRegles.add(new JScrollPane(txtRegles), BorderLayout.CENTER);
        panelRegles.add(panelBoutons, BorderLayout.SOUTH);

        this.add(panelRegles, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    /**
     * Méthode refactorisée pour initialiser l'UI du jeu
     */
    private Jeu demarrerPartieGenerique(boolean isIA, Jeu.NiveauDifficulte niveau) {
        System.out.println("[UI] Initialisation de l'interface de jeu...");

        // 1. On vide la fenêtre
        this.getContentPane().removeAll();

        // 2. Configuration pour le jeu
        this.setLayout(new BorderLayout());
        this.setSize(1000, 600); // Grande fenêtre pour jouer

        // 3. Création du moteur
        Jeu monJeu = new Jeu();
        if (isIA && niveau != null) {
            monJeu.setModeIA(niveau);
        }
        // On donne la référence de la fenêtre au moteur pour le retour menu
        monJeu.setFenetreJeu(this);

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

        // DÉTERMINATION DU TITRE DU JOUEUR 2
        String titreJ2Texte = "JOUEUR 2";
        if (isIA) {
            titreJ2Texte = "IA (" + niveau + ")";
        }

        JLabel titreJ2 = new JLabel(titreJ2Texte, SwingConstants.CENTER);
        titreJ2.setFont(new Font("Arial", Font.BOLD, 16));
        zoneJ2.add(titreJ2, BorderLayout.NORTH);
        zoneJ2.add(panelJ2, BorderLayout.CENTER);
        panelJ2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Panneau central pour les grilles
        JPanel centre = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        centre.add(zoneJ1);
        centre.add(zoneJ2);

        this.add(centre, BorderLayout.CENTER);

        // Bandeau d'aide et bouton au sud
        JPanel sudPanel = new JPanel(new BorderLayout());

        JLabel aide = new JLabel("Initialisation...", SwingConstants.CENTER);
        aide.setFont(new Font("Arial", Font.BOLD, 16)); // Plus gros pour être lisible
        aide.setForeground(new Color(50, 50, 50));
        aide.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnQuitterPartie = new JButton("Quitter");
        styleButton(btnQuitterPartie);
        btnQuitterPartie.setBackground(new Color(255, 200, 200)); // Rouge clair
        btnQuitterPartie.addActionListener(e -> {
            this.dispose();
            new MenuGraphique().setVisible(true);
        });

        sudPanel.add(aide, BorderLayout.CENTER);
        sudPanel.add(btnQuitterPartie, BorderLayout.EAST);

        this.add(sudPanel, BorderLayout.SOUTH);

        // TIMER DE RAFRAÎCHISSEMENT DE L'INTERFACE
        // Permet de mettre à jour le texte d'aide en temps réel selon l'état du jeu
        new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // On demande au moteur : "Quoi faire maintenant ?"
                String instruction = monJeu.getInstructionPlacement();
                aide.setText(instruction);

                // Si on a fini le placement, on change le style
                if (monJeu.getPhase() == Jeu.Phase.JEU) {
                    aide.setForeground(new Color(200, 0, 0)); // Rouge combat
                }
            }
        }).start();

        this.revalidate();
        this.repaint();

        return monJeu;
    }
}
