package com.example.helloworld;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SpriteActivity extends AppCompatActivity{
    private ImageView character1, character2, character3;
    private TextView selectedCharacterText;
    private static final String PREFS_NAME = "GamePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprite);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpriteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // Enregistrer les chemins des sprites dans SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Enregistrer les chemins des images dans SharedPreferences
        editor.putString("sprite_1", "bleu");
        editor.putString("sprite_2", "rouge");
        editor.putString("sprite_3", "vert");
        editor.apply();  // Sauvegarder les données

        // Références aux vues
        /*character1 = findViewById(R.id.bleu);
        character2 = findViewById(R.id.rouge);
        character3 = findViewById(R.id.vert);
        selectedCharacterText = findViewById(R.id.selected_character_text);

        // Appliquer un clic pour chaque personnage
        character1.setOnClickListener(view -> selectCharacter("Bleu", character1));
        character2.setOnClickListener(view -> selectCharacter("Rouge", character2));
        character3.setOnClickListener(view -> selectCharacter("Vert", character3));
*/
        // Charger le sprite à partir des SharedPreferences
        loadSprite("sprite_1");
        hideNavigationBar();

        /*
        Button start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpriteActivity.this, LabyActivity.class);
                startActivity(intent);
            }
        });*/
    }

    // Charger le sprite à partir des SharedPreferences
    private void loadSprite(String Sprite) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Récupérer le chemin du sprite depuis SharedPreferences
        String spritePath = sharedPreferences.getString(Sprite, null);

        if (spritePath != null) {
            // Trouver le ressource correspondant au sprite (en supposant que tu utilises un sprite dans les ressources)
            int resId = getResources().getIdentifier(spritePath, "drawable", getPackageName());
            if (resId != 0) {
                Bitmap spriteBitmap = BitmapFactory.decodeResource(getResources(), resId);
                //ImageView imageView = findViewById(R.id.Sprite);
                //imageView.setImageBitmap(spriteBitmap); // Afficher le sprite
            } else {
                Toast.makeText(this, "Image non trouvée dans les ressources", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Sprite non trouvé", Toast.LENGTH_SHORT).show();
        }
    }

/*
    private void selectCharacter(String characterName, ImageView selectedCharacter) {
        // Réinitialiser l'arrière-plan pour tous les personnages
        character1.setBackgroundColor(Color.TRANSPARENT);
        character2.setBackgroundColor(Color.TRANSPARENT);
        character3.setBackgroundColor(Color.TRANSPARENT);

        // Mettre en surbrillance le personnage sélectionné
        selectedCharacter.setBackgroundColor(Color.LTGRAY);

        // Mettre à jour le texte du personnage sélectionné
        selectedCharacterText.setText("Selected Character: " + characterName);

    }*/



    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
