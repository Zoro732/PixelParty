package com.example.helloworld;

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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Board_MA extends AppCompatActivity {

    // Constants for request codes
    private static final int LABY_REQUEST_CODE = 1;
    private static final int RUN_REQUEST_CODE = 2;
    private static final int TAQUIN_REQUEST_CODE = 3;

    // UI Elements
    private Board_BoardView boardBoardView;
    private TextView scoreMessage;
    private TextView textView_DicePlusOne;
    private TextView starsNumber;
    private Button continueButton;
    private Button playButton;
    private Button diceButton;
    private Button plusOneToDiceButton;

    // Game state variables
    private int currentPlayerCaseNumber;
    private boolean doPlayerUsePlusOneItem = false;

    // Handler for player movement
    private final Handler playerMovementHandler = new Handler(Looper.getMainLooper());

    // Sprite selection
    private String spriteSelection;

    // New round
    private boolean newRound = false;

    // Store stars number
    private int starsNumberValue = 0;

    // Check if player win previous game to increment star number
    private boolean doPlayerWinPreviousGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the navigation bar if the Android version is compatible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        setContentView(R.layout.board);
        // Initialize UI elements
        initializeUI();
        // Handle data passed from the previous activity
        handleIntentData();
        // Setup listeners for UI components
        setupListeners();
        // Start monitoring player movement
        playerMovementHandler.post(playerMovementRunnable);
    }

    /**
     * Initialize UI components and link them to layout elements.
     */
    private void initializeUI() {
        boardBoardView = findViewById(R.id.boardView);
        scoreMessage = findViewById(R.id.scoreText);
        textView_DicePlusOne = findViewById(R.id.textView_DicePlusOne);
        starsNumber = findViewById(R.id.starsNumber);
        continueButton = findViewById(R.id.continueButton);
        playButton = findViewById(R.id.play);
        diceButton = findViewById(R.id.dice);
        plusOneToDiceButton = findViewById(R.id.plusOneButton);

        textView_DicePlusOne.bringToFront();

        playButton.setEnabled(false); // Disabled by default
    }

    /**
     * Handle the intent data passed from the previous activity.
     */
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            spriteSelection = intent.getStringExtra("selection_key");
            Log.d("Board_MA", "Received spriteSelection: " + spriteSelection + ", setting player sprite.");
            boardBoardView.setPlayerSpriteSelection(spriteSelection);
        }
    }

    /**
     * Set up listeners for UI components like buttons and inventory management.
     */
    private void setupListeners() {
        diceButton.setOnClickListener(v -> {
            if (diceButton.isEnabled()) {
                boardBoardView.startDiceRoll();
                plusOneToDiceButton.setVisibility(View.VISIBLE);
                if (newRound && !boardBoardView.getIsPlayerMoving()){
                    newRound = false;
                }
            }
        });

        playButton.setOnClickListener(v -> startMiniGame());

        plusOneToDiceButton.setOnClickListener(v -> {
            Toast.makeText(Board_MA.this, "Item Used: Dice +1", Toast.LENGTH_SHORT).show();
            plusOneToDiceButton.setEnabled(false);
            doPlayerUsePlusOneItem = true;
            boardBoardView.setItemAction(1);
        });

        setupInventoryManagement();
    }

    /**
     * Setup inventory management animations and button actions.
     */
    private void setupInventoryManagement() {
        Button inventoryButton = findViewById(R.id.inventory);
        View inventoryWindow = findViewById(R.id.inventoryWindow);
        Button closeInventoryButton = findViewById(R.id.closeInventoryButton);

        inventoryButton.setOnClickListener(v -> {
            inventoryWindow.setVisibility(View.VISIBLE);
            inventoryWindow.animate()
                    .translationX(10)
                    .setDuration(200)
                    .start();
        });

        closeInventoryButton.setOnClickListener(v -> {
            inventoryWindow.animate()
                    .translationX(0)
                    .setDuration(200)
                    .withEndAction(() -> inventoryWindow.setVisibility(View.GONE))
                    .start();
        });
    }

    /**
     * Runnable to manage player movement and UI updates.
     */
    private final Runnable playerMovementRunnable = new Runnable() {
        @Override
        public void run() {
            if (!boardBoardView.getIsPlayerMoving()) {
                handlePlayerIdleState();
            } else {
                diceButton.setEnabled(false);
            }

            if (boardBoardView.isDiceRolling()) {
                handleDiceRollingState();
            }

            playerMovementHandler.postDelayed(this, 100);
        }
    };

    /**
     * Handle player idle state and enable/disable buttons accordingly.
     */
    private void handlePlayerIdleState() {
        if (!newRound) {
            if (getPlayerCurrentCaseActionFromBoardView() == 0) {
                diceButton.setEnabled(true);
                playButton.setEnabled(false);
            } else {
                diceButton.setEnabled(false);
                playButton.setEnabled(true);
            }

            boardBoardView.setPlayerFinishedMoving(false);
        } else {
            diceButton.setEnabled(true);
            playButton.setEnabled(false);
        }

    }

    /**
     * Handle dice rolling state.
     */
    private void handleDiceRollingState() {
        if (doPlayerUsePlusOneItem) {
            textView_DicePlusOne.setVisibility(View.VISIBLE);
            Log.d("Board_MA", "Dice is rolling with +1");
        }
        playButton.setEnabled(false);
        Log.d("Board_MA", "Dice is rolling");
    }

    /**
     * Start a mini-game based on the player's current case action.
     */
    private void startMiniGame() {
        Log.d("Board_MA", "Starting mini-game.");
        Intent intent = null;
        int requestCode = -1;

        switch (getPlayerCurrentCaseActionFromBoardView()) {
            case 1:
                intent = new Intent(this, Labyrinthe_MA.class);
                requestCode = LABY_REQUEST_CODE;
                break;
            case 2:
                intent = new Intent(this, RunGame_MA.class);
                requestCode = RUN_REQUEST_CODE;
                break;
            case 3:
                intent = new Intent(this, Taquin_MA.class);
                requestCode = TAQUIN_REQUEST_CODE;
                break;
        }

        if (intent != null) {
            intent.putExtra("game_mode", "board");
            intent.putExtra("selection_key", spriteSelection);
            Log.d("Board_MA", "Starting game with request code: " + requestCode);
            startActivityForResult(intent, requestCode);
        }
    }

    /**
     * Handle results from mini-games.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String result = data.getStringExtra("score");
            handleMiniGameResult(requestCode, result);
            Log.d("Board_MA", "Result Intent from Labyrinthe_MA: " + data.toString());
            Log.d("Board_MA", "Received result from mini-game: " + result);
        }
    }

    private void handleMiniGameResult(int requestCode, String result) {
        switch (requestCode) {
            case LABY_REQUEST_CODE:
                scoreMessage.setText(result.equals("quit") ? "Labyrinthe Failed" : "Labyrinthe finished in " + result + "s");
                break;
            case RUN_REQUEST_CODE:
                scoreMessage.setText("RunGame finished with " + result + " coins");
                break;
            case TAQUIN_REQUEST_CODE:
                scoreMessage.setText(result.equals("quit") ? "Taquin Failed" : "Taquin finished in " + result + "s");
                break;
        }
        Log.d("Board_MA", "Received result from mini-game: " + result);
        if (!result.equals("quit")) {
            Log.d("Board_MA", "Incrementing stars number");
            doPlayerWinPreviousGame = true;
        }

        scoreMessage.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        playButton.setEnabled(false);
        diceButton.setEnabled(false);

        continueButton.setOnClickListener(v -> {
            newRound = true;
            scoreMessage.setVisibility(View.GONE);
            continueButton.setVisibility(View.GONE);
            diceButton.setEnabled(true);
            doPlayerUsePlusOneItem = false;
            textView_DicePlusOne.setVisibility(View.GONE);

            if (doPlayerWinPreviousGame) {
                starsNumberValue++;
                starsNumber.setText(String.valueOf(starsNumberValue));
                doPlayerWinPreviousGame = false;
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private int getPlayerCurrentCaseActionFromBoardView() {
        return boardBoardView.getCurrentCaseAction();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentPlayerCaseNumber = boardBoardView.getPlayerCaseNumber();
        outState.putInt("playerCaseNumber", currentPlayerCaseNumber);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (boardBoardView != null) {
            boardBoardView.setPlayerMovingAnimationToTargetCase(false);
            currentPlayerCaseNumber = savedInstanceState.getInt("playerCaseNumber");
            boardBoardView.setPlayerCaseNumber(currentPlayerCaseNumber);
        } else {
            Log.d("Board_MA", "boardBoardView is null");
        }

        Log.d("Board_MA", "End of onrestore");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerMovementHandler.removeCallbacks(playerMovementRunnable);
    }

    @Override
    public void onBackPressed() {
        onPause();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Quitter le jeu")
                .setMessage("Êtes-vous sûr de vouloir quitter ? Votre progression sera perdue.")
                .setPositiveButton("Oui", (dialog, which) -> {
                    Log.d("Board_MA", "User chose to quit the game.");
                    finish(); // Appelle finish() pour gérer la fermeture
                })
                .setNegativeButton("Non", (dialog, which) -> {
                    dialog.dismiss(); // Ferme la boîte de dialogue sans quitter
                })
                .setCancelable(false) // Empêche la fermeture de la boîte de dialogue en appuyant à l'extérieur
                .show();
    }
}
