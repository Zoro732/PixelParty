package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;

public class RunGame_MA extends AppCompatActivity {

    private RunGame_GameView gameView;
    private boolean isGameOver = false;
    private boolean sound = true;
    private Button btnResume, btnRestart, btnQuit;
    private String game_mode;
    private ImageView ivSettings;

    private MediaPlayer mainTheme;

    private Handler handler;
    private FrameLayout flMainPage;

    private boolean doPlayerQuitGame = false;

    public int scoreToWinForBoard = 50;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Afficher un AlertDialog dès le lancement de l'Activity

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        handler = new Handler();

        setContentView(R.layout.rungame);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout gameFrame = findViewById(R.id.flMainPage);
        // Initialiser GameView avec la largeur et hauteur de l'écran
        gameView = new RunGame_GameView(this, getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        gameFrame.addView(gameView);

        // Main theme music
        mainTheme = MediaPlayer.create(this, R.raw.rungame_maintheme);
        mainTheme.setVolume(0.5f, 0.5f);
        mainTheme.setLooping(true);
        mainTheme.start();


        Intent intent = getIntent();
        game_mode = intent.getStringExtra("game_mode");
        sound = intent.getBooleanExtra("sound", true);


        flMainPage = findViewById(R.id.flMainPage);

        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.bringToFront();

        if (game_mode != null) {
            if (game_mode.equals("board")) {
                ivSettings.setVisibility(View.GONE);
            }
        }

        // Récupérer l'ImageView
        btnResume = findViewById(R.id.btnResume);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit = findViewById(R.id.btnQuit);

        setButtonBackground();

        TextView tvPauseText = findViewById(R.id.tvGamePause);
        tvPauseText.bringToFront();

        // Définir un OnClickListener
        ivSettings.setOnClickListener(v -> {
            // Action à réaliser lors du clic
            gameView.pause();
            if (sound) {
                playSoundEffect(R.raw.pause);
            }
            btnResume.setVisibility(View.VISIBLE);
            btnRestart.setVisibility(View.VISIBLE);
            btnQuit.setVisibility(View.VISIBLE);
            tvPauseText.setVisibility(View.VISIBLE);
            tvPauseText.setText("Pause");
            if (mainTheme != null && mainTheme.isPlaying()) {
                mainTheme.pause();
            }
            flMainPage.setBackgroundColor(Color.RED);
        });

        btnResume.setOnClickListener(v -> {
            gameView.resume();
            if (sound) {
                playSoundEffect(R.raw.button_clik);
            }
            btnResume.setVisibility(View.GONE);
            btnRestart.setVisibility(View.GONE);
            btnQuit.setVisibility(View.GONE);
            tvPauseText.setVisibility(View.GONE);
            mainTheme.start();
            flMainPage.setBackgroundColor(Color.TRANSPARENT);

        });

        btnRestart.setOnClickListener(v -> {
            gameView.restartGame();
            isGameOver = false;
            ivSettings.setVisibility(View.VISIBLE);
            if (sound) {
                playSoundEffect(R.raw.button_clik);
            }
            btnResume.setVisibility(View.GONE);
            btnRestart.setVisibility(View.GONE);
            btnQuit.setVisibility(View.GONE);
            tvPauseText.setVisibility(View.GONE);
            mainTheme.seekTo(0);
            mainTheme.start();
            startGameLoop();


        });

        btnQuit.setOnClickListener(v -> {
            gameView.quitGame();
            if (sound) {
                playSoundEffect(R.raw.button_clik);
            }
        });


        // Lancer la boucle de jeu
        startGameLoop();


    }

    private void startGameLoop() {
        handler = new Handler();
        Runnable checkGameOver = new Runnable() {
            @Override
            public void run() {
                Log.d("RunGame_MA", "In run()");
                if (!isGameOver) {
                    Log.d("RunGame_MA", "Vérification de la fin du jeu");
                    // Vérifie si le joueur est mort
                    if (gameView.getIsDead()) {
                        Log.d("RunGame_MA", "Le joueur est mort");
                        // Le joueur est mort, gère la fin du jeu
                        if (game_mode != null) {
                            if (game_mode.equals("board")) {
                                finish();
                            }
                            if (game_mode.equals("minigames")) {
                                onPause();
                                btnRestart.setVisibility(View.VISIBLE);
                                btnQuit.setVisibility(View.VISIBLE);
                                ivSettings.setVisibility(View.GONE);
                                TextView tvPauseText = findViewById(R.id.tvGamePause);
                                tvPauseText.setVisibility(View.VISIBLE);
                                tvPauseText.setText("Game Over");
                                tvPauseText.setGravity(Gravity.CENTER);
                                mainTheme.pause();
                                if (sound) {
                                    playSoundEffect(R.raw.lose);
                                }
                            }
                            isGameOver = true;
                        }
                    } else {
                        // Sinon, vérifier à nouveau dans un certain délai
                        handler.postDelayed(this, 100); // Vérifier toutes les secondes
                    }
                }
            }
        };

        // Lancer la vérification dès que le jeu commence ou redémarre
        handler.post(checkGameOver);
    }


    private void playSoundEffect(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
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


    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        // Ajoutez des données si nécessaire
        if (gameView.getScore() >= scoreToWinForBoard) {
            resultIntent.putExtra("score", String.valueOf(gameView.getScore()));
        } else if (doPlayerQuitGame || gameView.getScore() < scoreToWinForBoard) {
            resultIntent.putExtra("score", "quit");
        }
        setResult(RESULT_OK, resultIntent);
        super.finish(); // Terminez l'Activity
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mainTheme != null && mainTheme.isPlaying()) {
            mainTheme.pause(); // Arrête la musique
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        if (mainTheme != null && !mainTheme.isPlaying()) { // Si la musique est à l'arrêt
            mainTheme.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null); // Arrête toutes les tâches
        }
        if (mainTheme != null) {
            mainTheme.stop();
            mainTheme.release();
            mainTheme = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onBackPressed() {
        onPause();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Quit ?")
                .setMessage("Are you sure you want to quit? Your progress will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    doPlayerQuitGame = true;
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

}

