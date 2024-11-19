package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.content.Intent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ImageView playerBlue, playerRed, playerPurple;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FrameLayout frameLayout = findViewById(R.id.gameFrame);
        ImageView imageView = new ImageView(this);

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

        LinearLayout spriteLayout = findViewById(R.id.sprite);
        LinearLayout boutonSprite = findViewById(R.id.button);
        Button mini_jeux = findViewById(R.id.mini_jeux);
        Button gameMode_Solo = findViewById(R.id.gameMode_Solo);
        gameMode_Solo.setOnClickListener(view -> {
//            gameMode_Solo.setVisibility(View.GONE);
//            mini_jeux.setVisibility(View.GONE);
//            mainpage_text.setVisibility(View.GONE);
//            spriteLayout.setVisibility(View.VISIBLE);
//            boutonSprite.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, SpriteActivity.class);
            startActivity(intent);
        });

        Button start = findViewById(R.id.start);
        start.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Taquin_MA.class);
            startActivity(intent);
        });

        mini_jeux.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MiniGames_MA.class);
            startActivity(intent);
        });
        ImageView settings = findViewById(R.id.settings);
        settings.bringToFront();
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OptionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
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
}
