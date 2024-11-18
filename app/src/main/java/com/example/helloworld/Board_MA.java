package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Board_MA extends AppCompatActivity {

    private static final int MINI_GAME_REQUEST_CODE = 1;
    private Board_BoardView boardBoardView; // Déclaration de la vue BoardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();  // Masquer la barre de navigation
        }

        setContentView(R.layout.board);
        boardBoardView = findViewById(R.id.boardView);

        // Initialiser le bouton de lancer de dés
        Button dice = findViewById(R.id.dice);
        dice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardBoardView.startDiceRoll(); // Déclencher le lancer de dés
                startMiniGame(); // Vérifier et démarrer le mini-jeu si nécessaire
            }
        });
    }

    // Méthode appelée pour démarrer le mini-jeu
    private void startMiniGame() {
        // Vérifie l'action de la case sur laquelle le joueur est situé
        if (getPlayerCurrentCaseActionFromBoardView() == 1) {
            // Si l'action est de type 1 (par exemple, démarrer un mini-jeu)
            Intent intent = new Intent(Board_MA.this, Labyrinthe_MA.class);
            intent.putExtra("game_mode", "board");
            intent.putExtra("selection_key", "Bleu");
            Log.d("Board_MA", "Starting Labyrinthe_MA");
            startActivityForResult(intent, MINI_GAME_REQUEST_CODE); // Lance le mini-jeu
        }
    }

    // Gestion du retour du mini-jeu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("score");
                Log.d("Board_MA", "Score received from MiniGame: " + result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    // Méthode pour obtenir la case sur laquelle le joueur se trouve (exemple hypothétique)
    private int getPlayerCurrentCaseActionFromBoardView() {
        // Exemple d'obtention de la case actuelle, il faut ajuster en fonction de ta logique de jeu
        return boardBoardView.getCurrentCaseAction(); // Méthode fictive pour obtenir la case actuelle
    }
}
