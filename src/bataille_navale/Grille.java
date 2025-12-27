package bataille_navale;

public class Grille {
    // Constantes pour la lisibilité
    public static final int TAILLE = 10;
    public static final int EAU = 0;
    public static final int NAVIRE = 1; // Navire intact
    public static final int LOUPE = 2; // Tir à l'eau
    public static final int TOUCHE = 3; // Navire touché

    private int[][] matrice;
    private Navire[][] grilleNavires; // Pour savoir quel navire est sur quelle case

    public Grille() {
        this.matrice = new int[TAILLE][TAILLE];
        this.grilleNavires = new Navire[TAILLE][TAILLE];
        // Initialisation à 0 (EAU) par défaut
    }

    /**
     * Réinitialise la grille pour une nouvelle partie.
     */
    public void vider() {
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                this.matrice[y][x] = EAU;
                this.grilleNavires[y][x] = null;
            }
        }
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
     * Place définitivement le navire.
     * Prend maintenant l'objet Navire pour le stocker.
     */
    public void placerNavire(Navire navire, int x, int y, boolean horizontal) {
        for (int i = 0; i < navire.getTaille(); i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            matrice[cy][cx] = NAVIRE;
            grilleNavires[cy][cx] = navire; // On lie la case au bateau
        }
    }

    /**
     * Vérifie si une case a déjà été jouée (Touché ou Loupé).
     */
    public boolean estDejaJoue(int x, int y) {
        if (!estDansGrille(x, y))
            return true;
        return matrice[y][x] == TOUCHE || matrice[y][x] == LOUPE;
    }

    /**
     * Gère un tir reçu. Renvoie true si un navire est touché.
     * Met à jour l'état du navire si touché.
     */
    public boolean recevoirTir(int x, int y) {
        if (!estDansGrille(x, y))
            return false;

        if (matrice[y][x] == NAVIRE) {
            matrice[y][x] = TOUCHE;
            // On notifie le navire qu'il a mal
            if (grilleNavires[y][x] != null) {
                grilleNavires[y][x].estTouche();
            }
            return true;
        } else if (matrice[y][x] == EAU) {
            matrice[y][x] = LOUPE;
            return false;
        }
        return false; // Déjà tiré
    }

    /**
     * Récupère le navire situé à cette position (utile pour savoir s'il vient de
     * couler).
     */
    public Navire getNavireEn(int x, int y) {
        if (!estDansGrille(x, y))
            return null;
        return grilleNavires[y][x];
    }

    private boolean estDansGrille(int x, int y) {
        return x >= 0 && x < TAILLE && y >= 0 && y < TAILLE;
    }

    public int getEtat(int x, int y) {
        return matrice[y][x];
    }

    /**
     * Vérifie si tous les navires ont été coulés.
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
        return casesRestantes == 0;
    }
}