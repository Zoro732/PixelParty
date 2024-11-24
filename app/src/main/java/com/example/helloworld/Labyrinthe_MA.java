package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class Labyrinthe_MA extends AppCompatActivity implements SensorEventListener {

    private Labyrinthe_GameView labyrintheGameView;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private String selection;
    private TextView pauseText, timeText;
    private Button buttonResume, buttonRestart, buttonQuit;
    private String game_mode;
    private boolean isGameFinished = false;
    private boolean doPlayerQuitGame = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        setContentView(R.layout.labyrinthe);
        FrameLayout gameFrame = findViewById(R.id.gameFrame);

        Intent intent = getIntent();
        game_mode = intent.getStringExtra("game_mode");

        selection = intent.getStringExtra("selection_key");
        if (selection != null) {
            labyrintheGameView = new Labyrinthe_GameView(this, selection);
            gameFrame.addView(labyrintheGameView);
        }

        if ("minigames".equals(game_mode)) {
            setupMinigameMode();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void setupMinigameMode() {
        Log.d("Labyrinthe_MA", "Starting Labyrinthe_GameView in onCreate for minigames");

        ImageView imageSettings = findViewById(R.id.iv_Settings);
        imageSettings.bringToFront();

        buttonResume = findViewById(R.id.resume);
        buttonRestart = findViewById(R.id.restart);
        buttonQuit = findViewById(R.id.quit);
        pauseText = findViewById(R.id.gamePause);
        timeText = findViewById(R.id.timerIndicator);

        setupPauseMenu(imageSettings);
    }

    private void setupPauseMenu(ImageView imageSettings) {
        pauseText.bringToFront();
        timeText.bringToFront();

        imageSettings.setOnClickListener(v -> {
            if (labyrintheGameView != null) {
                labyrintheGameView.pauseGame();
            }
            showPauseMenu();
        });

        buttonResume.setOnClickListener(v -> resumeGame());
        buttonRestart.setOnClickListener(v -> restartGame());
        buttonQuit.setOnClickListener(v -> quitGame());
    }

    private void showPauseMenu() {
        pauseText.setText("Pause");
        buttonResume.setVisibility(View.VISIBLE);
        buttonRestart.setVisibility(View.VISIBLE);
        buttonQuit.setVisibility(View.VISIBLE);
        pauseText.setVisibility(View.VISIBLE);
    }

    private void resumeGame() {
        if (labyrintheGameView != null) {
            labyrintheGameView.resumeGame();
        }
        hidePauseMenu();
    }

    private void restartGame() {
        if (labyrintheGameView != null) {
            labyrintheGameView.restartGame();
        }
        hidePauseMenu();
        timeText.setVisibility(View.GONE);
    }

    private void quitGame() {
        if (labyrintheGameView != null) {
            labyrintheGameView.quitGame();
        }
    }

    private void hidePauseMenu() {
        buttonResume.setVisibility(View.GONE);
        buttonRestart.setVisibility(View.GONE);
        buttonQuit.setVisibility(View.GONE);
        pauseText.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        }

        if (labyrintheGameView != null) {
            labyrintheGameView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (labyrintheGameView != null) {
            labyrintheGameView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (labyrintheGameView != null) {
            labyrintheGameView.quitGame();
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
    public void onSensorChanged(SensorEvent event) {
        if (labyrintheGameView == null) {
            Log.e("Labyrinthe_MA", "Labyrinthe_GameView is null in onSensorChanged.");
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            labyrintheGameView.moveBall(x, y);
        }

        checkGameStatus();
    }

    private void checkGameStatus() {
        if (labyrintheGameView.isWin() && !isGameFinished) {
            isGameFinished = true;
            handleGameWin();
        } else if (labyrintheGameView.isLoosed()) {
            handleGameLoss();
        }
    }

    private void handleGameWin() {
        if ("minigames".equals(game_mode)) {
            labyrintheGameView.pauseGame();
            timeText.setText("Made in " + labyrintheGameView.getRemainingTime() + "s");
            timeText.setVisibility(View.VISIBLE);
            showPauseMenu();
            pauseText.setText("You WIN !");
        } else if ("board".equals(game_mode)) {
            Log.d("Labyrinthe_MA", "Labyrinthe finished, returning to Board_MA");
            finish();
        }
    }

    private void handleGameLoss() {
        labyrintheGameView.pauseGame();
        showPauseMenu();
        pauseText.setText("Game over !");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", doPlayerQuitGame ? "quit" : String.valueOf(labyrintheGameView.getRemainingTime()));
        Log.d("Labyrinthe_MA", "Finishing Labyrinthe_MA with score: " + resultIntent.getStringExtra("score"));
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        onPause();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Quitter le jeu")
                .setMessage("Êtes-vous sûr de vouloir quitter ? Votre progression sera perdue.")
                .setPositiveButton("Oui", (dialog, which) -> {
                    doPlayerQuitGame = true;
                    finish();
                })
                .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }


}
