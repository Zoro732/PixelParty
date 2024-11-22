package com.example.helloworld;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView tvScore;
    private int score = 0;
    private int speed = 1000; // Temps initial entre les apparitions (en millisecondes)
    private int taupesActives = 1; // Nombre initial de taupes actives
    private final int MAX_TAUPES = 3; // Nombre total de taupes
    private Handler handler = new Handler();
    private Random random = new Random();
    private ImageButton[] moles;
    private int screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mode plein écran
        hideSystemUI();

        // Récupération des dimensions de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        tvScore = findViewById(R.id.tv_score);
        moles = new ImageButton[]{
                findViewById(R.id.mole1),
                findViewById(R.id.mole2),
                findViewById(R.id.mole3)
        };

        // Configuration des taupes
        for (ImageButton mole : moles) {
            mole.setOnClickListener(v -> {
                score++;
                tvScore.setText("Score: " + score);
                v.setVisibility(View.INVISIBLE);
            });
        }

        startGame();
    }

    private void startGame() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Gérer le nombre de taupes actives
                for (int i = 0; i < taupesActives; i++) {
                    int index = random.nextInt(moles.length);

                    // Positionner les taupes de manière aléatoire
                    int randomX = random.nextInt(screenWidth - moles[index].getWidth());
                    int randomY = random.nextInt(screenHeight - moles[index].getHeight());

                    moles[index].setX(randomX);
                    moles[index].setY(randomY);
                    moles[index].setVisibility(View.VISIBLE);
                }

                // Cache les taupes après un certain temps
                handler.postDelayed(() -> {
                    for (ImageButton mole : moles) {
                        mole.setVisibility(View.INVISIBLE);
                    }
                }, speed - 200);

                // Augmente la difficulté
                if (speed > 300) {
                    speed -= 50; // Réduit le délai entre les cycles
                }
                if (taupesActives < MAX_TAUPES && score % 10 == 0) {
                    taupesActives++; // Augmente le nombre de taupes actives tous les 10 points
                }

                // Relance le cycle
                handler.postDelayed(this, speed);
            }
        }, speed);
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
}
