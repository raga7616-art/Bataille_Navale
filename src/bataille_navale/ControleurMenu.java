package bataille_navale;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Le Contrôleur gère les interactions de l'utilisateur.
 * Il reçoit les événements de la Vue et exécute la logique appropriée.
 */
public class ControleurMenu implements ActionListener {

    private JFrame vueParente;

    // Le contrôleur a besoin d'une référence à la vue pour afficher les popups
    // par-dessus
    public ControleurMenu(JFrame vueParente) {
        this.vueParente = vueParente;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // On vérifie si l'action vient bien du bouton "Réglages" (via la commande
        // d'action ou autre)
        // Ici, comme on l'attache spécifiquement, on exécute directement la logique.

        System.out.println("[Contrôleur] Clic sur Réglages détecté.");
        ouvrirSelecteurDossier();
    }

    private void ouvrirSelecteurDossier() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Sélectionnez le dossier de sauvegarde");

        // IMPORTANT : On restreint la sélection aux répertoires uniquement
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Affiche la boîte de dialogue
        int resultat = chooser.showOpenDialog(vueParente);

        if (resultat == JFileChooser.APPROVE_OPTION) {
            File dossierSelectionne = chooser.getSelectedFile();
            System.out.println("[Contrôleur] Dossier validé : " + dossierSelectionne.getAbsolutePath());
        } else {
            System.out.println("[Contrôleur] Sélection annulée.");
        }
    }
}
