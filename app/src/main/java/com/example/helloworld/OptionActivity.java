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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;

public class OptionActivity extends AppCompatActivity {
    private Button backButton; // Changement ici
    private Vibrator vibrator;
    private boolean hasVibrated = false; // Booléen pour vérifier si la vibration a déjà eu lieu
    long[] timings = {0, 100};  // Vibre pendant 100ms, puis s'arrête
    private FrameLayout gameFrame;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(R.layout.activity_option);

        // Obtenir l'instance de Vibrator à partir du contexte actuel
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Associer le bouton de retour
        backButton = findViewById(R.id.back);
        gameFrame = findViewById(R.id.gameFrame);  // Initialiser le FrameLayout pour le changement de thème

        // Définir l'écouteur d'événements pour le bouton retour
        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!hasVibrated) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(timings, -1);
                        }
                        hasVibrated = true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
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

        // Configurer les boutons pour le changement de thème
        Button buttonClair = findViewById(R.id.buttonClair);
        Button buttonSombre = findViewById(R.id.buttonSombre);
        Button aboutButton = findViewById(R.id.aboutButton); // Nouveau bouton
        TextView themeLabel = findViewById(R.id.theme);
        TextView languageLabel = findViewById(R.id.languageLabel);

        // Changer le thème en clair
        buttonClair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFrame.setBackgroundColor(getResources().getColor(R.color.cyan));
                themeLabel.setTextColor(getResources().getColor(R.color.textCyan));
                languageLabel.setTextColor(getResources().getColor(R.color.textCyan));
                languageSpinner.setBackgroundColor(getResources().getColor(R.color.white)); // Fond du spinner
                ((TextView) languageSpinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.textCyan)); // Couleur du texte du spinner
            }
        });

        // Changer le thème en sombre
        buttonSombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFrame.setBackgroundColor(getResources().getColor(R.color.violet));
                themeLabel.setTextColor(getResources().getColor(R.color.textViolet));
                languageLabel.setTextColor(getResources().getColor(R.color.textViolet));
                languageSpinner.setBackgroundColor(getResources().getColor(R.color.violet)); // Fond du spinner
                ((TextView) languageSpinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.textViolet)); // Couleur du texte du spinner
            }
        });

        // Afficher un dialogue "À propos" lorsque le bouton est cliqué
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OptionActivity.this);
                builder.setTitle("À propos")
                        .setMessage("PIXEL PART\n\nApplication développée par AMAURY GIELEN, ILYES RABAOUY, et KACPER WOJTOWIC.\n\nVersion 1.0\n\nDescription de l'application : Jeu de plateforme familiale")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
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
