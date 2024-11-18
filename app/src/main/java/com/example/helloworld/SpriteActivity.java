package com.example.helloworld;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class SpriteActivity extends AppCompatActivity {
    private ImageView playerBlue, playerRed, playerPurple;
    private TextView selectedCharacterText;
    private static final String PREFS_NAME = "GamePrefs";
    public String selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprite);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(SpriteActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Références aux vues
        playerBlue = findViewById(R.id.bleu);
        playerRed = findViewById(R.id.rouge);
        playerPurple = findViewById(R.id.purple);
        ImageView imageView = new ImageView(this);

        // Insert gif into imageview
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

        // Appliquer un clic pour chaque personnage
        playerBlue.setOnClickListener(view -> selectCharacter("Bleu", playerBlue));
        playerRed.setOnClickListener(view -> selectCharacter("Rouge", playerRed));
        playerPurple.setOnClickListener(view -> selectCharacter("Violet", playerPurple));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        Button start = findViewById(R.id.start);
        start.setOnClickListener(v -> {
            saveSelectedSprite(); // Enregistrer le sprite sélectionné
            if (selection != null) {
                Intent intent = new Intent(SpriteActivity.this, Labyrinthe_MA.class);
                startActivity(intent);
                Intent envoi = new Intent(SpriteActivity.this, Labyrinthe_MA.class);
                envoi.putExtra("selection_key", selection); // Envoi de la variable à ActivityB
                startActivity(envoi);
                overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_bottom);

            }
            else {
                Toast.makeText(this, "Aucun sprite selectionne", Toast.LENGTH_SHORT).show();
            }

        });
    }


    // Méthode pour enregistrer le sprite sélectionné dans SharedPreferences
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
        selectedCharacter.setBackgroundColor(Color.LTGRAY);

        // Mettre à jour le texte du personnage sélectionné
        selectedCharacterText.setText("Selected Character: " + characterName);
        selection = characterName;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
