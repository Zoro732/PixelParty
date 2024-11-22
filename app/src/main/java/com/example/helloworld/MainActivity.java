package com.example.helloworld;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView tvScore;
    private int score = 0;
    private Handler handler = new Handler();
    private Random random = new Random();
    private ImageButton[] moles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore = findViewById(R.id.tv_score);
        moles = new ImageButton[]{
                findViewById(R.id.mole1),
                // Ajoutez d'autres taupes ici
        };

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
                int randomMole = random.nextInt(moles.length);
                moles[randomMole].setVisibility(View.VISIBLE);

                handler.postDelayed(() -> moles[randomMole].setVisibility(View.INVISIBLE), 800);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}
