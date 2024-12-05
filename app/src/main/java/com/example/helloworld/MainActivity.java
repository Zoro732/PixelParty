package com.example.helloworld;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String PREFS_NAME = "GamePrefs";

    // UI Elements
    private ImageView playerBlue;
    private ImageView playerRed;
    private ImageView playerPurple;
    private ImageView ivTaquin;
    private ImageView ivLabyrinthe;
    private ImageView ivRunGame;
    private ImageView ivMole;
    private ImageView ivSoundOff;
    private ImageView ivSoundOn;
    private TextView selectedCharacterText;
    private String selection;


    private MediaPlayer mainTheme;

    private MediaPlayer toggleSoundButtonOn;
    private MediaPlayer toggleSoundButtonOff;

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
        // Hide Navigation Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }

        mainTheme = MediaPlayer.create(this, R.raw.mainactivity_maintheme);
        mainTheme.setLooping(true);
        mainTheme.setVolume(0.3f, 0.3f);
        if (SoundPreferences.isSoundEnabled(this)) {
            mainTheme.start();
        }

        toggleSoundButtonOn = MediaPlayer.create(this, R.raw.toggle_on_sound);
        toggleSoundButtonOff = MediaPlayer.create(this, R.raw.toggle_off_sound);
    }

    private void initializeUI() {
        // Initialize the background and layout
        FrameLayout frameLayout = findViewById(R.id.flMainPage);
        ImageView imageView = new ImageView(this);
        Glide.with(this)
                .asGif()
                .load(R.drawable.mainpage_background)
                .centerCrop()
                .into(imageView);
        frameLayout.addView(imageView);

        // Initialize UI components
        playerBlue = findViewById(R.id.ivCharacterBlue);
        playerRed = findViewById(R.id.ivCharacterRed);
        playerPurple = findViewById(R.id.ivCharacterPurple);
        ivTaquin = findViewById(R.id.ivTaquin);
        ivMole = findViewById(R.id.ivMole);
        ivLabyrinthe = findViewById(R.id.ivLabyrinth);
        ivRunGame = findViewById(R.id.ivRunGame);
        selectedCharacterText = findViewById(R.id.tvSelectedCharacterForBoard);

        ivSoundOn = findViewById(R.id.ivSoundOn);
        ivSoundOff = findViewById(R.id.ivSoundOff);

        // Initialize buttons
        Button[] buttons = {
                findViewById(R.id.btnLaunchSpriteSelectionForBoard),
                findViewById(R.id.btnLaunchMiniGamesMenu),
                findViewById(R.id.btnLaunchBoard),
                findViewById(R.id.btnBackMiniGames),
                findViewById(R.id.btnBackButtonBoardMenu),
                findViewById(R.id.btnAbout),
                findViewById(R.id.btnLaunchLabyrinth),
                findViewById(R.id.btnLaunchRunGame),
                findViewById(R.id.btnLaunchTaquin),
                findViewById(R.id.btnLaunchMole)
        };

        // Apply background and bring buttons to the front
        for (Button btn : buttons) {
            btn.setBackgroundResource(R.drawable.button_background_img);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setBackgroundTintList(null);
            }
            btn.bringToFront();  // Bring buttons to the front
        }
        ivSoundOff.bringToFront();
        ivSoundOn.bringToFront();
        // Main text
        TextView mainpageText = findViewById(R.id.tvMainPageTitle);
        mainpageText.bringToFront();  // Bring main text to the front
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

        Glide.with(this)
                .asGif()
                .load(R.drawable.key_gif_labyrinth)
                .into(ivLabyrinthe);

        Glide.with(this)
                .asGif()
                .load(R.drawable.coin_gif_rungame)
                .into(ivRunGame);

        Glide.with(this)
                .asGif()
                .load(R.drawable.taquin)
                .into(ivTaquin);

        Glide.with(this)
                .asGif()
                .load(R.drawable.mole_monster)
                .into(ivMole);

    }

    private void configureButtons() {

        // In your button click listeners:
        findViewById(R.id.ivSoundOn).setOnClickListener(v -> {
            ivSoundOff.setVisibility(View.VISIBLE);
            ivSoundOn.setVisibility(View.GONE);
            ivSoundOff.bringToFront();
            toggleSoundButtonOff.start();
            SoundPreferences.setSoundEnabled(this, false);
            mainTheme.pause();
        });
        findViewById(R.id.ivSoundOff).setOnClickListener(v -> {
            ivSoundOff.setVisibility(View.GONE);
            ivSoundOn.setVisibility(View.VISIBLE);
            ivSoundOn.bringToFront();
            toggleSoundButtonOn.start();

            SoundPreferences.setSoundEnabled(this, true);
            mainTheme.start();

        });

        if (SoundPreferences.isSoundEnabled(this)) {
            ivSoundOn.setVisibility(View.VISIBLE);
            ivSoundOff.setVisibility(View.GONE);
        } else {
            ivSoundOn.setVisibility(View.GONE);
            ivSoundOff.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnLaunchSpriteSelectionForBoard).setOnClickListener(v -> {
            showSpriteSelection();
            playSoundEffect(R.raw.button_clik);
        });
        findViewById(R.id.btnLaunchBoard).setOnClickListener(v -> {
            launchBoard();
            if (selection == null) {
                playSoundEffect(R.raw.error);
            } else {
                playSoundEffect(R.raw.startgame);
            }
        });
        findViewById(R.id.btnLaunchMiniGamesMenu).setOnClickListener(v -> {
            showMiniGames();
            playSoundEffect(R.raw.button_clik);
        });
        findViewById(R.id.btnBackButtonBoardMenu).setOnClickListener(v -> {
            showMainMenu();
            playSoundEffect(R.raw.button_clik);
        });
        findViewById(R.id.btnBackMiniGames).setOnClickListener(v -> {
            showMainMenu();
            playSoundEffect(R.raw.button_clik);
        });
        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            showAboutDialog();
            playSoundEffect(R.raw.button_clik);
        });
        findViewById(R.id.btnLaunchTaquin).setOnClickListener(v -> {
            launchActivity(Taquin_MA.class);
            playSoundEffect(R.raw.startgame);
        });
        findViewById(R.id.btnLaunchLabyrinth).setOnClickListener(v -> {
            launchActivity(Labyrinthe_MA.class);
            playSoundEffect(R.raw.startgame);
        });
        findViewById(R.id.btnLaunchRunGame).setOnClickListener(v -> {
            launchActivity(RunGame_MA.class);
            playSoundEffect(R.raw.startgame);
        });
        findViewById(R.id.btnLaunchMole).setOnClickListener(v -> {
            launchActivity(Boss_MA.class);
            playSoundEffect(R.raw.startgame);
        });

        ivLabyrinthe.setOnClickListener(v -> {
            launchActivity(Labyrinthe_MA.class);
            playSoundEffect(R.raw.startgame);
        });
        ivRunGame.setOnClickListener(v -> {
            launchActivity(RunGame_MA.class);
            playSoundEffect(R.raw.startgame);
        });
        ivTaquin.setOnClickListener(v -> {
            launchActivity(Taquin_MA.class);
            playSoundEffect(R.raw.startgame);
        });

    }

    private void playSoundEffect(int soundResourceId) {
        if (!SoundPreferences.isSoundEnabled(this)) {
            return;
        }
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    private void configureCharacterSelection() {
        playerBlue.setOnClickListener(v -> selectCharacter("Blue", playerBlue));
        playerRed.setOnClickListener(v -> selectCharacter("Red", playerRed));
        playerPurple.setOnClickListener(v -> selectCharacter("Purple", playerPurple));
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |          // Masque la barre de navigation
                        View.SYSTEM_UI_FLAG_FULLSCREEN |              // Masque la barre d'état
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |        // Permet d'éviter que les barres réapparaissent avec les gestes
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |           // Assure que le contenu ne change pas de taille quand les barres sont masquées
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | // Cache la barre de navigation sans décaler l'interface
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN        // Cache la barre d'état sans décaler l'interface
        );
    }

    private void selectCharacter(String characterName, ImageView selectedCharacter) {
        // Reset Background
        playerBlue.setBackgroundColor(Color.TRANSPARENT);
        playerRed.setBackgroundColor(Color.TRANSPARENT);
        playerPurple.setBackgroundColor(Color.TRANSPARENT);

        // Highlight Selected Character
        if (selectedCharacter == playerBlue) {
            findViewById(R.id.ivSpriteSelectionSelectEffectForBlue).setVisibility(View.VISIBLE);
            findViewById(R.id.ivSpriteSelectionSelectEffectForRed).setVisibility(View.GONE);
            findViewById(R.id.ivSpriteSelectionSelectEffectForPurple).setVisibility(View.GONE);
            playSoundEffect(R.raw.spriteselection_select);
        } else if (selectedCharacter == playerRed) {
            findViewById(R.id.ivSpriteSelectionSelectEffectForBlue).setVisibility(View.GONE);
            findViewById(R.id.ivSpriteSelectionSelectEffectForRed).setVisibility(View.VISIBLE);
            findViewById(R.id.ivSpriteSelectionSelectEffectForPurple).setVisibility(View.GONE);
            playSoundEffect(R.raw.spriteselection_select);
        } else if (selectedCharacter == playerPurple) {
            findViewById(R.id.ivSpriteSelectionSelectEffectForBlue).setVisibility(View.GONE);
            findViewById(R.id.ivSpriteSelectionSelectEffectForRed).setVisibility(View.GONE);
            findViewById(R.id.ivSpriteSelectionSelectEffectForPurple).setVisibility(View.VISIBLE);
            playSoundEffect(R.raw.spriteselection_select);
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
        findViewById(R.id.llSpriteSelectionForBoard).setVisibility(View.VISIBLE);
        findViewById(R.id.llBoardMenu).setVisibility(View.VISIBLE);

        // Disable Main Menu UI
        disableMainMenuUI();
    }

    private void disableMainMenuUI() {
        // Disable Main Menu UI
        findViewById(R.id.btnLaunchSpriteSelectionForBoard).setVisibility(View.GONE);
        findViewById(R.id.btnLaunchMiniGamesMenu).setVisibility(View.GONE);
        findViewById(R.id.tvMainPageTitle).setVisibility(View.GONE);
        findViewById(R.id.btnAbout).setVisibility(View.GONE);
        findViewById(R.id.ivSoundOn).setVisibility(View.GONE);
        findViewById(R.id.ivSoundOff).setVisibility(View.GONE);
    }

    private void showMiniGames() {
        // Show Mini Games buttons
        findViewById(R.id.btnLaunchTaquin).setVisibility(View.VISIBLE);
        findViewById(R.id.btnLaunchRunGame).setVisibility(View.VISIBLE);
        findViewById(R.id.btnLaunchLabyrinth).setVisibility(View.VISIBLE);
        findViewById(R.id.btnBackMiniGames).setVisibility(View.VISIBLE);
        findViewById(R.id.btnLaunchMole).setVisibility(View.VISIBLE);

        // Enable textview
        findViewById(R.id.tvSelectGame).setVisibility(View.VISIBLE);

        // Enable gif
        ivLabyrinthe.setVisibility(View.VISIBLE);
        ivRunGame.setVisibility(View.VISIBLE);
        ivTaquin.setVisibility(View.VISIBLE);
        ivMole.setVisibility(View.VISIBLE);

        // Disable Main Menu UI
        disableMainMenuUI();
    }

    private void showMainMenu() {
        // Enable Main Menu UI
        findViewById(R.id.btnLaunchSpriteSelectionForBoard).setVisibility(View.VISIBLE);
        findViewById(R.id.btnLaunchMiniGamesMenu).setVisibility(View.VISIBLE);
        findViewById(R.id.tvMainPageTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.btnAbout).setVisibility(View.VISIBLE);

        if (!SoundPreferences.isSoundEnabled(this)) {
            findViewById(R.id.ivSoundOff).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ivSoundOn).setVisibility(View.VISIBLE);
        }

        // Disable SpriteSelection for Board Menu
        findViewById(R.id.llSpriteSelectionForBoard).setVisibility(View.GONE);
        findViewById(R.id.llBoardMenu).setVisibility(View.GONE);

        // Disable Mini Game UI
        findViewById(R.id.btnLaunchTaquin).setVisibility(View.GONE);
        findViewById(R.id.btnLaunchRunGame).setVisibility(View.GONE);
        findViewById(R.id.btnLaunchLabyrinth).setVisibility(View.GONE);
        findViewById(R.id.btnBackMiniGames).setVisibility(View.GONE);
        findViewById(R.id.btnLaunchMole).setVisibility(View.GONE);

        // Disable gif
        ivLabyrinthe.setVisibility(View.GONE);
        ivRunGame.setVisibility(View.GONE);
        ivTaquin.setVisibility(View.GONE);
        ivMole.setVisibility(View.GONE);

        // Disable TextViex of MiniGames
        findViewById(R.id.tvSelectGame).setVisibility(View.GONE);

    }


    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("PIXEL PARTY\n\nApplication developped by AMAURY GIELEN, ILYES RABAOUY, & KACPER WOJTOWICZ.\n\nVersion 5.0")
                .setPositiveButton("OK", null)
                .show();

    }

    private void launchBoard() {
        saveSelectedSprite();
        if (selection != null) {
            Intent intent = new Intent(this, Board_MA.class);
            intent.putExtra("selection_key", selection);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aucun personnage sélectionné", Toast.LENGTH_SHORT).show();
        }

    }

    private void launchActivity(Class<?> activityClass) {
        mainTheme.pause();
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("selection_key", "Bleu");
        intent.putExtra("game_mode", "minigames");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainTheme != null && SoundPreferences.isSoundEnabled(this) && !mainTheme.isPlaying()) {
            mainTheme.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mainTheme != null && mainTheme.isPlaying()) {
            mainTheme.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainTheme != null) {
            mainTheme.release();
            mainTheme = null;
        }
    }

}
