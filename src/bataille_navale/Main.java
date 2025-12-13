package bataille_navale;

public class Main {
    public static void main(String[] args) {
        // Lancement de l'interface graphique dans le thread dédié à Swing (EDT)
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MenuGraphique menu = new MenuGraphique();
                menu.setVisible(true); // Rend la fenêtre visible
            }
        });
    }
}