package com.example.helloworld;

import android.app.Dialog;
import android.content.Intent;
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

public class Taquin_MA extends AppCompatActivity {

    private ImageView[][] tiles = new ImageView[3][3];
    private ImageView[][] solutionTiles = new ImageView[3][3];

    private int emptyRow = 2, emptyCol = 2;
    private Bitmap originalBitmap;

    private Dialog pauseDialog;
    private Dialog endDialog;

    private TextView timerTextView;
    private int defaultTimerValue = 120;
    private int timerValue = defaultTimerValue;
    private Handler timerHandler = new Handler();
    private boolean isTimerRunning = false;

    private Intent intent = new Intent();
    private String game_mode = intent.getStringExtra("game_mode");

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

        // Choisir aléatoirement l'image à utiliser
        int[] images = {R.drawable.oiseaux, R.drawable.etoile, R.drawable.enfant_espace};
        int randomImageIndex = new Random().nextInt(images.length);
        originalBitmap = BitmapFactory.decodeResource(getResources(), images[randomImageIndex]);

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
                if (timerValue > 0) {
                    timerTextView.setText(String.valueOf(timerValue));
                    timerValue--;
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
            timerValue = 120;
            timerTextView.setText(String.valueOf(timerValue));
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
        // On doit tout d'abord réinitialiser l'état des tuiles
        emptyRow = 2;
        emptyCol = 2;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1), "id", getPackageName());
                ImageView tile = findViewById(resID);
                tileArray[i][j] = tile;

                // On ne met pas d'image sur la case vide (en bas à droite)
                if (i == 2 && j == 2) {
                    tile.setImageResource(0);  // La case vide doit être réinitialisée
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

    /// Gère le clic sur une tuile
    private void onTileClick(int row, int col) {
        // Vérifier si la tuile cliquée est adjacente à la tuile vide
        if (Math.abs(emptyRow - row) + Math.abs(emptyCol - col) == 1) {
            Bitmap tempBitmap = tiles[row][col].getDrawable() != null ? ((BitmapDrawable) tiles[row][col].getDrawable()).getBitmap() : null;

            // Déplacer la tuile dans la case vide
            tiles[emptyRow][emptyCol].setImageBitmap(tempBitmap);

            // Vider la tuile cliquée
            tiles[row][col].setImageResource(0);

            // Mettre à jour les indices de la case vide
            emptyRow = row;
            emptyCol = col;

            // Vérification après chaque mouvement
            if (isGameWon()) {
                stopCountdownTimer();
                onGameWon();
            }
        }
    }


    // Action lorsque le jeu est gagné
    private void onGameWon() {
        stopCountdownTimer();
        showGameEndDialog("Félicitations ! Vous avez gagné.");
        if (game_mode.equals("board"))finish();
    }

    // Mélange aléatoire des tuiles
    private void shuffleTiles() {
        // Réinitialiser la case vide avant le mélange
        tiles[emptyRow][emptyCol].setImageResource(0);  // Réinitialiser la case vide avant de mélanger

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

            // Vérifier que la nouvelle position est valide
            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                // Récupérer le bitmap de la tuile à déplacer
                Bitmap tempBitmap = tiles[newRow][newCol].getDrawable() != null ? ((BitmapDrawable) tiles[newRow][newCol].getDrawable()).getBitmap() : null;

                // Déplacer la tuile vers la case vide
                tiles[emptyRow][emptyCol].setImageBitmap(tempBitmap);

                // Mettre à jour la tuile déplacée avec l'image vide
                tiles[newRow][newCol].setImageResource(0);

                // Mettre à jour les indices de la case vide
                emptyRow = newRow;
                emptyCol = newCol;
            }
        }
    }



    // Action lorsque le chronomètre arrive à zéro (perte du jeu)
    private void onCountdownFinished() {
        showGameEndDialog("Temps écoulé ! Vous avez perdu.");
        timerValue = -1;
        finish();
    }

    // Affiche un dialogue de fin de jeu (perte ou victoire)
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
            // Choisir une nouvelle image aléatoire lorsque l'on rejoue
            int[] images = {R.drawable.oiseaux, R.drawable.etoile, R.drawable.enfant_espace};
            int randomImageIndex = new Random().nextInt(images.length);
            originalBitmap = BitmapFactory.decodeResource(getResources(), images[randomImageIndex]);

            // Réinitialiser les tuiles et la solution avec la nouvelle image
            initializeTiles((GridLayout) findViewById(R.id.gridLayout), tiles);
            initializeSolutionTiles((GridLayout) findViewById(R.id.solutionGrid));  // Réinitialise aussi la solution

            // Réinitialiser la case vide (en bas à droite)
            tiles[emptyRow][emptyCol].setImageResource(0);  // S'assurer que la case vide est vide

            // Mélanger les tuiles avec la nouvelle image
            shuffleTiles();  // Mélange les tuiles après réinitialisation

            // Redémarrer le chronomètre
            timerValue = 120;
            timerTextView.setText(String.valueOf(timerValue));

            // Fermer le dialogue et démarrer un nouveau jeu
            endDialog.dismiss();
            startCountdownTimer();
        });

        buttonQuit.setOnClickListener(v -> finish());

        // Ajuster la taille de la fenêtre du dialogue
        Window window = endDialog.getWindow();
        if (window != null) {
            window.setLayout(1500, 500);  // Modifier la largeur et la hauteur selon vos besoins
        }

        endDialog.show();
    }


    private boolean isGameWon() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Drawable solutionDrawable = solutionTiles[i][j].getDrawable();
                Drawable tileDrawable = tiles[i][j].getDrawable();

                // Vérifie si le Drawable est un BitmapDrawable
                if (solutionDrawable instanceof BitmapDrawable && tileDrawable instanceof BitmapDrawable) {
                    Bitmap solutionBitmap = ((BitmapDrawable) solutionDrawable).getBitmap();
                    Bitmap tileBitmap = ((BitmapDrawable) tileDrawable).getBitmap();

                    // Compare les Bitmaps
                    if (!solutionBitmap.sameAs(tileBitmap)) {
                        return false; // Les tuiles ne sont pas identiques
                    }
                } else if (solutionDrawable != null || tileDrawable != null) {
                    return false; // Si l'un des Drawables est null, ce n'est pas une solution valide
                }
            }
        }
        return true; // Toutes les tuiles sont en place
    }


    // Récupère le bitmap de la tuile en fonction de la position
    private Bitmap getTileBitmap(int row, int col) {
        int tileWidth = originalBitmap.getWidth() / 3;
        int tileHeight = originalBitmap.getHeight() / 3;

        return Bitmap.createBitmap(originalBitmap, col * tileWidth, row * tileHeight, tileWidth, tileHeight);
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        // Ajoutez des données si nécessaire
        resultIntent.putExtra("score", String.valueOf(defaultTimerValue - timerValue));
        setResult(RESULT_OK, resultIntent);
        super.finish(); // Terminez l'Activity
    }
}