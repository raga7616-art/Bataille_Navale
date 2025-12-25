package bataille_navale;

public class Grille {
    // Constantes pour la lisibilité
    public static final int TAILLE = 10;
    public static final int EAU = 0;
    public static final int NAVIRE = 1; // Navire intact
    public static final int LOUPE = 2; // Tir à l'eau
    public static final int TOUCHE = 3; // Navire touché

    private int[][] matrice;

    public Grille() {
        this.matrice = new int[TAILLE][TAILLE];
        // Initialisation à 0 (EAU) par défaut
    }

    /**
     * Tente de placer un navire. Vérifie les limites et la règle de non-contact
     * (diagonales incluses).
     */
    public boolean peutPlacer(int taille, int x, int y, boolean horizontal) {
        // 1. Vérification des limites de la grille
        if (horizontal) {
            if (x + taille > TAILLE)
                return false;
        } else {
            if (y + taille > TAILLE)
                return false;
        }

        // 2. Vérification de l'espace libre (y compris diagonales autour)
        for (int i = 0; i < taille; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;

            // On regarde les 9 cases autour de chaque segment du bateau
            if (!estZoneLibre(cx, cy)) {
                return false; // Une case voisine est occupée
            }
        }
        return true;
    }

    /**
     * Helper pour vérifier qu'une case et ses voisins immédiats sont libres (EAU).
     */
    private boolean estZoneLibre(int cx, int cy) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = cx + dx;
                int ny = cy + dy;
                // Si on est dans la grille, on vérifie que c'est de l'eau
                if (nx >= 0 && nx < TAILLE && ny >= 0 && ny < TAILLE) {
                    if (matrice[ny][nx] != EAU) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Place définitivement le navire (On suppose que peutPlacer a renvoyé true
     * avant).
     */
    public void placerNavire(int taille, int x, int y, boolean horizontal) {
        for (int i = 0; i < taille; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            matrice[cy][cx] = NAVIRE;
        }
    }

    /**
     * Gère un tir reçu. Renvoie true si un navire est touché.
     */
    public boolean recevoirTir(int x, int y) {
        if (x < 0 || x >= TAILLE || y < 0 || y >= TAILLE)
            return false;

        if (matrice[y][x] == NAVIRE) {
            matrice[y][x] = TOUCHE;
            return true;
        } else if (matrice[y][x] == EAU) {
            matrice[y][x] = LOUPE;
            return false;
        }
        return false; // Déjà tiré ici
    }

    public int getEtat(int x, int y) {
        return matrice[y][x];
    }

    /**
     * Vérifie si tous les navires ont été coulés.
     * 
     * @return true si plus aucune case NAVIRE ne subsiste (uniquement EAU, LOUPE ou
     *         TOUCHE).
     */
    public boolean estDefaite() {
        int casesRestantes = 0;
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if (matrice[y][x] == NAVIRE) {
                    casesRestantes++;
                }
            }
        }
        if (casesRestantes == 0)
            return true;

        // (DEBUG TEMPORAIRE)
        // System.out.println(" [Debug] Il reste encore " + casesRestantes + " cases de
        // navires à toucher.");
        return false;
    }
}