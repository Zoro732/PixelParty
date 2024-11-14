package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private BoardView boardView; // Déclaration de la vue BoardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();  // Masquer la barre de navigation
        }

        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.boardView);  // 'this' refers to the MainActivity context

        Button moveButton = findViewById(R.id.moveButton);
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exemple de déplacement du joueur
                boardView.movePlayer(); // Méthode que vous définissez pour déplacer le joueur
            }
        });
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
