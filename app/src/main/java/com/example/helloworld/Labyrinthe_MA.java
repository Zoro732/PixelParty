package com.example.helloworld;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        setContentView(R.layout.labyrinthe);

        // Créer une instance de GameView et l'ajouter au FrameLayout
        FrameLayout gameFrame = findViewById(R.id.gameFrame);
        labyrintheGameView = new Labyrinthe_GameView(this);
        gameFrame.addView(labyrintheGameView);

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
            labyrintheGameView.pauseGame();
            buttonResume.setVisibility(View.VISIBLE);
            buttonRestart.setVisibility(View.VISIBLE);
            buttonQuit.setVisibility(View.VISIBLE);
            pauseText.setVisibility(View.VISIBLE);
        });

        buttonResume.setOnClickListener(v -> {
            labyrintheGameView.resumeGame();
            buttonResume.setVisibility(View.GONE);
            buttonRestart.setVisibility(View.GONE);
            buttonQuit.setVisibility(View.GONE);
            pauseText.setVisibility(View.GONE);
        });
        buttonRestart.setOnClickListener(v -> {
            labyrintheGameView.restartGame();
            buttonResume.setVisibility(View.GONE);
            buttonRestart.setVisibility(View.GONE);
            buttonQuit.setVisibility(View.GONE);
            pauseText.setVisibility(View.GONE);
        });
        buttonQuit.setOnClickListener(v -> {
            labyrintheGameView.quitGame();
        });

            // Initialiser le gestionnaire de capteurs
            sensorManager =(SensorManager)

            getSystemService(Context.SENSOR_SERVICE);

            gyroscope =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        @Override
        protected void onResume () {
            super.onResume();
            // Enregistrer le listener du gyroscope
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
            }
            labyrintheGameView.resume();
        }

        @Override
        protected void onPause () {
            super.onPause();
            // Arrêter le listener du gyroscope
            sensorManager.unregisterListener(this);
            labyrintheGameView.pause();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void hideNavigationBar () {
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
        public void onSensorChanged (SensorEvent event){
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // Récupérer les valeurs du gyroscope
                float x = event.values[0]; // Rotation autour de l'axe X
                float y = event.values[1]; // Rotation autour de l'axe Y

                // Déplacer la boule en fonction des valeurs du gyroscope
                labyrintheGameView.moveBall(x, y); // Méthode à implémenter dans GameView
            }
        }

        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){
            // Ne rien faire
        }
    }
