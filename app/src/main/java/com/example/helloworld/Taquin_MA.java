package com.example.helloworld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Taquin_MA extends AppCompatActivity {

    // Variables globales
    private final ImageView[][] tiles = new ImageView[3][3];
    private final ImageView[][] solutionTiles = new ImageView[3][3];
    private int emptyRow = 2, emptyCol = 2;
    private Bitmap originalBitmap;

    private TextView tvTimer, tvPause;
    private Button btnResume, btnRestart, btnQuit;
    private final int defaultTimerValue = 120;
    private int timerValue = defaultTimerValue;
    private final Handler timerHandler = new Handler();
    private boolean isTimerRunning = false;
    private boolean isLoose = false;
    private boolean doPlayerQuitGame = false;

    private MediaPlayer mainTheme;
    private String game_mode;

    private LinearLayout llPauseMenu;

    private GridLayout glGame;

    // Lifecycle : Création
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taquin);

        initializeGame();
        initializeUI();
        startCountdownTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        if (!isTimerRunning) startCountdownTimer();
        mainTheme.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountdownTimer();
        mainTheme.pause();
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        if (game_mode.equals("minigames")) {
            onPause();
            llPauseMenu.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.GONE);
            stopCountdownTimer();
            glGame.setEnabled(false); // Disable interaction
            mainTheme.pause();
        } else {
            if (doPlayerQuitGame || isLoose) {
                resultIntent.putExtra("score", "quit");
            } else {
                resultIntent.putExtra("score", String.valueOf(defaultTimerValue - timerValue));
            }
            setResult(RESULT_OK, resultIntent);
        }

        super.finish();
    }

    // Initialisation
    private void initializeGame() {
        Intent intent = getIntent();
        game_mode = intent.getStringExtra("game_mode");
        mainTheme = MediaPlayer.create(this, R.raw.taquin_maintheme);
        mainTheme.setVolume(0.5f, 0.5f);
        mainTheme.setLooping(true);
        mainTheme.start();

        originalBitmap = chooseRandomImage();
    }

    private void initializeUI() {
        // Masquer l'interface système
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        // Initialisation des éléments UI
        glGame = findViewById(R.id.glGame);
        tvTimer = findViewById(R.id.tvTimer);

        initializeTiles(tiles);
        initializeSolutionTiles();
        shuffleTiles();

        // Pause menu
        initializePauseMenu();

        if (game_mode != null && game_mode.equals("board")) {
            findViewById(R.id.ivSettings).setVisibility(View.GONE);
        }
    }

    private void playSoundEffect(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    private void initializePauseMenu() {
        llPauseMenu = findViewById(R.id.llPauseMenu);
        btnResume = findViewById(R.id.btnResume);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit = findViewById(R.id.btnQuit);
        tvPause = findViewById(R.id.tvGamePause);
        ImageView ivSettings = findViewById(R.id.ivSettings);

        glGame = findViewById(R.id.glGame); // Get the GridLayout

        btnResume.setOnClickListener(v -> {
            llPauseMenu.setVisibility(View.GONE);
            startCountdownTimer();
            glGame.setEnabled(true); // Disable interaction
            mainTheme.start();
            playSoundEffect(R.raw.button_clik);
        });

        btnRestart.setOnClickListener(v -> {
            restartGame();
            playSoundEffect(R.raw.button_clik);
        });
        btnQuit.setOnClickListener(v -> {
            finish();
            llPauseMenu.setVisibility(View.GONE);
            playSoundEffect(R.raw.button_clik);
        });

        ivSettings.setOnClickListener(v -> {
            llPauseMenu.setVisibility(View.VISIBLE);
            stopCountdownTimer();
            glGame.setEnabled(false); // Disable interaction
            mainTheme.pause();
            playSoundEffect(R.raw.button_clik);
        });

        setButtonBackground();
    }

    private Bitmap chooseRandomImage() {
        int[] images = {
                R.drawable.taquin_shape,
                R.drawable.taquin_fox,
                R.drawable.taquin_dog,
                R.drawable.taquin_sus,
                R.drawable.taquin_globe
        };

        // Initialiser un générateur de nombres aléatoires avec une graine basée sur l'heure
        Random random = new Random(System.currentTimeMillis());

        // Sélectionner un index aléatoire
        int randomImageIndex = random.nextInt(images.length);

        // Retourner l'image correspondante
        return BitmapFactory.decodeResource(getResources(), images[randomImageIndex]);
    }


    // Chronomètre
    private void startCountdownTimer() {
        isTimerRunning = true;
        timerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (timerValue > 0) {
                    tvTimer.setText(String.valueOf(timerValue));
                    timerValue--;
                    timerHandler.postDelayed(this, 1000);
                } else {
                    tvTimer.setText("0");
                    onCountdownFinished();
                }
            }
        });
    }

    private void stopCountdownTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacksAndMessages(null);
    }

    private void onCountdownFinished() {
        isLoose = true;
        stopCountdownTimer();
        finish();
    }

    // Gestion des tuiles
    private void initializeTiles(ImageView[][] tileArray) {
        emptyRow = 2;
        emptyCol = 2;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("ivTile_" + (i * 3 + j + 1), "id", getPackageName());
                ImageView tile = findViewById(resID);
                tileArray[i][j] = tile;

                if (i == 2 && j == 2) {
                    tile.setImageResource(0);
                } else {
                    tile.setImageBitmap(getTileBitmap(i, j));
                }

                final int row = i, col = j;
                tile.setOnClickListener(v -> {
                    onTileClick(row, col);
                });
            }
        }
    }

    private void initializeSolutionTiles() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("tile_" + (i * 3 + j + 1) + "_solution", "id", getPackageName());
                ImageView tile = findViewById(resID);
                solutionTiles[i][j] = tile;

                if (i == 2 && j == 2) {
                    tile.setImageResource(0);
                } else {
                    tile.setImageBitmap(getTileBitmap(i, j));
                }
            }
        }
    }

    private Bitmap getTileBitmap(int row, int col) {
        int tileWidth = originalBitmap.getWidth() / 3;
        int tileHeight = originalBitmap.getHeight() / 3;
        return Bitmap.createBitmap(originalBitmap, col * tileWidth, row * tileHeight, tileWidth, tileHeight);
    }

    private void shuffleTiles() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int direction = random.nextInt(4);
            int newRow = emptyRow, newCol = emptyCol;

            switch (direction) {
                case 0:
                    newRow--;
                    break;
                case 1:
                    newRow++;
                    break;
                case 2:
                    newCol--;
                    break;
                case 3:
                    newCol++;
                    break;
            }

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                Bitmap tempBitmap = tiles[newRow][newCol].getDrawable() != null ?
                        ((BitmapDrawable) tiles[newRow][newCol].getDrawable()).getBitmap() : null;

                tiles[emptyRow][emptyCol].setImageBitmap(tempBitmap);
                tiles[newRow][newCol].setImageResource(0);

                emptyRow = newRow;
                emptyCol = newCol;
            }
        }
    }

    private void onTileClick(int row, int col) {
        if (Math.abs(emptyRow - row) + Math.abs(emptyCol - col) == 1) {
            playSoundEffect(R.raw.taquin_move);
            Bitmap tempBitmap = tiles[row][col].getDrawable() != null ?
                    ((BitmapDrawable) tiles[row][col].getDrawable()).getBitmap() : null;

            tiles[emptyRow][emptyCol].setImageBitmap(tempBitmap);
            tiles[row][col].setImageResource(0);

            emptyRow = row;
            emptyCol = col;

            if (isGameWon()) {
                onGameWon();
            }
        }
    }

    private boolean isGameWon() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Drawable solutionDrawable = solutionTiles[i][j].getDrawable();
                Drawable tileDrawable = tiles[i][j].getDrawable();

                if (solutionDrawable instanceof BitmapDrawable && tileDrawable instanceof BitmapDrawable) {
                    Bitmap solutionBitmap = ((BitmapDrawable) solutionDrawable).getBitmap();
                    Bitmap tileBitmap = ((BitmapDrawable) tileDrawable).getBitmap();

                    if (!solutionBitmap.sameAs(tileBitmap)) return false;
                } else if (solutionDrawable != null || tileDrawable != null) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void onGameWon() {
        if (game_mode.equals("board")) {
            stopCountdownTimer();
            finish();
        } else {
            llPauseMenu.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.GONE);
            stopCountdownTimer();
            glGame.setEnabled(false); // Disable interaction
            mainTheme.pause();
            playSoundEffect(R.raw.win);
            tvPause.setGravity(Gravity.CENTER);
            tvPause.setText("You won! in " + (defaultTimerValue - timerValue) + " seconds");
        }
    }

    // Divers
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void setButtonBackground() {
        btnResume = findViewById(R.id.btnResume);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit = findViewById(R.id.btnQuit);

        btnResume.setBackgroundResource(R.drawable.button_background_img);
        btnRestart.setBackgroundResource(R.drawable.button_background_img);
        btnQuit.setBackgroundResource(R.drawable.button_background_img);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnResume.setBackgroundTintList(null);
            btnRestart.setBackgroundTintList(null);
            btnQuit.setBackgroundTintList(null);
        }
    }


    private void restartGame() {
        recreate();
    }

    @Override
    public void onBackPressed() {
        onPause();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Quit ?")
                .setMessage("Are you sure you want to quit? Your progress will be lost")
                .setPositiveButton("Yes", (dialog, which) -> {
                    doPlayerQuitGame = true;
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
