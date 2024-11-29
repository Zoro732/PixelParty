package com.example.helloworld;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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
    private TextView tvScore, tvTime, tvCountdown;
    private int score = 0, speed = 1500, taupesActives = 1, gameDuration = 30;
    private final int MAX_TAUPES = 3;
    private long startTime;
    private boolean isPaused = false, isGameOver = false;

    private Handler handler = new Handler();
    private Random random = new Random();
    private ImageButton[] moles;
    private int screenWidth, screenHeight;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer; // MediaPlayer pour le son
    private MediaPlayer startGameSound; // MediaPlayer pour le son de démarrage
    private MediaPlayer winSound; // MediaPlayer pour le son de victoire

    private boolean[] moleTouched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideSystemUI();  // Appel à la méthode pour masquer les éléments de l'interface système.

        // Initialisation des vues
        tvCountdown = findViewById(R.id.tv_countdown);
        tvScore = findViewById(R.id.tv_score);
        tvTime = findViewById(R.id.tv_time);

        moles = new ImageButton[]{
                findViewById(R.id.mole1)
        };

        moleTouched = new boolean[moles.length];

        // Initialisation du MediaPlayer pour les sons
        mediaPlayer = MediaPlayer.create(this, R.raw.hurtmob);
        startGameSound = MediaPlayer.create(this, R.raw.startgame);
        winSound = MediaPlayer.create(this, R.raw.win); // Charger le son de victoire

        // Récupération des dimensions de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Bouton paramètres
        ImageView settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> showPausePopup());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        for (int i = 0; i < moles.length; i++) {
            int finalI = i;
            moles[i].setOnClickListener(v -> {
                if (!isPaused && !isGameOver && !moleTouched[finalI]) {
                    score++;
                    tvScore.setText("Score: " + score);

                    moleTouched[finalI] = true;
                    moles[finalI].setBackgroundResource(R.drawable.mole_hit);

                    vibrator.vibrate(100);

                    // Jouer le son
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }

                    // Augmenter la vitesse du jeu
                    increaseSpeed();
                }
            });
        }

        startCountdown();
    }

    private void startCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> tvCountdown.setText("Début dans"), 1000);
        handler.postDelayed(() -> tvCountdown.setText("3"), 2000);
        handler.postDelayed(() -> tvCountdown.setText("2"), 3000);
        handler.postDelayed(() -> tvCountdown.setText("1"), 4000);
        handler.postDelayed(() -> tvCountdown.setText("GO!"), 5000);
        tvCountdown.setText("");
        handler.postDelayed(() -> {
            tvCountdown.setVisibility(View.INVISIBLE);
            startGame();
        }, 6000);
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
                    isGameOver = true;
                    showGameOverPopup();
                    return;
                }
                increaseActiveMoles(elapsedTime);
                showMoleOneByOne();
            }

            if (!isGameOver) {
                handler.postDelayed(this, 1700);
            }
        }
    };

    private void increaseActiveMoles(long elapsedTime) {
        int newActiveMoles = 1 + (int) (elapsedTime / 10000);
        taupesActives = Math.min(newActiveMoles, MAX_TAUPES);
    }

    private void showMoleOneByOne() {
        findViewById(R.id.mole1).setBackgroundResource(R.drawable.mole);
        if (isPaused || isGameOver) return;

        int index = random.nextInt(moles.length);

        if (moleTouched[index]) {
            showMoleOneByOne();
            return;
        }

        int moleWidth = moles[index].getWidth();
        int moleHeight = moles[index].getHeight();

        if (moleWidth == 0 || moleHeight == 0) {
            moleWidth = 150;
            moleHeight = 150;
        }

        int maxX = screenWidth - moleWidth - 100;
        int maxY = screenHeight - moleHeight - 200;
        int randomX = random.nextInt(Math.max(1, maxX));
        int randomY = random.nextInt(Math.max(1, maxY));

        moles[index].setX(randomX);
        moles[index].setY(randomY);
        moles[index].setVisibility(View.VISIBLE);

        handler.postDelayed(() -> {
            moles[index].setVisibility(View.INVISIBLE);
            moleTouched[index] = false;
        }, speed);
    }

    private void increaseSpeed() {
        if (speed > 500) speed -= 75;
        else if (speed > 300) speed -= 25;
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
        if (hasFocus) hideSystemUI();
    }

    // Méthodes pour gérer la pause et la fin de jeu...
    private void showPausePopup() {
        isPaused = true;

        Dialog pauseDialog = new Dialog(this);
        pauseDialog.setContentView(R.layout.dialog_pause);
        pauseDialog.setCancelable(false);

        Button btnResume = pauseDialog.findViewById(R.id.btn_resume);
        Button btnRestart = pauseDialog.findViewById(R.id.btn_restart);
        Button btnQuit = pauseDialog.findViewById(R.id.btn_quit);

        btnResume.setOnClickListener(v -> {
            isPaused = false;
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

        Window window = pauseDialog.getWindow();
        if (window != null) {
            window.setLayout(1500, 650);
        }

        pauseDialog.show();
    }

    private void showGameOverPopup() {
        // Jouer le son de victoire lorsqu'on atteint la fin du jeu
        if (winSound != null) {
            winSound.start(); // Joue le son de victoire
        }

        Dialog gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.dialog_game_over);
        gameOverDialog.setCancelable(false);

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

        Window window = gameOverDialog.getWindow();
        if (window != null) {
            window.setLayout(1500, 650);
        }

        gameOverDialog.show();
    }

    private void restartGame() {
        handler.removeCallbacksAndMessages(null); // Arrêter toutes les tâches en cours

        // Réinitialiser les variables
        score = 0;
        speed = 1500;
        taupesActives = 1;
        isGameOver = false;
        isPaused = false;
        moleTouched = new boolean[moles.length]; // Réinitialiser les taupes touchées
        tvScore.setText("Score: 0");
        tvTime.setText(gameDuration + "s");

        // Réinitialiser les taupes
        for (ImageButton mole : moles) {
            mole.setVisibility(View.INVISIBLE);
            mole.setBackgroundResource(R.drawable.mole);  // Remettre l'état initial des taupes
        }

        // Démarrer le jeu
        startGame();
    }

    private void startGame() {
        // Réinitialiser le temps et démarrer le jeu
        startTime = System.currentTimeMillis();
        handler.postDelayed(gameRunnable, 1000);  // Redémarrer la boucle de jeu
    }

}
