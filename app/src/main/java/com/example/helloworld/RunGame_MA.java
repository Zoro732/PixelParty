package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class RunGame_MA extends AppCompatActivity {

    private RunGame_GameView gameView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        setContentView(R.layout.rungame);
        // Get instance of Vibrator from current Context
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FrameLayout gameFrame = findViewById(R.id.gameFrame);
        // Initialiser GameView avec la largeur et hauteur de l'écran
        gameView = new RunGame_GameView(this, getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight());
        gameFrame.addView(gameView);

        ImageView imageSettings = findViewById(R.id.settings);
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
