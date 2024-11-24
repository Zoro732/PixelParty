package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

    private String game_mode;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        setContentView(R.layout.rungame);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout gameFrame = findViewById(R.id.gameFrame);
        // Initialiser GameView avec la largeur et hauteur de l'écran
        gameView = new RunGame_GameView(this, getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        gameFrame.addView(gameView);


        Intent intent = getIntent();
        game_mode = intent.getStringExtra("game_mode");

        ImageView imageSettings = findViewById(R.id.iv_Settings);
        imageSettings.bringToFront();

        // Récupérer l'ImageView
        Button buttonResume = findViewById(R.id.resume);
        Button buttonRestart = findViewById(R.id.restart);
        Button buttonQuit = findViewById(R.id.quit);

        TextView pauseText = findViewById(R.id.gamePause);
        pauseText.bringToFront();

        // Définir un OnClickListener
        imageSettings.setOnClickListener(v -> {
            // Action à réaliser lors du clic
            gameView.pause();
            buttonResume.setVisibility(View.VISIBLE);
            buttonRestart.setVisibility(View.VISIBLE);
            buttonQuit.setVisibility(View.VISIBLE);
            pauseText.setVisibility(View.VISIBLE);
        });

        buttonResume.setOnClickListener(v -> {
            gameView.resume();
            buttonResume.setVisibility(View.GONE);
            buttonRestart.setVisibility(View.GONE);
            buttonQuit.setVisibility(View.GONE);
            pauseText.setVisibility(View.GONE);
        });

        buttonRestart.setOnClickListener(v -> {
            gameView.restartGame();
            buttonResume.setVisibility(View.GONE);
            buttonRestart.setVisibility(View.GONE);
            buttonQuit.setVisibility(View.GONE);
            pauseText.setVisibility(View.GONE);
        });

        buttonQuit.setOnClickListener(v -> {
            gameView.quitGame();
        });

        // Vérifier si le joueur est mort à chaque seconde
        final Handler handler = new Handler();
        Runnable checkGameOver = new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    // Vérifie si le joueur est mort
                    if (gameView.isDead()) {
                        // Le joueur est mort, gère la fin du jeu
                        if (game_mode != null) {
                            if (game_mode.equals("board")) {
                                Log.d("RunGame_MA", "game_mode.equals(\"board\")" + game_mode.equals("board"));
                                finish();
                            }
                            if (game_mode.equals("minigames")) {
                                Log.d("RunGame_MA", "gamemode" + game_mode.equals("minigames"));
                            }
                            isGameOver = true;
                        } else {
                            Log.d("RunGame_MA", "game_mode == null");
                        }

                    } else {
                        // Sinon, vérifier à nouveau dans un certain délai
                        handler.postDelayed(this, 1000); // Vérifier toutes les secondes
                    }
                }
            }
        };

        // Lancer la vérification dès que le jeu commence
        handler.post(checkGameOver);
    }


    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        // Ajoutez des données si nécessaire
        resultIntent.putExtra("score", String.valueOf(gameView.getScore()));
        setResult(RESULT_OK, resultIntent);
        super.finish(); // Terminez l'Activity
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.pause();
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
}

