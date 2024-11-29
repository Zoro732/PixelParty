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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class Labyrinthe_MA extends AppCompatActivity implements SensorEventListener {

    private Labyrinthe_GameView labyrintheGameView;
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private TextView tvGamePause, tvTimerIndicator;
    private Button btnResume, btnRestart, btnQuit;
    private ImageView ivSettings;
    private String game_mode;
    private boolean isGameFinished = false;
    private boolean doPlayerQuitGame = false;

    private MediaPlayer maintheme;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        setContentView(R.layout.labyrinthe);
        FrameLayout gameFrame = findViewById(R.id.flMainPage);

        ivSettings = findViewById(R.id.ivSettings);

        Intent intent = getIntent();
        game_mode = intent.getStringExtra("game_mode");
        String selection = intent.getStringExtra("selection_key");

        labyrintheGameView = new Labyrinthe_GameView(this, selection);
        gameFrame.addView(labyrintheGameView);

        if ("minigames".equals(game_mode)) {
            setupMinigameMode();
        }

        setButtonBackground();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        maintheme = MediaPlayer.create(this,R.raw.labyrinththeme);
        maintheme.setVolume(0.5f,0.5f);
        maintheme.setLooping(true);
        maintheme.start();
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

    private void setupMinigameMode() {

        ivSettings.bringToFront();
        btnResume = findViewById(R.id.btnResume);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit = findViewById(R.id.btnQuit);
        tvGamePause = findViewById(R.id.tvGamePause);
        tvTimerIndicator = findViewById(R.id.tvTimerIndicator);

        tvTimerIndicator.setVisibility(View.GONE);

        setupPauseMenu(ivSettings);
    }

    private void setupPauseMenu(ImageView imageSettings) {
        tvGamePause.bringToFront();
        tvTimerIndicator.bringToFront();

        imageSettings.setOnClickListener(v -> {
            if (labyrintheGameView != null) {
                labyrintheGameView.pauseGame();
                playSoundEffect(R.raw.pause);
            }
            maintheme.pause();
            showPauseMenu();
        });

        btnResume.setOnClickListener(v -> {
            resumeGame();
            maintheme.start();
            playSoundEffect(R.raw.clik);
        });

        btnRestart.setOnClickListener(v -> {
            restartGame();
            maintheme.seekTo(0);
            maintheme.start();
            playSoundEffect(R.raw.clik);
        });

        btnQuit.setOnClickListener(v -> {
            quitGame();
            playSoundEffect(R.raw.clik);
        });
    }

    private void playSoundEffect(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
    }

    private void showPauseMenu() {
        tvGamePause.setText("Pause");
        btnResume.setVisibility(View.VISIBLE);
        btnRestart.setVisibility(View.VISIBLE);
        btnQuit.setVisibility(View.VISIBLE);
        tvGamePause.setVisibility(View.VISIBLE);
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
            isGameFinished = false;
            ivSettings.setVisibility(View.VISIBLE);
        }
        hidePauseMenu();
        tvTimerIndicator.setVisibility(View.GONE);
    }

    private void quitGame() {
        if (labyrintheGameView != null) {
            labyrintheGameView.quitGame();
        }
    }

    private void hidePauseMenu() {
        btnResume.setVisibility(View.GONE);
        btnRestart.setVisibility(View.GONE);
        btnQuit.setVisibility(View.GONE);
        tvGamePause.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        maintheme.start();
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
        maintheme.pause();
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

            tvGamePause.setVisibility(View.VISIBLE);
            tvGamePause.setText("You WIN !");

            tvTimerIndicator.setVisibility(View.VISIBLE);
            tvTimerIndicator.setText("Made in " + labyrintheGameView.getRemainingTime() + "s");

            ivSettings.setVisibility(View.GONE);
            btnRestart.setVisibility(View.VISIBLE);
            btnQuit.setVisibility(View.VISIBLE);
            playSoundEffect(R.raw.win);
            maintheme.pause();

        } else if ("board".equals(game_mode)) {
            finish();
            labyrintheGameView.quitGame();

        }
    }

    private void handleGameLoss() {
        labyrintheGameView.pauseGame();
        showPauseMenu();
        btnResume.setVisibility(View.GONE);
        tvGamePause.setText("Game over !");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("score", doPlayerQuitGame ? "quit" : String.valueOf(labyrintheGameView.getRemainingTime()));
        setResult(RESULT_OK, resultIntent);
        super.finish();
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
