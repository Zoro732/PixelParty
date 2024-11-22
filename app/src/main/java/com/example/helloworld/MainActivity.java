package com.example.helloworld;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView tvScore;
    private TextView tvTime;
    private TextView tvCountdown;
    private int score = 0;
    private int speed = 1500;
    private int taupesActives = 1;
    private final int MAX_TAUPES = 3;
    private Handler handler = new Handler();
    private Random random = new Random();
    private ImageButton[] moles;
    private int screenWidth, screenHeight;
    private int gameDuration = 30;
    private long startTime;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideSystemUI();

        // Initialiser le TextView pour le compte à rebours
        tvCountdown = findViewById(R.id.tv_countdown);

        // Récupération des dimensions de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        tvScore = findViewById(R.id.tv_score);
        tvTime = findViewById(R.id.tv_time);
        moles = new ImageButton[] {
                findViewById(R.id.mole1),
                findViewById(R.id.mole2),
                findViewById(R.id.mole3)
        };

        ImageView settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> showPausePopup());

        for (ImageButton mole : moles) {
            mole.setOnClickListener(v -> {
                if (!isPaused) {
                    score++;
                    tvScore.setText("Score: " + score);
                    v.setVisibility(View.INVISIBLE);
                    increaseSpeed();
                }
            });
        }

        startCountdown();
    }

    // Méthode pour démarrer le compte à rebours
    private void startCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> tvCountdown.setText("3"), 1000);
        handler.postDelayed(() -> tvCountdown.setText("2"), 2000);
        handler.postDelayed(() -> tvCountdown.setText("1"), 3000);
        handler.postDelayed(() -> tvCountdown.setText("30sec, GO!"), 4000);

        // Démarrer le jeu après "GO!"
        handler.postDelayed(this::startGame, 5000);
    }

    private void startGame() {
        tvCountdown.setVisibility(View.INVISIBLE);  // Masquer le compte à rebours
        startTime = System.currentTimeMillis();
        handler.postDelayed(gameRunnable, 1000);
    }

    private final Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPaused && !isGameOver) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = gameDuration * 1000 - elapsedTime;
                int secondsRemaining = (int) (remainingTime / 1000);
                tvTime.setText(secondsRemaining + "s");

                if (remainingTime <= 0) {

                    isGameOver = true; // Marquer la fin du jeu
                    showGameOverPopup(); // Afficher le popup de fin de jeu
                    return;
                }

                showMoleOneByOne();
            }

            if (!isGameOver) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void showMoleOneByOne() {
        if (isPaused || isGameOver) {
            return; // Empêche l'apparition de taupes pendant la pause ou après la fin du jeu
        }

        int index = random.nextInt(moles.length);

        int randomX = random.nextInt(screenWidth - moles[index].getWidth());
        int randomY = random.nextInt(screenHeight - moles[index].getHeight() - 50); // 50 pour un léger espace de marge

        moles[index].setX(randomX);
        moles[index].setY(randomY);
        moles[index].setVisibility(View.VISIBLE);

        handler.postDelayed(() -> {
            moles[index].setVisibility(View.INVISIBLE);
        }, speed);
    }

    private void increaseSpeed() {
        if (speed > 500) {
            speed -= 75;
        } else if (speed > 300) {
            speed -= 25;
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void showPausePopup() {
        isPaused = true;

        Dialog pauseDialog = new Dialog(this);
        pauseDialog.setContentView(R.layout.dialog_pause);
        pauseDialog.setCancelable(false);

        // Accéder aux éléments de la vue du dialogue
        Button btnResume = pauseDialog.findViewById(R.id.btn_resume);
        Button btnRestart = pauseDialog.findViewById(R.id.btn_restart);
        Button btnQuit = pauseDialog.findViewById(R.id.btn_quit);

        btnResume.setOnClickListener(v -> {
            isPaused = false; // Reprendre le jeu
            pauseDialog.dismiss();
        });

        btnRestart.setOnClickListener(v -> {
            isPaused = false;
            pauseDialog.dismiss();
            restartGame();
        });

        btnQuit.setOnClickListener(v -> {
            pauseDialog.dismiss();
            finish();
        });

        // Ajuster la taille de la fenêtre du dialogue
        Window window = pauseDialog.getWindow();
        if (window != null) {
            window.setLayout(1500, 650);  // Largeur 1500px, hauteur 500px
        }

        pauseDialog.show();
    }

    private void showGameOverPopup() {
        // Créer et afficher le popup de fin de jeu
        Dialog gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.dialog_game_over);
        gameOverDialog.setCancelable(false);

        // Accéder aux éléments de la vue du dialogue
        TextView tvFinalScore = gameOverDialog.findViewById(R.id.tv_final_score);
        Button btnRestart = gameOverDialog.findViewById(R.id.btn_restart);
        Button btnQuit = gameOverDialog.findViewById(R.id.btn_quit);

        tvFinalScore.setText("Score final: " + score);

        btnRestart.setOnClickListener(v -> {
            gameOverDialog.dismiss();
            restartGame();
        });

        btnQuit.setOnClickListener(v -> {
            gameOverDialog.dismiss();
            finish();
        });

        // Ajuster la taille de la fenêtre du dialogue
        Window window = gameOverDialog.getWindow();
        if (window != null) {
            window.setLayout(1500, 650);  // Largeur 1500px, hauteur 500px
        }

        gameOverDialog.show();
    }

    private void restartGame() {
        // Réinitialiser le score et les variables de jeu
        score = 0;
        speed = 1500;
        taupesActives = 1;
        tvScore.setText("Score: " + score);

        // Masquer les taupes visibles
        for (ImageButton mole : moles) {
            mole.setVisibility(View.INVISIBLE);
        }

        // Annuler toutes les actions en cours liées aux taupes
        handler.removeCallbacks(gameRunnable);  // Annuler le Runnable d'apparition des taupes

        // Réinitialiser le compte à rebours
        tvCountdown.setVisibility(View.VISIBLE);
        tvCountdown.setText("3");

        // Réinitialiser le temps de jeu
        startTime = System.currentTimeMillis();

        // Réinitialiser le compte à rebours
        handler.postDelayed(() -> {
            tvCountdown.setText("2");
        }, 1000);

        handler.postDelayed(() -> {
            tvCountdown.setText("1");
        }, 2000);

        handler.postDelayed(() -> {
            tvCountdown.setText("GO!");
        }, 3000);

        // Démarrer le jeu après "GO!"
        handler.postDelayed(this::startGame, 4000);

        // Marquer que le jeu est réinitialisé
        isGameOver = false;
        isPaused = false;

        // Démarrer le jeu après "GO!"
        handler.postDelayed(gameRunnable, 4000);
    }

}
