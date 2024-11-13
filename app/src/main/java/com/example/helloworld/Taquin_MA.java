package com.example.helloworld;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class Taquin_MA extends AppCompatActivity {

    private final TextView[][] tiles = new TextView[3][3]; // Nouvelle grille 3x3
    private final TextView[][] solutionTiles = new TextView[3][3]; // Nouvelle grille solution 3x3
    private int emptyRow = 2, emptyCol = 2; // Position initiale de l'espace vide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taquin);

        // Masquer complètement la barre de statut et la barre de navigation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideSystemUI();
        }

        // Récupérer les GridLayouts pour les tuiles du jeu et de la solution
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        GridLayout solutionGrid = findViewById(R.id.solutionGrid);

        // Initialisation des tuiles du jeu
        initializeTiles(gridLayout, tiles);

        // Initialisation des tuiles de la solution
        initializeSolutionTiles(solutionGrid);

        // Mélanger les tuiles du jeu
        shuffleTiles(); // Mélanger les tuiles au démarrage
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideSystemUI(); // Assurer que le mode plein écran est réactivé lors de la reprise
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        // Masquer la barre de statut et la barre de navigation en utilisant les options modernes
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initializeTiles(GridLayout gridLayout, TextView[][] tileArray) {
        for (int i = 0; i < 3; i++) { // Changer la taille de 4 à 3
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1), "id", getPackageName());
                if (resID == 0) {
                    resID = getResources().getIdentifier("tile_empty", "id", getPackageName());
                }
                TextView tile = findViewById(resID);
                tileArray[i][j] = tile;

                tile.setText(String.valueOf(i * 3 + j + 1)); // Remplir les tuiles avec les valeurs
                if (i == 2 && j == 2) {
                    tile.setText(""); // La dernière tuile est vide
                }

                final int row = i;
                final int col = j;
                tile.setOnClickListener(v -> onTileClick(row, col));
            }
        }
    }

    private void initializeSolutionTiles(GridLayout solutionGrid) {
        for (int i = 0; i < 3; i++) { // Adapter à la grille 3x3
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1) + "_solution", "id", getPackageName());
                if (resID == 0) {
                    resID = getResources().getIdentifier("tile_empty_solution", "id", getPackageName());
                }
                TextView tile = findViewById(resID);
                solutionTiles[i][j] = tile;

                // Remplir les tuiles avec les valeurs de la solution
                tile.setText(String.valueOf(i * 3 + j + 1)); // Remplir les tuiles avec les valeurs
                if (i == 2 && j == 2) {
                    tile.setText(""); // La dernière tuile est vide dans la solution
                }
            }
        }
    }

    private void onTileClick(int row, int col) {
        if (Math.abs(emptyRow - row) + Math.abs(emptyCol - col) == 1) { // Vérifie si la tuile est adjacente
            tiles[emptyRow][emptyCol].setText(tiles[row][col].getText());
            tiles[row][col].setText("");
            emptyRow = row;
            emptyCol = col;

            if (isGameWon()) {
                Toast.makeText(this, "Bravo, vous avez gagné!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void shuffleTiles() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) { // Effectuer 100 mouvements aléatoires
            int direction = random.nextInt(4);
            int newRow = emptyRow, newCol = emptyCol;

            switch (direction) {
                case 0: newRow--; break; // Haut
                case 1: newRow++; break; // Bas
                case 2: newCol--; break; // Gauche
                case 3: newCol++; break; // Droite
            }

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) { // Vérifier les limites de la grille 3x3
                tiles[emptyRow][emptyCol].setText(tiles[newRow][newCol].getText());
                tiles[newRow][newCol].setText("");
                emptyRow = newRow;
                emptyCol = newCol;
            }
        }
    }

    private boolean isGameWon() {
        for (int i = 0; i < 3; i++) { // Vérifier la grille 3x3
            for (int j = 0; j < 3; j++) {
                if (!tiles[i][j].getText().toString().equals(String.valueOf(i * 3 + j + 1))) {
                    return false;
                }
            }
        }
        return true;
    }
}
