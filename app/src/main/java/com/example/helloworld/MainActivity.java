package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private ImageButton BackButton;
    private Vibrator vibrator;
    private boolean hasVibrated = false; // Booléen pour vérifier si la vibration a déjà eu lieu
    long[] timings = {0, 100};  // Vibre pendant 100ms, puis s'arrête

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(R.layout.activity_main);

        // Obtenir l'instance de Vibrator à partir du contexte actuel
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Associer le bouton
        BackButton = findViewById(R.id.back);

        // Définir l'écouteur d'événements pour le bouton
        BackButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Vérifiez si la vibration n'a pas déjà eu lieu
                    if (!hasVibrated) {
                        // Vibration
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(timings, -1); // Utiliser -1 pour une vibration unique
                        }
                        hasVibrated = true; // Marquez que la vibration a eu lieu
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Réinitialiser le booléen lorsque le bouton est relâché
                    hasVibrated = false;
                }
                return true;
            }
        });

        // Initialiser le Spinner
        Spinner languageSpinner = findViewById(R.id.language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
