package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    private boolean isGameFinished = false; // Ajoutez ce drapeau

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        setContentView(R.layout.labyrinthe);

        // Initialize labyrintheGameView here, outside the if block
        FrameLayout gameFrame = findViewById(R.id.gameFrame);

        Intent intent = getIntent();
        game_mode = intent.getStringExtra("game_mode");

        if (intent.hasExtra("selection_key")) {
            selection = intent.getStringExtra("selection_key");
            labyrintheGameView = new Labyrinthe_GameView(this, selection); // You might need to provide a default value for 'selection' if it's not always available
            gameFrame.addView(labyrintheGameView);

            if (game_mode.equals("minigames")) { //Si on start l'activity depuis le minijeux
                Log.d("Labyrinthe_MA", "Starting Labyrinthe_GameView in onCreate for minigames");
                // Créer une instance de GameView et l'ajouter au FrameLayout
                ImageView imageSettings = findViewById(R.id.settings);
                imageSettings.bringToFront();

                // Récupérer l'ImageView
                buttonResume = findViewById(R.id.resume);
                buttonRestart = findViewById(R.id.restart);
                buttonQuit = findViewById(R.id.quit);

                pauseText = findViewById(R.id.gamePause);
                timeText = findViewById(R.id.timerIndicator);
                timeText.bringToFront();
                pauseText.bringToFront();

                // Définir un OnClickListener
                imageSettings.setOnClickListener(v -> {
                    // Action à réaliser lors du clic
                    if (labyrintheGameView != null) {
                        labyrintheGameView.pauseGame();
                    }
                    pauseText.setText("Pause");
                    buttonResume.setVisibility(View.VISIBLE);
                    buttonRestart.setVisibility(View.VISIBLE);
                    buttonQuit.setVisibility(View.VISIBLE);
                    pauseText.setVisibility(View.VISIBLE);
                });

                buttonResume.setOnClickListener(v -> {
                    if (labyrintheGameView != null) {
                        labyrintheGameView.resumeGame();
                    }
                    buttonResume.setVisibility(View.GONE);
                    buttonRestart.setVisibility(View.GONE);
                    buttonQuit.setVisibility(View.GONE);
                    pauseText.setVisibility(View.GONE);
                });
                buttonRestart.setOnClickListener(v -> {
                    if (labyrintheGameView != null) {
                        labyrintheGameView.restartGame();
                    }
                    buttonResume.setVisibility(View.GONE);
                    buttonRestart.setVisibility(View.GONE);
                    buttonQuit.setVisibility(View.GONE);
                    pauseText.setVisibility(View.GONE);
                    timeText.setVisibility(View.GONE);
                });
                buttonQuit.setOnClickListener(v -> {
                    if (labyrintheGameView != null) {
                        labyrintheGameView.quitGame();
                    }
                });
            } else {
                Log.d("Labyrinthe_MA", "Starting Labyrinthe_GameView in onCreate for board");
            }
        } else {
            Log.e("Labyrinthe_MA", "Labyrinthe_GameView is null in onCreate.");
        }

        // Initialiser le gestionnaire de capteurs
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enregistrer le listener du gyroscope
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        }

        // Vérification que labyrintheGameView n'est pas null avant d'appeler resume()
        if (labyrintheGameView != null) {
            labyrintheGameView.resume();
        } else {
            Log.e("Labyrinthe_MA", "Labyrinthe_GameView is not initialized.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Arrêter le listener du gyroscope
        sensorManager.unregisterListener(this);
        if (labyrintheGameView != null) {
            labyrintheGameView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            // Récupérer les valeurs du gyroscope
            float x = event.values[0]; // Rotation autour de l'axe X
            float y = event.values[1]; // Rotation autour de l'axe Y

            // Déplacer la boule en fonction des valeurs du gyroscope
            labyrintheGameView.moveBall(x, y); // Méthode à implémenter dans GameView
        }

        // Vérification si le jeu est gagné ou perdu
        if (labyrintheGameView.isWin() && !isGameFinished) {
            isGameFinished = true;

            if (game_mode.equals("minigames")) {
                labyrintheGameView.pauseGame();
                timeText.setText("Made in " + labyrintheGameView.getRemainingTime() + "s");
                timeText.setVisibility(View.VISIBLE);
                buttonRestart.setVisibility(View.VISIBLE);
                buttonQuit.setVisibility(View.VISIBLE);
                pauseText.setVisibility(View.VISIBLE);
                pauseText.setText("You WIN !");

            } else if (game_mode.equals("board")) {

                Log.d("Labyrinthe_MA", "Labyrinthe finished, returning to Board_MA");
                finish();
                labyrintheGameView.quitGame();
            }
        }

        if (labyrintheGameView.isLoosed()) {
            labyrintheGameView.pauseGame();
            buttonRestart.setVisibility(View.VISIBLE);
            buttonQuit.setVisibility(View.VISIBLE);
            pauseText.setVisibility(View.VISIBLE);
            pauseText.setText("Game over !");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne rien faire
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        // Ajoutez des données si nécessaire
        resultIntent.putExtra("score", String.valueOf(labyrintheGameView.getRemainingTime())); // Exemple : temps écoulé
        setResult(RESULT_OK, resultIntent);
        super.finish(); // Terminez l'Activity
    }

}
