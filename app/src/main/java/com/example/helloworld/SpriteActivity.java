package com.example.helloworld;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SpriteActivity extends AppCompatActivity{
    private ImageView character1, character2, character3;
    private TextView selectedCharacterText;

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
        character1.setOnClickListener(view -> selectCharacter("Bleu", character1));
        character2.setOnClickListener(view -> selectCharacter("Rouge", character2));
        character3.setOnClickListener(view -> selectCharacter("Vert", character3));

        hideNavigationBar();
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
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
