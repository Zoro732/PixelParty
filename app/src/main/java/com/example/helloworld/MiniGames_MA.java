package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MiniGames_MA extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_minijeux);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(MiniGames_MA.this, MainActivity.class);
            startActivity(intent);
        });

        Button laby = findViewById(R.id.laby);
        laby.setOnClickListener(v -> {
            Intent intent = new Intent(MiniGames_MA.this, SpriteActivity.class);
            intent.putExtra("game_mode", "minigames"); // Envoi d'une autre valeur
            startActivity(intent);
        });

        Button run = findViewById(R.id.run);
        run.setOnClickListener(v -> {
            Intent intent = new Intent(MiniGames_MA.this, RunGame_MA.class);
            startActivity(intent);
        });

        Button taquin = findViewById(R.id.taquin);
        taquin.setOnClickListener(v -> {
            Intent intent = new Intent(MiniGames_MA.this, Taquin_MA.class);
            startActivity(intent);
        });

        hideNavigationBar();
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
