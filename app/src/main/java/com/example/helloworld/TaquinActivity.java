package com.example.helloworld;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class TaquinActivity extends AppCompatActivity {

    private ImageView[][] tiles = new ImageView[3][3];
    private ImageView[][] solutionTiles = new ImageView[3][3];

    private int emptyRow = 2, emptyCol = 2;
    private Bitmap originalBitmap;

    private Dialog pauseDialog;
    private Dialog endDialog;

    private TextView timerTextView;
    private int remainingSeconds = 120;
    private Handler timerHandler = new Handler();
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taquin);

        // Masquer l'interface système
        hideSystemUI();

        // Initialisation de la grille de jeu et de la solution
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        GridLayout solutionGrid = findViewById(R.id.solutionGrid);
        timerTextView = findViewById(R.id.timer);

        // Chargement de l'image originale
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enfant_espace);

        // Initialisation des tuiles
        initializeTiles(gridLayout, tiles);
        initializeSolutionTiles(solutionGrid);

        // Mélange des tuiles
        shuffleTiles();

        // Paramètres du jeu
        ImageView imageSettings = findViewById(R.id.settings);
        imageSettings.setOnClickListener(v -> showPauseDialog());

        // Démarre le chronomètre
        startCountdownTimer();
    }

    // Démarre le chronomètre
    private void startCountdownTimer() {
        isTimerRunning = true;
        timerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    timerTextView.setText(String.valueOf(remainingSeconds));
                    remainingSeconds--;
                    timerHandler.postDelayed(this, 1000);
                } else {
                    timerTextView.setText("0");
                    onCountdownFinished();
                }
            }
        });
    }

    // Arrête le chronomètre
    private void stopCountdownTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (!isTimerRunning) {
            startCountdownTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountdownTimer();
    }

    // Affiche le dialogue de pause
    private void showPauseDialog() {
        stopCountdownTimer();

        pauseDialog = new Dialog(this);
        pauseDialog.setContentView(R.layout.dialog_pause);
        pauseDialog.setCancelable(false);

        Button buttonResume = pauseDialog.findViewById(R.id.btn_resume);
        Button buttonRestart = pauseDialog.findViewById(R.id.btn_restart);
        Button buttonQuit = pauseDialog.findViewById(R.id.btn_quit);

        // Actions des boutons du dialogue de pause
        buttonResume.setOnClickListener(v -> {
            pauseDialog.dismiss();
            startCountdownTimer();
        });

        buttonRestart.setOnClickListener(v -> {
            shuffleTiles();
            remainingSeconds = 120;
            timerTextView.setText(String.valueOf(remainingSeconds));
            pauseDialog.dismiss();
            startCountdownTimer();
        });

        buttonQuit.setOnClickListener(v -> finish());

        pauseDialog.show();
    }

    // Masque l'interface système pour passer en mode plein écran
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

    // Initialisation des tuiles de la grille de jeu
    private void initializeTiles(GridLayout gridLayout, ImageView[][] tileArray) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1), "id", getPackageName());
                ImageView tile = findViewById(resID);
                tileArray[i][j] = tile;

                // On ne met pas d'image sur la case vide (en bas à droite)
                if (i == 2 && j == 2) {
                    tile.setImageResource(0);
                } else {
                    tile.setImageBitmap(getTileBitmap(i, j));
                }

                final int row = i;
                final int col = j;
                tile.setOnClickListener(v -> onTileClick(row, col));
            }
        }
    }

    // Initialisation des tuiles de la solution
    private void initializeSolutionTiles(GridLayout solutionGrid) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1) + "_solution", "id", getPackageName());
                ImageView tile = findViewById(resID);
                solutionTiles[i][j] = tile;

                if (i == 2 && j == 2) {
                    tile.setImageResource(0);
                } else {
                    tile.setImageBitmap(getTileBitmap(i, j));
                }
            }
        }
    }

    // Gère le clic sur une tuile
    private void onTileClick(int row, int col) {
        if (Math.abs(emptyRow - row) + Math.abs(emptyCol - col) == 1) {
            Bitmap tempBitmap = tiles[row][col].getDrawable() != null ? ((BitmapDrawable) tiles[row][col].getDrawable()).getBitmap() : null;
            tiles[emptyRow][emptyCol].setImageBitmap(tempBitmap);
            tiles[row][col].setImageResource(0);
            emptyRow = row;
            emptyCol = col;

            // Vérification après chaque mouvement
            if (isGameWon()) {
                stopCountdownTimer();
                onGameWon();
            }
        }
    }

    // Mélange aléatoire des tuiles
    private void shuffleTiles() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int direction = random.nextInt(4);
            int newRow = emptyRow, newCol = emptyCol;

            switch (direction) {
                case 0: newRow--; break;
                case 1: newRow++; break;
                case 2: newCol--; break;
                case 3: newCol++; break;
            }

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                Bitmap tempBitmap = tiles[newRow][newCol].getDrawable() != null ? ((BitmapDrawable) tiles[newRow][newCol].getDrawable()).getBitmap() : null;
                tiles[emptyRow][emptyCol].setImageBitmap(tempBitmap);
                tiles[newRow][newCol].setImageResource(0);
                emptyRow = newRow;
                emptyCol = newCol;
            }
        }
    }

    // Action lorsque le chronomètre arrive à zéro (perte du jeu)
    private void onCountdownFinished() {
        showGameEndDialog("Temps écoulé ! Vous avez perdu.");
    }

    // Affiche un dialogue de fin de jeu (perte)
    private void showGameEndDialog(String message) {
        // Crée et montre le pop-up de fin de jeu
        endDialog = new Dialog(this);
        endDialog.setContentView(R.layout.dialog_game_over);
        endDialog.setCancelable(false);

        TextView messageTextView = endDialog.findViewById(R.id.message);
        messageTextView.setText(message);

        Button buttonRestart = endDialog.findViewById(R.id.btn_restart);
        Button buttonQuit = endDialog.findViewById(R.id.btn_quit);

        buttonRestart.setOnClickListener(v -> {
            shuffleTiles();
            remainingSeconds = 100;
            timerTextView.setText(String.valueOf(remainingSeconds));
            endDialog.dismiss();
            startCountdownTimer();
        });

        buttonQuit.setOnClickListener(v -> finish());

        // Ajuster la taille de la fenêtre du dialogue
        Window window = endDialog.getWindow();
        if (window != null) {
            window.setLayout(1500, 500);  // Modifier la largeur et la hauteur selon vos besoins
        }

        endDialog.show(); // Afficher le pop-up de fin de jeu
    }

    // Action lorsque le joueur gagne
    private void onGameWon() {
        if (endDialog != null && endDialog.isShowing()) {
            return; // Empêcher l'ouverture de plusieurs dialogues
        }

        stopCountdownTimer();
        showGameEndDialog("Félicitations ! Vous avez gagné.");
    }

    // Récupère le Bitmap d'une tuile donnée (i, j)
    private Bitmap getTileBitmap(int row, int col) {
        int tileWidth = originalBitmap.getWidth() / 3;
        int tileHeight = originalBitmap.getHeight() / 3;

        return Bitmap.createBitmap(originalBitmap, col * tileWidth, row * tileHeight, tileWidth, tileHeight);
    }

    // Vérifie si le jeu est gagné
    private boolean isGameWon() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tiles[i][j].getDrawable() == null) {
                    continue;  // Ignorer la case vide
                }

                Drawable tileDrawable = tiles[i][j].getDrawable();
                Drawable solutionDrawable = solutionTiles[i][j].getDrawable();

                if (tileDrawable != null && solutionDrawable != null
                        && tileDrawable instanceof BitmapDrawable
                        && solutionDrawable instanceof BitmapDrawable) {

                    Bitmap tileBitmap = ((BitmapDrawable) tileDrawable).getBitmap();
                    Bitmap solutionBitmap = ((BitmapDrawable) solutionDrawable).getBitmap();

                    // Vérification que les tuiles sont aux bonnes positions
                    if (!tileBitmap.sameAs(solutionBitmap)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true; // Si toutes les tuiles sont à la bonne place
    }
}
