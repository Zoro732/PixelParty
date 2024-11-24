package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
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

        ImageView gameMode_Solo = findViewById(R.id.gameMode_Solo);
        ImageView mini_jeux = findViewById(R.id.mini_jeux);
        ImageView backButton = findViewById(R.id.backbutton);
        ImageView back = findViewById(R.id.back);
        ImageView start = findViewById(R.id.start);
        Button backminijeux = findViewById(R.id.backminijeux);
        Button laby = findViewById(R.id.laby);
        Button run = findViewById(R.id.run);
        Button taquin = findViewById(R.id.taquin);
        Button buttonClair = findViewById(R.id.buttonClair);
        Button buttonSombre = findViewById(R.id.buttonSombre);
        ImageView aboutButton = findViewById(R.id.aboutButton);
        TextView themeLabel = findViewById(R.id.theme);
        TextView languageLabel = findViewById(R.id.languageLabel);
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
        gameMode_Solo.bringToFront();
        mini_jeux.bringToFront();
        start.bringToFront();
        back.bringToFront();
        backButton.bringToFront();

        // Bouton start
        gameMode_Solo.setOnClickListener(v -> { // temporarely replace SpriteAcitivity with Board_MA
            gameMode_Solo.setVisibility(View.GONE);
            mini_jeux.setVisibility(View.GONE);
            mainpage_text.setVisibility(View.GONE);
            sprite.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);

        });

        // Chargement des images GIF
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

        selectedCharacterText = findViewById(R.id.selected_character_text);

        playerBlue.setOnClickListener(view -> selectCharacter("Bleu", playerBlue));
        playerRed.setOnClickListener(view -> selectCharacter("Rouge", playerRed));
        playerPurple.setOnClickListener(view -> selectCharacter("Violet", playerPurple));

        // Lancement de la partie
        start.setOnClickListener(v -> {
            saveSelectedSprite(); // Enregistrer le sprite sélectionné
            if (selection != null) {
                intent = new Intent(this, Board_MA.class);
                intent.putExtra("selection_key", selection); // Envoi de la variable
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Aucun sprite selectionne", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> {
            gameMode_Solo.setVisibility(View.VISIBLE);
            mini_jeux.setVisibility(View.VISIBLE);
            mainpage_text.setVisibility(View.VISIBLE);
            sprite.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
        });

        // Bouton Mini jeux
        mini_jeux.setOnClickListener(v -> {
            gameMode_Solo.setVisibility(View.GONE);
            mini_jeux.setVisibility(View.GONE);
            mainpage_text.setVisibility(View.GONE);
            taquin.setVisibility(View.VISIBLE);
            run.setVisibility(View.VISIBLE);
            laby.setVisibility(View.VISIBLE);
            backminijeux.setVisibility(View.VISIBLE);
            settings.setVisibility(View.GONE);

        });

        backminijeux.setOnClickListener(v -> {
            gameMode_Solo.setVisibility(View.VISIBLE);
            mini_jeux.setVisibility(View.VISIBLE);
            mainpage_text.setVisibility(View.VISIBLE);
            sprite.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            option.setVisibility(View.GONE);
            settings.setVisibility(View.VISIBLE);
            taquin.setVisibility(View.GONE);
            run.setVisibility(View.GONE);
            laby.setVisibility(View.GONE);
            backminijeux.setVisibility(View.GONE);
        });

        run.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RunGame_MA.class);
            startActivity(intent);
        });

        laby.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Labyrinthe_MA.class);
            startActivity(intent);

        });

        taquin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Taquin_MA.class);
            startActivity(intent);
        });

        // Bouton option
        settings.bringToFront();

        settings.setOnClickListener(v -> {
            gameMode_Solo.setVisibility(View.GONE);
            mini_jeux.setVisibility(View.GONE);
            mainpage_text.setVisibility(View.GONE);
            sprite.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            option.setVisibility(View.VISIBLE);
            settings.setVisibility(View.GONE);
        });



        // Initialiser le Spinner
        Spinner languageSpinner = findViewById(R.id.language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Configurer les boutons pour le changement de thème


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

    @SuppressLint("SetTextI18n")
    private void selectCharacter(String characterName, ImageView selectedCharacter) {
        // Réinitialiser l'arrière-plan pour tous les personnages
        playerBlue.setBackgroundColor(Color.TRANSPARENT);
        playerRed.setBackgroundColor(Color.TRANSPARENT);
        playerPurple.setBackgroundColor(Color.TRANSPARENT);

        // Mettre en surbrillance le personnage sélectionné
        ImageView animationBackground1 = findViewById(R.id.animation_background1);
        ImageView animationBackground2 = findViewById(R.id.animation_background2);
        ImageView animationBackground3 = findViewById(R.id.animation_background3);

        if (selectedCharacter == playerBlue) {
            animationBackground1.setVisibility(View.VISIBLE);
            animationBackground2.setVisibility(View.GONE);
            animationBackground3.setVisibility(View.GONE);
        }

        if (selectedCharacter == playerRed) {
            animationBackground1.setVisibility(View.GONE);
            animationBackground2.setVisibility(View.VISIBLE);
            animationBackground3.setVisibility(View.GONE);
        }

        if (selectedCharacter == playerPurple) {
            animationBackground1.setVisibility(View.GONE);
            animationBackground2.setVisibility(View.GONE);
            animationBackground3.setVisibility(View.VISIBLE);
        }

        // Mettre à jour le texte du personnage sélectionné
        selectedCharacterText.setText(characterName);
        selection = characterName;
    }
}
