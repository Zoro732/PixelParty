package com.example.helloworld;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    public String selection;
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
        // Références aux vues
        character1 = findViewById(R.id.bleu);
        character2 = findViewById(R.id.rouge);
        character3 = findViewById(R.id.vert);
        selectedCharacterText = findViewById(R.id.selected_character_text);

        // Appliquer un clic pour chaque personnage
        character1.setOnClickListener(view -> selectCharacter("bleu", character1));
        character2.setOnClickListener(view -> selectCharacter("rouge", character2));
        character3.setOnClickListener(view -> selectCharacter("vert", character3));

        hideNavigationBar();
        //Log.i("Sprite", selection);

        Button start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            saveSelectedSprite(); // Enregistrer le sprite sélectionné

                Intent intent = new Intent(SpriteActivity.this, LabyActivity.class);
                startActivity(intent);
                Intent envoi = new Intent(SpriteActivity.this, LabyActivity.class);
                envoi.putExtra("selection_key", selection); // Envoi de la variable à ActivityB
                startActivity(envoi);
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
            Toast.makeText(this, "Sprite selectionne enregistre : " + selection, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Aucun sprite sélectionne", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCharacter(String characterName, ImageView selectedCharacter) {
        // Réinitialiser l'arrière-plan pour tous les personnages
        character1.setBackgroundColor(Color.TRANSPARENT);
        character2.setBackgroundColor(Color.TRANSPARENT);
        character3.setBackgroundColor(Color.TRANSPARENT);

        // Mettre en surbrillance le personnage sélectionné
        selectedCharacter.setBackgroundColor(Color.LTGRAY);

        // Mettre à jour le texte du personnage sélectionné
        selectedCharacterText.setText("Selected Character: " + characterName);
        selection = characterName;
    }



    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
