package com.example.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Board_MA extends AppCompatActivity {

    // Constants for request codes
    private static final int LABY_REQUEST_CODE = 1;
    private static final int RUN_REQUEST_CODE = 2;
    private static final int TAQUIN_REQUEST_CODE = 3;

    // UI Elements
    private Board_BoardView boardBoardView;
    private TextView tvScore, tvPlusOneToDice, tvStarsNumber;
    private Button btnContinue, btnPlay, btnDice, btnPlusOne, btnInventory, btnCloseInventory;


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
        tvScore = findViewById(R.id.tvScore);
        tvPlusOneToDice = findViewById(R.id.tvPlusOneToDice);
        tvStarsNumber = findViewById(R.id.tvStarsNumber);
        btnContinue = findViewById(R.id.btnContinue);
        btnPlay = findViewById(R.id.btnPlay);
        btnDice = findViewById(R.id.btnDice);
        btnPlusOne = findViewById(R.id.btnPlusOne);
        btnInventory = findViewById(R.id.btnInventory);
        btnCloseInventory = findViewById(R.id.btnCloseInventory);

        tvPlusOneToDice.bringToFront();

        btnPlay.setEnabled(false); // Disabled by default
        btnDice.setBackgroundResource(R.drawable.button_background_img);
        btnPlay.setBackgroundResource(R.drawable.button_background_img);
        btnInventory.setBackgroundResource(R.drawable.button_background_img);
        btnContinue.setBackgroundResource(R.drawable.button_background_img);
        btnPlusOne.setBackgroundResource(R.drawable.button_background_img);
        btnCloseInventory.setBackgroundResource(R.drawable.button_background_img);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnDice.setBackgroundTintList(null); // Désactiver la teinte
            btnPlay.setBackgroundTintList(null); // Désactiver la teinte
            btnInventory.setBackgroundTintList(null); // Désactiver la teinte
            btnContinue.setBackgroundTintList(null); // Désactiver la teinte
            btnPlusOne.setBackgroundTintList(null); // Désactiver la teinte
            btnCloseInventory.setBackgroundTintList(null); // Désactiver la teinte
        }
    }

    private void playSoundEffect(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
    }

    /**
     * Handle the intent data passed from the previous activity.
     */
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            spriteSelection = intent.getStringExtra("selection_key");
            boardBoardView.setPlayerSpriteSelection(spriteSelection);
        }
    }

    /**
     * Set up listeners for UI components like buttons and inventory management.
     */
    private void setupListeners() {
        btnDice.setOnClickListener(v -> {
            if (btnDice.isEnabled()) {
                playSoundEffect(R.raw.clik);
                boardBoardView.startDiceRoll();
                btnPlusOne.setVisibility(View.VISIBLE);

                btnDice.setTextColor(Color.WHITE);

                if (newRound && boardBoardView.getIsPlayerMoving()) {
                    newRound = false;
                }
            }
        });

        btnPlay.setOnClickListener(v -> {
            startMiniGame();
            playSoundEffect(R.raw.clik);

        });

        btnPlusOne.setOnClickListener(v -> {
            playSoundEffect(R.raw.itemuse);
            Toast.makeText(Board_MA.this, "Item Used: Dice +1", Toast.LENGTH_SHORT).show();
            btnPlusOne.setEnabled(false);
            btnPlusOne.setTextColor(Color.GRAY);
            doPlayerUsePlusOneItem = true;
            boardBoardView.setItemAction(1);
        });

        setupInventoryManagement();
    }

    /**
     * Setup inventory management animations and button actions.
     */
    private void setupInventoryManagement() {
        btnInventory = findViewById(R.id.btnInventory);
        View inventoryWindow = findViewById(R.id.inventoryWindow);
        btnCloseInventory = findViewById(R.id.btnCloseInventory);

        btnInventory.setOnClickListener(v -> {
            playSoundEffect(R.raw.clik);
            inventoryWindow.setVisibility(View.VISIBLE);
            inventoryWindow.animate()
                    .translationX(10)
                    .setDuration(200)
                    .start();
        });

        btnCloseInventory.setOnClickListener(v -> {
            playSoundEffect(R.raw.clik);
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
            if (boardBoardView.getIsPlayerMoving()) {
                handlePlayerIdleState();
            } else if (!newRound) {
                btnDice.setEnabled(false);
                btnDice.setTextColor(Color.GRAY);
            }

            if (boardBoardView.isDiceRolling()) {
                handleDiceRollingState();
            }

            playerMovementHandler.postDelayed(this, 10);
        }
    };

    /**
     * Handle player idle state and enable/disable buttons accordingly.
     */
    private void handlePlayerIdleState() {
        if (!newRound) {
            if (getPlayerCurrentCaseActionFromBoardView() == 0) {
                btnDice.setEnabled(true);
                btnPlay.setEnabled(false);
                btnDice.setTextColor(Color.WHITE);
            } else {
                btnDice.setEnabled(false);
                btnDice.setTextColor(Color.GRAY);
                btnPlay.setEnabled(true);
                btnPlay.setTextColor(Color.WHITE);
            }

        } else {
            btnDice.setEnabled(true);
            btnPlay.setEnabled(false);
        }

    }

    /**
     * Handle dice rolling state.
     */
    private void handleDiceRollingState() {
        if (doPlayerUsePlusOneItem) {
            tvPlusOneToDice.setVisibility(View.VISIBLE);
        } else {
            btnPlusOne.setTextColor(Color.WHITE);
        }
        btnPlay.setEnabled(false);
    }

    /**
     * Start a mini-game based on the player's current case action.
     */
    private void startMiniGame() {
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
            if (requestCode == RUN_REQUEST_CODE) {
                int finalRequestCode = requestCode;
                Intent finalIntent = intent;
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("RunGame")
                        .setMessage("To Win --> Score >= 50\n (the game may be laggy at the begeinning)")
                        .setPositiveButton("Okay !", (dialog, which) -> {
                            // Action à effectuer si le joueur commence
                            dialog.dismiss();
                            startActivityForResult(finalIntent, finalRequestCode);

                        })
                        .setCancelable(false) // Empêche de fermer le dialog en dehors des boutons
                        .show();
            } else {
                startActivityForResult(intent, requestCode);

            }
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
        }
    }

    private void handleMiniGameResult(int requestCode, String result) {
        switch (requestCode) {
            case LABY_REQUEST_CODE:
                tvScore.setText(result.equals("quit") ? "Labyrinthe Failed!" : "Labyrinthe finished in " + result + "s");
                break;
            case RUN_REQUEST_CODE:
                tvScore.setText(result.equals("quit") ? "RunGame Failed!" : "RunGame finished with " + result + " coins");
                break;
            case TAQUIN_REQUEST_CODE:
                tvScore.setText(result.equals("quit") ? "Taquin Failed!" : "Taquin finished in " + result + "s");
                break;
        }
        if (result.equals("quit")) {
            playSoundEffect(R.raw.lose);
        } else {
            playSoundEffect(R.raw.win);
        }
        if (!result.equals("quit")) {
            doPlayerWinPreviousGame = true;
        }

        tvScore.setVisibility(View.VISIBLE);
        btnContinue.setVisibility(View.VISIBLE);
        btnPlay.setEnabled(false);

        btnContinue.setOnClickListener(v -> {
            playSoundEffect(R.raw.clik);
            newRound = true;
            tvScore.setVisibility(View.GONE);
            btnContinue.setVisibility(View.GONE);
            btnDice.setEnabled(true);
            doPlayerUsePlusOneItem = false;
            tvPlusOneToDice.setVisibility(View.GONE);
            btnDice.setTextColor(Color.WHITE);
            btnPlay.setEnabled(false);
            btnPlay.setTextColor(Color.GRAY);

            if (doPlayerWinPreviousGame) {
                starsNumberValue++;
                tvStarsNumber.setText(String.valueOf(starsNumberValue));
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        currentPlayerCaseNumber = boardBoardView.getPlayerCaseNumber();
        outState.putInt("playerCaseNumber", currentPlayerCaseNumber);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (boardBoardView != null) {
            boardBoardView.setPlayerMovingAnimationToTargetCase(false);
            currentPlayerCaseNumber = savedInstanceState.getInt("playerCaseNumber");
            boardBoardView.setPlayerCaseNumber(currentPlayerCaseNumber);
        }
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
                .setTitle("Quit ?")
                .setMessage("Are you sure you want to quit? Your progress will be lost")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish(); // Appelle finish() pour gérer la fermeture
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Ferme la boîte de dialogue sans quitter
                })
                .setCancelable(false) // Empêche la fermeture de la boîte de dialogue en appuyant à l'extérieur
                .show();
    }
}
