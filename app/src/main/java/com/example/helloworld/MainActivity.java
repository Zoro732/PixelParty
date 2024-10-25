package com.example.helloworld;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameView gameView;
    private Vibrator vibrator;
    private SensorManager sensorManager;
    private Sensor gyroscope;

    long[] timings = {0, 100, 330, 100};  // Vibrer 200ms, pause 100ms, vibrer 300ms
    int[] amplitudes = {0, 100, 0, 255};  // Intensité correspondante (0 pour pas de vibration, 100 et 255 pour vibrer)

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(R.layout.activity_main);

        // Get instance of Vibrator from current Context
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Créer une instance de GameView et l'ajouter au FrameLayout
        FrameLayout gameFrame = findViewById(R.id.gameFrame);
        gameView = new GameView(this);
        gameFrame.addView(gameView);

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
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Arrêter le listener du gyroscope
        sensorManager.unregisterListener(this);
        gameView.pause();
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Récupérer les valeurs du gyroscope
            float x = event.values[0]; // Rotation autour de l'axe X
            float y = event.values[1]; // Rotation autour de l'axe Y

            // Déplacer la boule en fonction des valeurs du gyroscope
            gameView.moveBall(x, y); // Méthode à implémenter dans GameView
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne rien faire
    }
}
