package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Locale;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private FrameLayout gameFrame;
    private TextView themeLabel;
    private TextView languageLabel;
    private Button buttonClair;
    private Button buttonSombre;
    private Button aboutButton;
    private Button backButton; // Déclaration du bouton retour

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Charger la langue enregistrée
        loadLocale();

        hideNavigationBar();
        setContentView(R.layout.activity_main);

        gameFrame = findViewById(R.id.gameFrame);
        themeLabel = findViewById(R.id.theme);
        languageLabel = findViewById(R.id.languageLabel);
        buttonClair = findViewById(R.id.buttonClair);
        buttonSombre = findViewById(R.id.buttonSombre);
        aboutButton = findViewById(R.id.aboutButton);
        backButton = findViewById(R.id.backButton); // Initialisation du bouton retour

        // Initialiser le Spinner
        Spinner languageSpinner = findViewById(R.id.language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Sélectionner la langue actuelle dans le Spinner
        languageSpinner.setSelection(getCurrentLanguagePosition());

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = position == 0 ? "fr" : "en"; // "fr" pour Français, "en" pour Anglais
                setLocale(language);
                updateTexts(); // Mettre à jour les textes après le changement de langue
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurer les boutons pour le changement de thème
        setupThemeButtons();
        setupAboutButton();

        // Configurer le bouton retour
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Retour à l'écran précédent", Toast.LENGTH_SHORT).show();
                // Vous pouvez également faire d'autres actions ici, sans fermer l'application
            }
        });

    }

    private void setupThemeButtons() {
        // Changer le thème en clair
        buttonClair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFrame.setBackgroundColor(getResources().getColor(R.color.cyan));
                themeLabel.setTextColor(getResources().getColor(R.color.textCyan));
                languageLabel.setTextColor(getResources().getColor(R.color.textCyan));
            }
        });

        // Changer le thème en sombre
        buttonSombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameFrame.setBackgroundColor(getResources().getColor(R.color.violet));
                themeLabel.setTextColor(getResources().getColor(R.color.textViolet));
                languageLabel.setTextColor(getResources().getColor(R.color.textViolet));
            }
        });
    }

    private void setupAboutButton() {
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("À propos")
                        .setMessage("PIXEL PARTY\n\nApplication développée par AMAURY GIELEN, ILYES RABAOUY, et KACPER WOJTOWICZ.\n\nVersion 1.0\n\nDescription de l'application : Jeu de plateforme familiale")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    // Enregistrer la langue et recharger l'activité
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        }
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    // Charger la langue enregistrée
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "fr");
        setLocale(language);
    }

    // Positionner la langue actuelle dans le Spinner
    private int getCurrentLanguagePosition() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "fr");
        return language.equals("fr") ? 0 : 1;
    }

    // Mettre à jour les textes en fonction de la langue
    private void updateTexts() {
        themeLabel.setText(getString(R.string.theme_label));
        languageLabel.setText(getString(R.string.language_label));
        buttonClair.setText(getString(R.string.button_clair));
        buttonSombre.setText(getString(R.string.button_sombre));
        aboutButton.setText(getString(R.string.button_about));
        backButton.setText(getString(R.string.backButton));
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
