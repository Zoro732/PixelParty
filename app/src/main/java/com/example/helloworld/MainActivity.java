package com.example.helloworld;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private Button changeSpriteButton;
    private Vibrator vibrator;
    long[] timings = {0, 100, 330, 100};  // Vibrer 200ms, pause 100ms, vibrer 300ms
    int[] amplitudes = {0, 100, 0, 255};  // Intensité correspondante (0 pour pas de vibration, 100 et 255 pour vibrer)

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button gameMode_Solo = findViewById(R.id.gameMode_Solo);
        gameMode_Solo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SoloActivity.class);
                startActivity(intent);
            }
        });


        hideNavigationBar();

        // Get instance of Vibrator from current Context
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Créer une instance de GameView et l'ajouter au FrameLayout
        FrameLayout gameFrame = findViewById(R.id.gameFrame);
        gameView = new GameView(this);
        gameFrame.addView(gameView);


        //VibrationEffect vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, -1);

        // Définir l'écouteur d'événements pour changer le spritesheet
        /*changeSpriteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Changer le spritesheet quand le bouton est pressé
                    gameView.changeSpriteSheet(R.drawable.attack);  // Remplacer par ton nouveau spritesheet
                    // Vibrate for 300 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(timings,0);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Restaurer le spritesheet original quand le bouton est relâché
                    gameView.changeSpriteSheet(R.drawable.idle);  // Remplacer par le spritesheet original
                    vibrator.cancel();
                }
                return true;
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
