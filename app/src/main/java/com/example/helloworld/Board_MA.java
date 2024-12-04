package com.example.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Board_MA extends AppCompatActivity {

    // Constants for request codes
    private static final int LABY_REQUEST_CODE = 1;
    private static final int RUN_REQUEST_CODE = 2;
    private static final int TAQUIN_REQUEST_CODE = 3;
    private static final int MINI_BOSS_REQUEST_CODE = 4;
    private static final int END_BOSS_REQUEST_CODE = 5;

    // UI Elements
    private Board_BoardView boardBoardView;
    private TextView tvScore, tvPlusOneToDice, tvStarsNumber;
    private Button btnContinue, btnPlay, btnDice, btnPlusOne, btnInventory, btnCloseInventory;
    private ImageView ivStar;

    // Game state variables
    private int currentPlayerCaseNumber;
    private boolean doPlayerUsePlusOneItem = false;
    private boolean newRound = false;
    private int starsNumberValue = 0;
    private boolean doPlayerWinPreviousGame = false;
    private boolean doPlayerEndBoard = false;
    private boolean sound = true;

    // Media player for main theme
    private MediaPlayer mainTheme;

    // Handler for player movement
    private final Handler playerMovementHandler = new Handler(Looper.getMainLooper());

    // Sprite selection
    private String spriteSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        initializeUI();
        handleIntentData();
        setupListeners();
        startMainTheme();
        playerMovementHandler.post(playerMovementRunnable);
    }

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
        ivStar = findViewById(R.id.ivStar);

        tvPlusOneToDice.bringToFront();
        setButtonBackgrounds();
    }

    private void setButtonBackgrounds() {
        int[] buttonIds = {R.id.btnDice, R.id.btnPlay, R.id.btnInventory, R.id.btnContinue, R.id.btnPlusOne, R.id.btnCloseInventory};
        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setBackgroundResource(R.drawable.button_background_img);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                button.setBackgroundTintList(null);
            }
        }
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            spriteSelection = intent.getStringExtra("selection_key");
            boardBoardView.setPlayerSpriteSelection(spriteSelection);
            sound = intent.getBooleanExtra("sound", true);
        }
    }

    private void setupListeners() {
        btnDice.setOnClickListener(v -> handleDiceClick());
        btnPlay.setOnClickListener(v -> handlePlayClick());
        btnPlusOne.setOnClickListener(v -> handlePlusOneClick());
        setupInventoryManagement();
    }

    private void handleDiceClick() {
        if (btnDice.isEnabled()) {
            if (sound) {
                playSoundEffect(R.raw.button_clik);
            }
            boardBoardView.startDiceRoll();
            btnPlusOne.setVisibility(View.VISIBLE);
            btnDice.setTextColor(Color.WHITE);
            if (newRound && boardBoardView.getIsPlayerMoving()) {
                newRound = false;
            }
        }
    }

    private void handlePlayClick() {
        startMiniGame();
        if (sound) {
            playSoundEffect(R.raw.button_clik);
        }
    }

    private void handlePlusOneClick() {
        if (sound) {
            playSoundEffect(R.raw.board_itemused);
        }
        Toast.makeText(Board_MA.this, "Item Used: Dice +1", Toast.LENGTH_SHORT).show();
        btnPlusOne.setEnabled(false);
        btnPlusOne.setTextColor(Color.GRAY);
        doPlayerUsePlusOneItem = true;
        boardBoardView.setItemAction(1);
    }

    private void setupInventoryManagement() {
        View inventoryWindow = findViewById(R.id.inventoryWindow);
        btnInventory.setOnClickListener(v -> {
            if (sound) {
                playSoundEffect(R.raw.button_clik);
            }
            inventoryWindow.setVisibility(View.VISIBLE);
            inventoryWindow.animate().translationX(10).setDuration(200).start();
        });

        btnCloseInventory.setOnClickListener(v -> {
            if (sound) {
                playSoundEffect(R.raw.button_clik);
            }
            inventoryWindow.animate().translationX(0).setDuration(200).withEndAction(() -> inventoryWindow.setVisibility(View.GONE)).start();
        });
    }

    private void startMainTheme() {
        if (sound) {
            mainTheme = MediaPlayer.create(this, R.raw.board_maintheme);
            mainTheme.setLooping(true);
            mainTheme.setVolume(0.5f, 0.5f);
            mainTheme.start();
        }
    }

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

    private void handleDiceRollingState() {
        if (doPlayerUsePlusOneItem) {
            tvPlusOneToDice.setVisibility(View.VISIBLE);
        } else {
            btnPlusOne.setTextColor(Color.WHITE);
        }
        btnPlay.setEnabled(false);
    }

    private void startMiniGame() {
        Intent intent = null;
        int requestCode = -1;
        String message = "";
        String title = "";

        switch (getPlayerCurrentCaseActionFromBoardView()) {
            case 1:
                intent = new Intent(this, Labyrinthe_MA.class);
                requestCode = LABY_REQUEST_CODE;
                message = "Complete the Labyrinth to win!";
                title = "Labyrinthe";
                break;
            case 2:
                intent = new Intent(this, RunGame_MA.class);
                requestCode = RUN_REQUEST_CODE;
                message = "To Win --> Score >= 50\n (the game may be laggy at the beginning)";
                title = "RunGame";
                break;
            case 3:
                intent = new Intent(this, Taquin_MA.class);
                requestCode = TAQUIN_REQUEST_CODE;
                message = "Solve the Taquin puzzle to win!";
                title = "Taquin";
                break;
            case 5:
                intent = new Intent(this, Boss_MA.class);
                requestCode = END_BOSS_REQUEST_CODE;
                message = "Defeat the Boss to win!\nKill at least 30 minions";
                title = "Boss";
                break;
        }

        if (intent != null) {
            intent.putExtra("game_mode", "board");
            intent.putExtra("selection_key", spriteSelection);

            showGameDialog(title, message, intent, requestCode);
        }
    }

    private void showGameDialog(String title, String message, Intent intent, int requestCode) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Okay !", (dialog, which) -> {
                    dialog.dismiss();
                    startActivityForResult(intent, requestCode);
                })
                .setOnDismissListener(dialog -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        hideNavigationBar();
                    }
                })
                .setCancelable(false)
                .show();
    }

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
            case END_BOSS_REQUEST_CODE:
                SpannableString spannableString;
                if (result.equals("quit")) {
                    spannableString = new SpannableString("Boss Failed! You lose !");
                    handleEndGame();

                } else {
                    String text = "Boss beated! You win! " + starsNumberValue + " ";
                    spannableString = new SpannableString(text);

                    Drawable starDrawable = ContextCompat.getDrawable(this, R.drawable.star);
                    assert starDrawable != null;
                    starDrawable.setBounds(0, 0, starDrawable.getIntrinsicWidth(), starDrawable.getIntrinsicHeight());
                    ImageSpan imageSpan = new ImageSpan(starDrawable, DynamicDrawableSpan.ALIGN_CENTER);

                    spannableString.setSpan(imageSpan, text.length() - 1, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    handleEndGame();
                }
                doPlayerEndBoard = true;
                tvScore.setText(spannableString);
                break;
        }
        if (requestCode != END_BOSS_REQUEST_CODE) {

            if (result.equals("quit")) {
                if (sound) {
                    playSoundEffect(R.raw.lose);
                }
            } else {
                if (sound) {
                    playSoundEffect(R.raw.win);
                }
                doPlayerWinPreviousGame = true;
            }
        } else {
            if (result.equals("quit")) {
                if (sound) {
                    playSoundEffect(R.raw.board_lose);
                }
            } else {
                if (sound) {
                    playSoundEffect(R.raw.mole_victory);
                }
            }
        }

        tvScore.setVisibility(View.VISIBLE);
        if (!doPlayerEndBoard) {
            btnContinue.setVisibility(View.VISIBLE);
        }
        btnPlay.setEnabled(false);

        btnContinue.setOnClickListener(v -> handleContinueClick());
    }

    private void handleContinueClick() {
        if (sound) {
            playSoundEffect(R.raw.button_clik);
        }
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
    }

    private void handleEndGame() {
        mainTheme.stop();
        btnContinue.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        btnDice.setVisibility(View.GONE);
        btnInventory.setVisibility(View.GONE);
        tvStarsNumber.setVisibility(View.GONE);
        ivStar.setVisibility(View.GONE);

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

private void playSoundEffect(int soundResourceId) {
    MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
    mediaPlayer.start();
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
protected void onResume() {
    super.onResume();
    if (mainTheme != null && !mainTheme.isPlaying()) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> mainTheme.start(), 2000); // 2000 milliseconds delay
    }
}

@Override
protected void onPause() {
    super.onPause();
    if (mainTheme != null && mainTheme.isPlaying()) {
        mainTheme.pause();
    }
}

@Override
protected void onDestroy() {
    super.onDestroy();
    playerMovementHandler.removeCallbacks(playerMovementRunnable);
    if (mainTheme != null) {
        mainTheme.pause();
        mainTheme = null;
    }
}

@Override
public void onBackPressed() {
    onPause();
    new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Quit ?")
            .setMessage("Are you sure you want to quit? Your progress will be lost")
            .setPositiveButton("Yes", (dialog, which) -> finish())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .setCancelable(false)
            .show();
}
}