package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ImageView playerBlue, playerRed, playerPurple;
    private static final String PREFS_NAME = "GamePrefs";
    public String selection;
    private Intent intent;
    private TextView selectedCharacterText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = findViewById(R.id.gameFrame);
        ImageView imageView = new ImageView(this);

        Button gameMode_Solo = findViewById(R.id.gameMode_Solo);
        Button mini_jeux = findViewById(R.id.mini_jeux);
        Button backButton = findViewById(R.id.backbutton);
        Button back = findViewById(R.id.back);
        ImageView settings = findViewById(R.id.settings);

        LinearLayout sprite = findViewById(R.id.sprite);
        LinearLayout button = findViewById(R.id.button);
        FrameLayout option = findViewById(R.id.optionFrame);
        playerBlue = findViewById(R.id.bleu);
        playerRed = findViewById(R.id.rouge);
        playerPurple = findViewById(R.id.purple);


        Glide.with(this)
                .asGif()
                .load(R.drawable.mainpage_background)
                .centerCrop()
                .into(imageView);
        frameLayout.addView(imageView);

        TextView mainpage_text = findViewById(R.id.mainpage_text);
        mainpage_text.bringToFront();

        // Apparition de la selection de sprite avant de commencer
        gameMode_Solo.setOnClickListener(v -> { // temporarely replace SpriteAcitivity with Board_MA
            //Intent intent = new Intent(MainActivity.this, SpriteActivity.class);
            //startActivity(intent);
            gameMode_Solo.setVisibility(View.GONE);
            mini_jeux.setVisibility(View.GONE);
            mainpage_text.setVisibility(View.GONE);
            sprite.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
        });

        Glide.with(this)
                .asGif()
                .load(R.drawable.player_blue_selection) // Remplacez par votre fichier GIF
                .into(playerBlue);
        Glide.with(this)
                .asGif()
                .load(R.drawable.player_purple_selection) // Remplacez par votre fichier GIF
                .into(playerPurple);
        Glide.with(this)
                .asGif()
                .load(R.drawable.player_red_selection) // Remplacez par votre fichier GIF
                .into(playerRed);

        // Apparition du choix de sprite avant le menu de minijeux
        mini_jeux.setOnClickListener(v -> {
            //Intent intent = new Intent(MainActivity.this, MiniGames_MA.class);
            //startActivity(intent);
            gameMode_Solo.setVisibility(View.GONE);
            mini_jeux.setVisibility(View.GONE);
            mainpage_text.setVisibility(View.GONE);
            sprite.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        });


        settings.bringToFront();

        settings.setOnClickListener(v -> {
            //Intent intent = new Intent(MainActivity.this, OptionActivity.class);
            //startActivity(intent);
            gameMode_Solo.setVisibility(View.GONE);
            mini_jeux.setVisibility(View.GONE);
            mainpage_text.setVisibility(View.GONE);
            sprite.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            option.setVisibility(View.VISIBLE);
            settings.setVisibility(View.GONE);
        });

        selectedCharacterText = findViewById(R.id.selected_character_text);

        playerBlue.setOnClickListener(view -> selectCharacter("Bleu", playerBlue));
        playerRed.setOnClickListener(view -> selectCharacter("Rouge", playerRed));
        playerPurple.setOnClickListener(view -> selectCharacter("Violet", playerPurple));

        // Lancement de la partie
        Button start = findViewById(R.id.start);
        start.setOnClickListener(v -> {
            saveSelectedSprite(); // Enregistrer le sprite sélectionné
            if (selection != null) {
                intent = new Intent(MainActivity.this, Labyrinthe_MA.class);
                intent.putExtra("selection_key", selection); // Envoi de la variable
                //intent.putExtra("game_mode", game_mode); // Envoi d'une autre valeur
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Aucun sprite selectionne", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(MainActivity.this, Board_MA.class);
            startActivity(intent);
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

        // Afficher un dialogue "À propos" lorsque le bouton est cliqué
        aboutButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("À propos")
                    .setMessage("PIXEL PARTY\n\nApplication développée par AMAURY GIELEN, ILYES RABAOUY, et KACPER WOJTOWICZ.\n\nVersion 1.0\n\nDescription de l'application : Jeu de plateforme familiale")
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Définir l'écouteur d'événements pour le bouton retour
        backButton.setOnClickListener(v -> {
            //Intent intent = new Intent(OptionActivity.this, MainActivity.class);
            //startActivity(intent);
            gameMode_Solo.setVisibility(View.VISIBLE);
            mini_jeux.setVisibility(View.VISIBLE);
            mainpage_text.setVisibility(View.VISIBLE);
            sprite.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        });
        // Définir l'écouteur d'événements pour le bouton retour
        back.setOnClickListener(v -> {
            //Intent intent = new Intent(OptionActivity.this, MainActivity.class);
            //startActivity(intent);
            gameMode_Solo.setVisibility(View.VISIBLE);
            mini_jeux.setVisibility(View.VISIBLE);
            mainpage_text.setVisibility(View.VISIBLE);
            sprite.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        });
        hideNavigationBar();

    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
    private void saveSelectedSprite() {
        if (selection != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Sprite", selection); // Enregistrer le nom du sprite
            editor.apply();
        }
    }

    private void selectCharacter(String characterName, ImageView selectedCharacter) {
        // Réinitialiser l'arrière-plan pour tous les personnages
        playerBlue.setBackgroundColor(Color.TRANSPARENT);
        playerRed.setBackgroundColor(Color.TRANSPARENT);
        playerPurple.setBackgroundColor(Color.TRANSPARENT);

        // Mettre en surbrillance le personnage sélectionné
        selectedCharacter.setBackgroundColor(Color.LTGRAY);

        // Mettre à jour le texte du personnage sélectionné
        selectedCharacterText.setText("Selected Character: " + characterName);
        selection = characterName;
    }
}
