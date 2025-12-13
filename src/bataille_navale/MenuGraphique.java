package bataille_navale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGraphique extends JFrame {

    public MenuGraphique() {
        // 1. Configuration de la fenêtre
        setTitle("Bataille Navale");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre à l'écran

        // --- SECTION MENU BAR (VUE) ---
        // Création de la barre de menu
        JMenuBar menuBar = new JMenuBar();

        // Création du menu "Options"
        JMenu menuOptions = new JMenu("Options");

        // Création de l'item "Réglages"
        JMenuItem itemReglages = new JMenuItem("Réglages");

        // --- CONNEXION CONTROLEUR ---
        // On instancie le contrôleur en lui passant la fenêtre actuelle (this)
        ControleurMenu controleur = new ControleurMenu(this);
        itemReglages.addActionListener(controleur);

        // Assemblage
        menuOptions.add(itemReglages);
        menuBar.add(menuOptions);

        // Attachement de la barre de menu à la fenêtre
        this.setJMenuBar(menuBar);
        // ------------------------------

        // 2. Création du conteneur principal (Panneau)
        JPanel panel = new JPanel();
        // Layout : Grille de 4 lignes (Titre + 3 boutons), 1 colonne, espacement de
        // 15px
        panel.setLayout(new GridLayout(4, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Marges intérieures

        // 3. Le Titre
        JLabel lblTitre = new JLabel("Bataille Navale", SwingConstants.CENTER);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitre.setForeground(new Color(25, 25, 112)); // Bleu nuit

        // 4. Les Boutons
        JButton btnPvIA = new JButton("Joueur vs IA");
        JButton btnPvP = new JButton("Joueur vs Joueur");
        JButton btnQuitter = new JButton("Quitter");

        // Style des boutons (Optionnel, pour faire plus propre)
        styleButton(btnPvIA);
        styleButton(btnPvP);
        styleButton(btnQuitter);

        // 5. Actions des boutons
        btnPvIA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuGraphique.this, "Lancement du mode Joueur vs IA \n(À implémenter)");
                // Ici on appellera plus tard la fenêtre de jeu
            }
        });

        btnPvP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuGraphique.this,
                        "Lancement du mode Joueur vs Joueur \n(À implémenter)");
            }
        });

        btnQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Ferme l'application
            }
        });

        // 6. Ajout des composants au panneau
        panel.add(lblTitre);
        panel.add(btnPvIA);
        panel.add(btnPvP);
        panel.add(btnQuitter);

        // Ajout du panneau à la fenêtre
        this.add(panel);
    }

    // Petite méthode utilitaire pour uniformiser le style des boutons
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setFocusPainted(false); // Enlève le cadre de sélection vilain
    }
}
