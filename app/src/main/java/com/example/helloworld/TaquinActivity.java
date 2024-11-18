package com.example.helloworld;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class TaquinActivity extends AppCompatActivity {

    private TextView[][] tiles = new TextView[3][3]; // Grille du jeu 3x3
    private TextView[][] solutionTiles = new TextView[3][3]; // Grille de la solution 3x3
    private int emptyRow = 2, emptyCol = 2; // Position initiale de l'espace vide
    private Dialog pauseDialog; // Dialog pour la pause

    // Timer
    private TextView timerTextView; // Affichage du timer
    private int remainingSeconds = 120; // Compte à rebours initial (60 secondes)
    private Handler timerHandler = new Handler();
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taquin);

        hideSystemUI();

        // Initialisation des éléments
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        GridLayout solutionGrid = findViewById(R.id.solutionGrid);
        timerTextView = findViewById(R.id.timer); // Ajouter un TextView pour afficher le timer

        initializeTiles(gridLayout, tiles);
        initializeSolutionTiles(solutionGrid);
        shuffleTiles();

        ImageView imageSettings = findViewById(R.id.settings);
        imageSettings.setOnClickListener(v -> showPauseDialog());

        startCountdownTimer(); // Démarrer le compte à rebours
    }

    private void startCountdownTimer() {
        isTimerRunning = true;
        timerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    timerTextView.setText(String.valueOf(remainingSeconds)); // Met à jour l'affichage
                    remainingSeconds--;
                    timerHandler.postDelayed(this, 1000); // Réexécute après 1 seconde
                } else {
                    timerTextView.setText("0"); // Affiche 0 quand le temps est écoulé
                    onCountdownFinished(); // Gère la fin du compte à rebours
                }
            }
        });
    }

    private void stopCountdownTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacksAndMessages(null); // Arrête le timer
    }

    private void onCountdownFinished() {
        Toast.makeText(this, "Temps écoulé ! Vous avez perdu.", Toast.LENGTH_LONG).show();
        // Optionnel : bloquer l'interaction avec la grille
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI(); // Assurer le mode plein écran au retour
        if (!isTimerRunning) {
            startCountdownTimer(); // Reprendre le timer si nécessaire
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountdownTimer(); // Arrêter le timer quand l'activité est mise en pause
    }

    private void showPauseDialog() {
        // Créer et afficher un dialog personnalisé pour la pause
        stopCountdownTimer(); // Mettre en pause le compte à rebours

        pauseDialog = new Dialog(this);
        pauseDialog.setContentView(R.layout.dialog_pause);
        pauseDialog.setCancelable(false); // Désactiver la fermeture en cliquant en dehors du dialog

        Button buttonResume = pauseDialog.findViewById(R.id.btn_resume);
        Button buttonRestart = pauseDialog.findViewById(R.id.btn_restart);
        Button buttonQuit = pauseDialog.findViewById(R.id.btn_quit);

        buttonResume.setOnClickListener(v -> {
            pauseDialog.dismiss();
            startCountdownTimer(); // Reprendre le timer
        });

        buttonRestart.setOnClickListener(v -> {
            shuffleTiles();
            remainingSeconds = 120; // Réinitialise le timer
            timerTextView.setText(String.valueOf(remainingSeconds));
            pauseDialog.dismiss();
            startCountdownTimer(); // Recommence le compte à rebours
        });

        buttonQuit.setOnClickListener(v -> finish());

        pauseDialog.show();
    }

    private void hideSystemUI() {
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1), "id", getPackageName());
                TextView tile = findViewById(resID);
                tileArray[i][j] = tile;

                tile.setText(String.valueOf(i * 3 + j + 1));
                if (i == 2 && j == 2) {
                    tile.setText(""); // Dernière tuile vide
                }

                final int row = i;
                final int col = j;
                tile.setOnClickListener(v -> onTileClick(row, col));
            }
        }
    }

    private void initializeSolutionTiles(GridLayout solutionGrid) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1) + "_solution", "id", getPackageName());
                TextView tile = findViewById(resID);
                solutionTiles[i][j] = tile;

                tile.setText(String.valueOf(i * 3 + j + 1));
                if (i == 2 && j == 2) {
                    tile.setText(""); // Dernière tuile vide
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
                stopCountdownTimer(); // Arrêter le timer
                Toast.makeText(this, "Bravo, vous avez gagné!", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void shuffleTiles() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int direction = random.nextInt(4);
            int newRow = emptyRow, newCol = emptyCol;

            switch (direction) {
                case 0: newRow--; break; // Haut
                case 1: newRow++; break; // Bas
                case 2: newCol--; break; // Gauche
                case 3: newCol++; break; // Droite
            }

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                tiles[emptyRow][emptyCol].setText(tiles[newRow][newCol].getText());
                tiles[newRow][newCol].setText("");
                emptyRow = newRow;
                emptyCol = newCol;
            }
        }
    }

    private boolean isGameWon() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Vérifier les tuiles sauf la dernière
                if (i == 2 && j == 2) {
                    if (!tiles[i][j].getText().toString().isEmpty()) {
                        return false; // La dernière case doit être vide
                    }
                } else {
                    if (!tiles[i][j].getText().toString().equals(String.valueOf(i * 3 + j + 1))) {
                        return false; // Les autres cases doivent correspondre à leur numéro
                    }
                }
            }
        }
        return true;
    }

}
