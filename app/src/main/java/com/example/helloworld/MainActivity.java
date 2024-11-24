package com.example.helloworld;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String PREFS_NAME = "GamePrefs";

    // UI Elements
    private ImageView playerBlue, playerRed, playerPurple;
    private TextView selectedCharacterText;
    private String selection;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize UI Components
        initializeUI();
        // Set Background GIF
        setBackgroundGif();
        // Configure Button Click Listeners
        configureButtons();
        // Configure Character Selection
        configureCharacterSelection();
        // Initialize Spinner
        initializeSpinner();
        // Hide Navigation Bar
        hideNavigationBar();
    }

    private void initializeUI() {
        // Main Frame and Background
        FrameLayout frameLayout = findViewById(R.id.gameFrame);
        ImageView imageView = new ImageView(this);

        // UI Components
        playerBlue = findViewById(R.id.bleu);
        playerRed = findViewById(R.id.rouge);
        playerPurple = findViewById(R.id.purple);
        selectedCharacterText = findViewById(R.id.selected_character_text);

        // Buttons and Options
        ImageView iv_SpriteSelectionForBoard = findViewById(R.id.iv_SpriteSelectionForBoard);
        ImageView iv_MiniGames = findViewById(R.id.iv_MiniGames);
        ImageView backButtonSettings = findViewById(R.id.backButtonSettings);
        ImageView backButton_BoardSpriteSelection = findViewById(R.id.backButton_BoardSpriteSelection);
        ImageView startBoard = findViewById(R.id.start);
        ImageView iv_Settings = findViewById(R.id.iv_Settings);

        // Main Text
        TextView mainpage_text = findViewById(R.id.mainpage_text);

        // Load Background Image
        Glide.with(this)
                .asGif()
                .load(R.drawable.mainpage_background)
                .centerCrop()
                .override(imageView.getWidth(), imageView.getHeight())
                .into(imageView);
        frameLayout.addView(imageView);

        // Bring UI elements to the front
        mainpage_text.bringToFront();
        iv_SpriteSelectionForBoard.bringToFront();
        iv_MiniGames.bringToFront();
        startBoard.bringToFront();
        backButton_BoardSpriteSelection.bringToFront();
        backButtonSettings.bringToFront();
        iv_Settings.bringToFront();

    }

    private void setBackgroundGif() {
        // Load GIFs for characters
        Glide.with(this)
                .asGif()
                .load(R.drawable.player_blue_selection)
                .into(playerBlue);

        Glide.with(this)
                .asGif()
                .load(R.drawable.player_purple_selection)
                .into(playerPurple);

        Glide.with(this)
                .asGif()
                .load(R.drawable.player_red_selection)
                .into(playerRed);
    }

    private void configureButtons() {
        // Game Mode Selection
        findViewById(R.id.iv_SpriteSelectionForBoard).setOnClickListener(v -> showSpriteSelection());
        findViewById(R.id.start).setOnClickListener(v -> launchGame());
        findViewById(R.id.iv_MiniGames).setOnClickListener(v -> showMiniGames());
        findViewById(R.id.backButton_BoardSpriteSelection).setOnClickListener(v -> showMainMenu());
        findViewById(R.id.backButtonMiniGames).setOnClickListener(v -> showMainMenu());
        findViewById(R.id.iv_Settings).setOnClickListener(v -> showOptions());
        findViewById(R.id.aboutButton).setOnClickListener(v -> showAboutDialog());
        findViewById(R.id.backButtonSettings).setOnClickListener(v -> showMainMenu());

        // Mini Game Buttons
        findViewById(R.id.run).setOnClickListener(v -> launchActivity(RunGame_MA.class));
        findViewById(R.id.laby).setOnClickListener(v -> launchActivity(Labyrinthe_MA.class));
        findViewById(R.id.taquin).setOnClickListener(v -> launchActivity(Taquin_MA.class));
    }

    private void configureCharacterSelection() {
        playerBlue.setOnClickListener(v -> selectCharacter("Bleu", playerBlue));
        playerRed.setOnClickListener(v -> selectCharacter("Rouge", playerRed));
        playerPurple.setOnClickListener(v -> selectCharacter("Violet", playerPurple));
    }

    private void initializeSpinner() {
        Spinner languageSpinner = findViewById(R.id.language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void selectCharacter(String characterName, ImageView selectedCharacter) {
        // Reset Background
        playerBlue.setBackgroundColor(Color.TRANSPARENT);
        playerRed.setBackgroundColor(Color.TRANSPARENT);
        playerPurple.setBackgroundColor(Color.TRANSPARENT);

        // Highlight Selected Character
        if (selectedCharacter == playerBlue) {
            findViewById(R.id.animation_background1).setVisibility(View.VISIBLE);
            findViewById(R.id.animation_background2).setVisibility(View.GONE);
            findViewById(R.id.animation_background3).setVisibility(View.GONE);
        } else if (selectedCharacter == playerRed) {
            findViewById(R.id.animation_background1).setVisibility(View.GONE);
            findViewById(R.id.animation_background2).setVisibility(View.VISIBLE);
            findViewById(R.id.animation_background3).setVisibility(View.GONE);
        } else if (selectedCharacter == playerPurple) {
            findViewById(R.id.animation_background1).setVisibility(View.GONE);
            findViewById(R.id.animation_background2).setVisibility(View.GONE);
            findViewById(R.id.animation_background3).setVisibility(View.VISIBLE);
        }

        // Update Selected Character
        selectedCharacterText.setText(characterName);
        selection = characterName;
    }

    private void saveSelectedSprite() {
        if (selection != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Sprite", selection);
            editor.apply();
        }
    }

    private void showSpriteSelection() {
        findViewById(R.id.sprite).setVisibility(View.VISIBLE);
        findViewById(R.id.boardSpriteSelectionLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.optionFrame).setVisibility(View.GONE);
        findViewById(R.id.iv_Settings).setVisibility(View.GONE);

        // Disable Main Menu UI
        disableMainMenuUI();
    }

    private void disableMainMenuUI (){
        // Disable Main Menu UI
        findViewById(R.id.iv_SpriteSelectionForBoard).setVisibility(View.GONE);
        findViewById(R.id.iv_MiniGames).setVisibility(View.GONE);
        findViewById(R.id.mainpage_text).setVisibility(View.GONE);
        findViewById(R.id.iv_Settings).setVisibility(View.GONE);
    }

    private void showMiniGames() {
        // Show Mini Games buttons
        findViewById(R.id.taquin).setVisibility(View.VISIBLE);
        findViewById(R.id.run).setVisibility(View.VISIBLE);
        findViewById(R.id.laby).setVisibility(View.VISIBLE);
        findViewById(R.id.backButtonMiniGames).setVisibility(View.VISIBLE);

        // Disable Main Menu UI
        disableMainMenuUI();
    }

    private void showMainMenu() {
        // Enable Main Menu UI
        findViewById(R.id.iv_SpriteSelectionForBoard).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_MiniGames).setVisibility(View.VISIBLE);
        findViewById(R.id.mainpage_text).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_Settings).setVisibility(View.VISIBLE);

        // Disable SpriteSelection for Board Menu
        findViewById(R.id.sprite).setVisibility(View.GONE);
        findViewById(R.id.boardSpriteSelectionLayout).setVisibility(View.GONE);
        findViewById(R.id.optionFrame).setVisibility(View.GONE);

        // Disbale Mini Game UI
        findViewById(R.id.taquin).setVisibility(View.GONE);
        findViewById(R.id.run).setVisibility(View.GONE);
        findViewById(R.id.laby).setVisibility(View.GONE);
        findViewById(R.id.backButtonMiniGames).setVisibility(View.GONE);

        // Disable Settings UI
        findViewById(R.id.theme).setVisibility(View.GONE);
        findViewById(R.id.buttonSombre).setVisibility(View.GONE);
        findViewById(R.id.buttonClair).setVisibility(View.GONE);

    }

    private void showOptions() {
        findViewById(R.id.optionFrame).setVisibility(View.VISIBLE);
        findViewById(R.id.languageLabel).setVisibility(View.VISIBLE);
        findViewById(R.id.language).setVisibility(View.VISIBLE);
        findViewById(R.id.theme).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonClair).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonSombre).setVisibility(View.VISIBLE);
        findViewById(R.id.backButtonSettings).setVisibility(View.VISIBLE);

        findViewById(R.id.iv_Settings).setVisibility(View.GONE);

        //DIsable Main Menu UI
        disableMainMenuUI();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("À propos")
                .setMessage("PIXEL PARTY\n\nApplication développée par AMAURY GIELEN, ILYES RABAOUY, et KACPER WOJTOWICZ.\n\nVersion 1.0")
                .setPositiveButton("OK", null)
                .show();
    }

    private void launchGame() {
        saveSelectedSprite();
        if (selection != null) {
            Intent intent = new Intent(this, Board_MA.class);
            intent.putExtra("selection_key", selection);
            Log.d("MainActivity", "selection: " + selection);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aucun sprite sélectionné", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
