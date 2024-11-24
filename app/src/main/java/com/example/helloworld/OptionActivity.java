package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;

public class OptionActivity extends AppCompatActivity {
    private FrameLayout gameFrame;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(R.layout.activity_option);

        Button backButton = findViewById(R.id.backButton_BoardSpriteSelection);
        gameFrame = findViewById(R.id.gameFrame);  // Initialiser le FrameLayout pour le changement de thème

        // Définir l'écouteur d'événements pour le bouton retour
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OptionActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
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
        buttonClair.setOnClickListener(v -> {
            gameFrame.setBackgroundColor(getResources().getColor(R.color.cyan));
            themeLabel.setTextColor(getResources().getColor(R.color.textCyan));
            languageLabel.setTextColor(getResources().getColor(R.color.textCyan));
            languageSpinner.setBackgroundColor(getResources().getColor(R.color.white)); // Fond du spinner
            ((TextView) languageSpinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.textCyan)); // Couleur du texte du spinner
        });

        // Changer le thème en sombre
        buttonSombre.setOnClickListener(v -> {
            gameFrame.setBackgroundColor(getResources().getColor(R.color.violet));
            themeLabel.setTextColor(getResources().getColor(R.color.textViolet));
            languageLabel.setTextColor(getResources().getColor(R.color.textViolet));
            languageSpinner.setBackgroundColor(getResources().getColor(R.color.violet)); // Fond du spinner
            ((TextView) languageSpinner.getChildAt(0)).setTextColor(getResources().getColor(R.color.textViolet)); // Couleur du texte du spinner
        });

        // Afficher un dialogue "À propos" lorsque le bouton est cliqué
        aboutButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(OptionActivity.this);
            builder.setTitle("À propos")
                    .setMessage("PIXEL PARTY\n\nApplication développée par AMAURY GIELEN, ILYES RABAOUY, et KACPER WOJTOWICZ.\n\nVersion 1.0\n\nDescription de l'application : Jeu de plateforme familiale")
                    .setPositiveButton("OK", null)
                    .show();
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
