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
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = findViewById(R.id.gameFrame);
        ImageView imageView = new ImageView(this);

        Glide.with(this)
                .asGif()
                .load(R.drawable.mainpage_background)
                .centerCrop()
                .into(imageView);
        frameLayout.addView(imageView);

        TextView mainpage_text = findViewById(R.id.mainpage_text);
        mainpage_text.bringToFront();



        Button gameMode_Solo = findViewById(R.id.gameMode_Solo);
        gameMode_Solo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SpriteActivity.class);
            startActivity(intent);
        });

        Button mini_jeux = findViewById(R.id.mini_jeux);
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
