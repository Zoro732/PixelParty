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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Mole_MA extends AppCompatActivity {
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
    private MediaPlayer mediaPlayer;
    private MediaPlayer winSound;

    private boolean[] moleTouched;

    private final float INITIAL_PROBABILITY = 0.1f; // Probabilité initiale d'apparition d'une taupe (10%)
    private final float PROBABILITY_INCREMENT = 0.05f; // Augmentation de la probabilité à chaque intervalle (5%)

    private float currentProbability = INITIAL_PROBABILITY; // Probabilité actuelle d'apparition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mole);

        hideSystemUI();

        // Initialisation des vues
        tvCountdown = findViewById(R.id.tv_countdown);
        tvScore = findViewById(R.id.tv_score);
        tvTime = findViewById(R.id.tv_time);

        moles = new ImageButton[]{
                findViewById(R.id.mole1),
                findViewById(R.id.mole2),
                findViewById(R.id.mole3)
        };

        moleTouched = new boolean[moles.length];

        // Initialisation du MediaPlayer pour les sons
        mediaPlayer = MediaPlayer.create(this, R.raw.hurtmob);
        winSound = MediaPlayer.create(this, R.raw.win);

        // Récupération des dimensions de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Bouton paramètres
        ImageView settingsButton = findViewById(R.id.settings);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialiser les clics sur les taupes
        initializeMoleClickHandlers();

        // Démarrer le compte à rebours
        startCountdown();
    }

    private void initializeMoleClickHandlers() {
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
    }

    private void startCountdown() {
        tvCountdown.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> tvCountdown.setText("Début dans"), 1000);
        handler.postDelayed(() -> tvCountdown.setText("3"), 2000);
        handler.postDelayed(() -> tvCountdown.setText("2"), 3000);
        handler.postDelayed(() -> tvCountdown.setText("1"), 4000);
        handler.postDelayed(() -> tvCountdown.setText("GO!"), 5000);
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
                    return;
                }

                // Augmenter dynamiquement le nombre de taupes actives
                increaseActiveMoles(elapsedTime);

                // Afficher les taupes
                showMoles();
            }

            if (!isGameOver) {
                handler.postDelayed(this, speed);
            }
        }
    };

    private void increaseActiveMoles(long elapsedTime) {
        // Augmenter le nombre de taupes actives en fonction du temps écoulé
        int newActiveMoles = 1 + (int) (elapsedTime / 10000); // Une taupe supplémentaire toutes les 10 secondes
        taupesActives = Math.min(newActiveMoles, MAX_TAUPES); // Limite au maximum défini
    }

    private void showMoles() {
        if (isPaused || isGameOver) return;

        // Réinitialiser toutes les taupes
        for (int i = 0; i < moles.length; i++) {
            moles[i].setVisibility(View.INVISIBLE);
            moleTouched[i] = false;
        }

        // Activer un ensemble unique de taupes avec une probabilité croissante
        Set<Integer> activeMoles = new HashSet<>();
        for (int i = 0; i < moles.length; i++) {
            // La chance d'apparition d'une taupe augmente avec le temps
            if (random.nextFloat() < currentProbability) {
                activeMoles.add(i); // Si la probabilité est atteinte, la taupe est activée
            }
        }

        // Afficher les taupes actives
        for (int index : activeMoles) {
            int moleWidth = moles[index].getWidth();
            int moleHeight = moles[index].getHeight();

            if (moleWidth == 0 || moleHeight == 0) {
                moleWidth = 150;
                moleHeight = 150; // Valeurs par défaut si les dimensions ne sont pas encore calculées
            }

            int maxX = screenWidth - moleWidth - 100;
            int maxY = screenHeight - moleHeight - 200;
            int randomX = random.nextInt(Math.max(1, maxX));
            int randomY = random.nextInt(Math.max(1, maxY));

            moles[index].setX(randomX);
            moles[index].setY(randomY);
            moles[index].setBackgroundResource(R.drawable.mole);
            moles[index].setVisibility(View.VISIBLE);

            handler.postDelayed(() -> {
                moles[index].setVisibility(View.INVISIBLE);
                moleTouched[index] = false;
            }, speed);
        }

        // Augmenter la probabilité d'apparition pour le prochain tour
        currentProbability = Math.min(currentProbability + PROBABILITY_INCREMENT, 1.0f); // La probabilité maximale est de 100%
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


    private void restartGame() {
        handler.removeCallbacksAndMessages(null);

        score = 0;
        speed = 1500;
        taupesActives = 1;
        isGameOver = false;
        isPaused = false;
        moleTouched = new boolean[moles.length];
        tvScore.setText("Score: 0");
        tvTime.setText(gameDuration + "s");

        for (ImageButton mole : moles) {
            mole.setVisibility(View.INVISIBLE);
            mole.setBackgroundResource(R.drawable.mole);
        }

        startGame();
    }

    private void startGame() {
        startTime = System.currentTimeMillis();
        handler.postDelayed(gameRunnable, 1000);
    }
}
