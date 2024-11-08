package com.example.helloworld;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.helloworld.R;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView[][] tiles = new TextView[4][4];
    private int emptyRow = 3, emptyCol = 3; // Position initiale de l'espace vide

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cacher la barre de navigation pour le mode plein écran
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION      // Cacher la barre de navigation
                        | View.SYSTEM_UI_FLAG_FULLSCREEN         // Cacher la barre de statut
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY   // Activer le mode immersif
        );

        // Récupérer le GridLayout
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        if (gridLayout == null) {
            // Log si le GridLayout n'est pas trouvé
            Log.e("MainActivity", "GridLayout not found!");
            return; // Sortir si la vue n'est pas trouvée
        }

        try {
            // Initialisation des tuiles avec les IDs
            TextView tile1 = findViewById(R.id.tile_1);
            TextView tile2 = findViewById(R.id.tile_2);
            TextView tile3 = findViewById(R.id.tile_3);
            TextView tile4 = findViewById(R.id.tile_4);
            TextView tile5 = findViewById(R.id.tile_5);
            TextView tile6 = findViewById(R.id.tile_6);
            TextView tile7 = findViewById(R.id.tile_7);
            TextView tile8 = findViewById(R.id.tile_8);
            TextView tile9 = findViewById(R.id.tile_9);
            TextView tile10 = findViewById(R.id.tile_10);
            TextView tile11 = findViewById(R.id.tile_11);
            TextView tile12 = findViewById(R.id.tile_12);
            TextView tile13 = findViewById(R.id.tile_13);
            TextView tile14 = findViewById(R.id.tile_14);
            TextView tile15 = findViewById(R.id.tile_15);
            TextView tileEmpty = findViewById(R.id.tile_empty);

            // Vérifiez si les TextViews sont bien trouvés
            if (tile1 == null || tile2 == null || tileEmpty == null) {
                Log.e("MainActivity", "One or more TextViews not found!");
                return; // Sortir si une tuile est manquante
            }

            // Mettre les TextViews dans le tableau tiles
            tiles[0][0] = tile1;
            tiles[0][1] = tile2;
            tiles[0][2] = tile3;
            tiles[0][3] = tile4;
            tiles[1][0] = tile5;
            tiles[1][1] = tile6;
            tiles[1][2] = tile7;
            tiles[1][3] = tile8;
            tiles[2][0] = tile9;
            tiles[2][1] = tile10;
            tiles[2][2] = tile11;
            tiles[2][3] = tile12;
            tiles[3][0] = tile13;
            tiles[3][1] = tile14;
            tiles[3][2] = tile15;
            tiles[3][3] = tileEmpty;

            int number = 1;

            // Initialiser les tuiles avec les numéros, sauf pour l'espace vide
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (tiles[i][j] != tileEmpty) {
                        tiles[i][j].setText(String.valueOf(number++));
                    } else {
                        tiles[i][j].setText(""); // L'espace vide
                    }
                    final int row = i;
                    final int col = j;

                    tiles[i][j].setOnClickListener(v -> onTileClick(row, col));
                }
            }

            shuffleTiles(); // Mélanger les tuiles au démarrage

        } catch (Exception e) {
            Log.e("MainActivity", "Error during initialization", e);
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

            if (newRow >= 0 && newRow < 4 && newCol >= 0 && newCol < 4) {
                onTileClick(newRow, newCol);
            }
        }
    }

    private boolean isGameWon() {
        int number = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String text = tiles[i][j].getText().toString();
                if (i == 3 && j == 3) {
                    return text.isEmpty(); // La dernière case doit être vide
                } else if (!text.equals(String.valueOf(number++))) {
                    return false; // La tuile n'est pas dans le bon ordre
                }
            }
        }
        return true;
    }
}
