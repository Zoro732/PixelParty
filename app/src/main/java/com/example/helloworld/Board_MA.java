package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Board_MA extends AppCompatActivity {

    private Board_BoardView boardBoardView; // Déclaration de la vue BoardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();  // Masquer la barre de navigation
        }

        setContentView(R.layout.board);

        boardBoardView = findViewById(R.id.boardView);  // 'this' refers to the MainActivity context

        Button dice = findViewById(R.id.dice);
        dice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exemple de déplacement du joueur avec dés
                boardBoardView.startDiceRoll();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
