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

    private int currentRound = 1; // Compteur pour le numéro de tour
    private TextView roundTextView; // TextView pour afficher le numéro du tour
    private int previousRound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();  // Masquer la barre de navigation
        }

        setContentView(R.layout.board);
        boardBoardView = findViewById(R.id.boardView);


        boardBoardView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Intent intent = getIntent();
                if (intent != null) {
                    String selection = intent.getStringExtra("selection_key");
                    Log.d("Board_MA", "Received selection: " + selection + "setting player sprite");
                    boardBoardView.setPlayerSpriteSelection(selection);
                    Log.d("Board_MA", "Received selection: " + selection + "player sprite set");
                }
                if (getPlayerCurrentCaseActionFromBoardView() != 0) {
                    playButton.setEnabled(true);
                    //dice.setEnabled(true);
                } else {
                    dice.setEnabled(true);
                }
            }
        });


        // Initialisation des items du layout
        scoreMessage = findViewById(R.id.scoreText);
        continueButton = findViewById(R.id.continueButton);
        playButton = findViewById(R.id.play);
        playButton.setEnabled(false);

        dice = findViewById(R.id.dice);

        // Initialisation du TextView pour le numéro de tour
        roundTextView = findViewById(R.id.round_number); // Assurez-vous que cette vue existe dans votre layout XML
        updateRoundDisplay();

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

                                playButton.setEnabled(false);
                                dice.setEnabled(false);

                            } else {
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

    private void updateRoundDisplay() {
        roundTextView.setText("Round : " + currentRound); // Met à jour le TextView
    }


    private void nextRound() {
        currentRound++;
        updateRoundDisplay();
        Log.d("Board_MA", "Next round started: Round " + currentRound);
    }



    // Méthode appelée pour démarrer le mini-jeu
    private void startMiniGame() {
        Log.d("Board_MA", "Starting startMiniGame");
        if (getPlayerCurrentCaseActionFromBoardView() == 1) {

            Intent intent = new Intent(Board_MA.this, Labyrinthe_MA.class);
            intent.putExtra("game_mode", "board");
            intent.putExtra("selection_key", "Bleu");
            Log.d("Board_MA", "Starting Labyrinthe_MA");
            startActivityForResult(intent, LABY_REQUEST_CODE);

        } else if (getPlayerCurrentCaseActionFromBoardView() == 2) {

            Intent intent = new Intent(Board_MA.this, RunGame_MA.class);
            intent.putExtra("game_mode", "board");
            Log.d("Board_MA", "Starting Rngame");
            startActivityForResult(intent, RUN_REQUEST_CODE);

        } else if (getPlayerCurrentCaseActionFromBoardView() == 3) {

            Intent intent = new Intent(Board_MA.this, Taquin_MA.class);
            intent.putExtra("game_mode", "board");
            Log.d("Board_MA", "Starting TAquin");
            startActivityForResult(intent, TAQUIN_REQUEST_CODE);
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
                        dice.setEnabled(true);
                    }
                });

            }
        }
        if (requestCode == TAQUIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("score");
                Log.d("Board_MA", "Score received from TAquin: " + result);
                if(result.equals("-1")) {
                    scoreMessage.setText("Taquin Failed");
                } else {
                    scoreMessage.setText("Taquin finished in " + result + "s");

                }
                scoreMessage.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(false);
                dice.setEnabled(false);

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scoreMessage.setVisibility(View.GONE);
                        continueButton.setVisibility(View.GONE);
                        dice.setEnabled(true);
                    }
                });

            } else {
                Log.d("Board_MA","result code invalid for taquin");
            }
        } else {
            Log.d("Board_MA","request code invalid for taquin");
        }
        nextRound();

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

