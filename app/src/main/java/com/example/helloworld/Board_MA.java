package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Board_MA extends AppCompatActivity {

    private static final int LABY_REQUEST_CODE = 1;
    private static final int RUN_REQUEST_CODE = 2;
    private static final int TAQUIN_REQUEST_CODE = 3;
    private static final int SHOP_REQUEST_CODE = 4;
    private static final int MINI_BOSS_REQUEST_CODE = 5;

    private Board_BoardView boardBoardView; // Déclaration de la vue BoardView
    private TextView scoreMessage;
    private Button continueButton;
    private Button playButton;
    private Button dice;

    public int currentPlayerCaseNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();  // Masquer la barre de navigation
        }

        setContentView(R.layout.board);
        boardBoardView = findViewById(R.id.boardView);

        // Initialisation des items du layout
        scoreMessage = findViewById(R.id.scoreText);
        continueButton = findViewById(R.id.continueButton);
        playButton = findViewById(R.id.play);

        // Initialiser le bouton de lancer de dés
        dice = findViewById(R.id.dice);
        dice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dice.isEnabled()) {
                    dice.setEnabled(false);

                    playButton.setEnabled(false);
                    boardBoardView.startDiceRoll();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (!boardBoardView.isDiceRolling()) {
                                // Dice roll complete
                                // Keep dice button disabled
                                // Enable play button only if on a mini-game tile
                                playButton.setEnabled(true);
                                if (getPlayerCurrentCaseActionFromBoardView() == 0) {
                                    dice.setEnabled(true);
                                }


                            } else {
                                // If still rolling, schedule another check
                                handler.postDelayed(this, 100);
                            }
                        }
                    }, 100);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMiniGame();
            }
        });
    }

    // Méthode appelée pour démarrer le mini-jeu
    private void startMiniGame() {
        Log.d("Board_MA", "Starting startMiniGame");
        // Vérifie l'action de la case sur laquelle le joueur est situé
        if (getPlayerCurrentCaseActionFromBoardView() == 1) {
            Intent intent = new Intent(Board_MA.this, Labyrinthe_MA.class);
            intent.putExtra("game_mode", "board");
            intent.putExtra("selection_key", "Bleu");
            Log.d("Board_MA", "Starting Labyrinthe_MA");
            startActivityForResult(intent, LABY_REQUEST_CODE); // Lance le mini-jeu laby

        } else if (getPlayerCurrentCaseActionFromBoardView() == 2) {
            // Si l'action est de type 1 (par exemple, démarrer un mini-jeu)
            Intent intent = new Intent(Board_MA.this, RunGame_MA.class);
            intent.putExtra("game_mode", "board");
            Log.d("Board_MA", "Starting Rngame");
            startActivityForResult(intent, RUN_REQUEST_CODE); // Lance le mini-jeu

        } else if (getPlayerCurrentCaseActionFromBoardView() == 3) {
            // Si l'action est de type 1 (par exemple, démarrer un mini-jeu)
            Intent intent = new Intent(Board_MA.this, Taquin_MA.class);
            Log.d("Board_MA", "Starting TAquin");
            startActivityForResult(intent, TAQUIN_REQUEST_CODE); // Lance le mini-jeu
        }
    }

    // Gestion du retour du mini-jeu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LABY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("score");
                Log.d("Board_MA", "Score received from MiniGame: " + result);
                scoreMessage.setText("Labyrinthe Made in " + result + "s");
                scoreMessage.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(false);
                dice.setEnabled(false);

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scoreMessage.setVisibility(View.GONE);
                        continueButton.setVisibility(View.GONE);
                        playButton.setEnabled(true);
                        dice.setEnabled(true);
                    }
                });
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Code si aucun résultat
            }
        }
        if (requestCode == RUN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("score");
                Log.d("Board_MA", "Score received from RunGame: " + result);
                scoreMessage.setText("Rungame finished with " + result + " coins");
                scoreMessage.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(false);
                dice.setEnabled(false);

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scoreMessage.setVisibility(View.GONE);
                        continueButton.setVisibility(View.GONE);
                        playButton.setEnabled(true);
                        dice.setEnabled(true);
                    }
                });

            }
        }
        if (requestCode == TAQUIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("score");
                Log.d("Board_MA", "Score received from TAquin: " + result);
                scoreMessage.setText("Taquin finished in " + result + "s");
                scoreMessage.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(false);
                dice.setEnabled(false);

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scoreMessage.setVisibility(View.GONE);
                        continueButton.setVisibility(View.GONE);
                        playButton.setEnabled(true);
                        dice.setEnabled(true);
                    }
                });

            }
        }
    }

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
        return boardBoardView.getCurrentCaseAction(); // Méthode fictive pour obtenir la case actuelle
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentPlayerCaseNumber = boardBoardView.getPlayerCaseNumber();
        outState.putInt("playerCaseNumber", currentPlayerCaseNumber); // Sauvegarde
        Log.d("Board_MA", "Saving player case number = " + currentPlayerCaseNumber);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            boardBoardView.setPlayerMovingAnimationToTargetCase(false);
            currentPlayerCaseNumber = savedInstanceState.getInt("playerCaseNumber");
            boardBoardView.setPlayerCaseNumber(currentPlayerCaseNumber);
            Log.d("Board_MA", "Retrieving player case number = " + currentPlayerCaseNumber);
        }
    }

}

