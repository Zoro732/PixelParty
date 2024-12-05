package com.example.helloworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class Boss_MA extends AppCompatActivity {
    private TextView tvScore, tvTimer, tvCountdown, tvPause;
    private int score = 0, gameDuration = 45;
    private long startTime, remainingTime;
    private boolean isPaused = false, isGameOver = false, countdownFinished = false;

    private final Handler handler = new Handler();
    private final SecureRandom random = new SecureRandom();
    private ImageView[] moles;
    private int screenHeight;

    private Vibrator vibrator;
    private MediaPlayer sfx;
    private MediaPlayer maintheme;

    private boolean[] moleTouched;
    private final float INITIAL_PROBABILITY = 0.2f;
    private final float PROBABILITY_INCREMENT = 0.03f;
    private float currentProbability = INITIAL_PROBABILITY;

    private LinearLayout llPauseMenu;
    private Button btnResume, btnRestart, btnQuit;
    private final Set<Integer> activeMoles = new HashSet<>();

    private final int[][] positions = new int[5 * 3][2];

    private int frameWidth, frameHeight;

    private FrameLayout flGame;

    private String game_mode;

    private ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mole);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        setButtonBackground();
        initializePauseMenu();

        // Get intent info
        game_mode = getIntent().getStringExtra("game_mode");

        // Handle game_mode intent
        if (game_mode.equals("board")) {
            findViewById(R.id.ivSettings).setVisibility(View.GONE);
            Log.d("game_mode", "board");
        }

        flGame = findViewById(R.id.flGame);
        flGame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flGame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                frameWidth = flGame.getWidth();
                frameHeight = flGame.getHeight();
            }
        });

        tvCountdown = findViewById(R.id.tvCountdown);
        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);

        moles = new ImageView[]{
                findViewById(R.id.mole1),
                findViewById(R.id.mole2),
                findViewById(R.id.mole3)
        };

        moleTouched = new boolean[moles.length];

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initializeMoleClickHandlers();
        startCountdown();

        // Initialize and start background music
        maintheme = MediaPlayer.create(this, R.raw.mole_maintheme);
        maintheme.setVolume(0.3f, 0.3f);
        maintheme.setLooping(true);

        if (SoundPreferences.isSoundEnabled(this)) {
            maintheme.start();
        }

        // Load the GIF into the background ImageView
        ivBackground = findViewById(R.id.ivBackground);
        Glide.with(this)
                .asGif()
                .load(R.drawable.mole_background) // Replace with your GIF resource
                .into(ivBackground);

        // Calculate positions for the grid and visualize the borders
        flGame.post(() -> {
            int frameWidth = flGame.getWidth();
            int frameHeight = flGame.getHeight();
            int cellWidth = frameWidth / 3;
            int cellHeight = frameHeight / 5;

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 3; col++) {
                    int x = col * cellWidth;
                    int y = row * cellHeight;
                    positions[row * 3 + col] = new int[]{x, y};
                }
            }
        });
    }

    private void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            this.sfx = null;
        }
    }

    private void playSoundEffect(int soundResourceId) {
        if (!SoundPreferences.isSoundEnabled(this)) {
            return;
        }
        releaseMediaPlayer(sfx);
        sfx = MediaPlayer.create(this, soundResourceId);
        sfx.start();
        sfx.setOnCompletionListener(this::releaseMediaPlayer);
    }

    private void initializePauseMenu() {
        llPauseMenu = findViewById(R.id.llPauseMenu);
        btnResume = findViewById(R.id.btnResume);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit = findViewById(R.id.btnQuit);
        tvPause = findViewById(R.id.tvGamePause);
        ImageView ivSettings = findViewById(R.id.ivSettings);

        btnResume.setOnClickListener(v -> resumeGame());
        btnRestart.setOnClickListener(v -> restartGame());
        btnQuit.setOnClickListener(v -> finish());
        ivSettings.setOnClickListener(v -> pauseGame());


    }

    private void setButtonBackground() {
        btnResume = findViewById(R.id.btnResume);
        btnRestart = findViewById(R.id.btnRestart);
        btnQuit = findViewById(R.id.btnQuit);

        btnResume.setBackgroundResource(R.drawable.button_background_img);
        btnRestart.setBackgroundResource(R.drawable.button_background_img);
        btnQuit.setBackgroundResource(R.drawable.button_background_img);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnResume.setBackgroundTintList(null);
            btnRestart.setBackgroundTintList(null);
            btnQuit.setBackgroundTintList(null);
        }
    }

    private void initializeMoleClickHandlers() {
        for (int i = 0; i < moles.length; i++) {
            int finalI = i;
            moles[i].setOnClickListener(v -> handleMoleClick(finalI));
        }
    }

    private void handleMoleClick(int index) {
        if (!isPaused && !isGameOver && !moleTouched[index]) {
            score++;
            tvScore.setText("Score: " + score);
            moleTouched[index] = true;

            // Replace the GIF with a static image
            Glide.with(this)
                    .load(R.drawable.mole_hit) // Replace with your static image resource
                    .into(moles[index]);

            if (vibrator.hasVibrator()) {
                vibrator.vibrate(100);
            }

            playSoundEffect(R.raw.mole_mobhit);
            handler.postDelayed(() -> {
                if (moleTouched[index]) {
                    moles[index].setVisibility(View.INVISIBLE);
                    moleTouched[index] = false;
                }
            }, 50);
        }
    }

    private void startCountdown() {
        if (isPaused) return;

        tvCountdown.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            if (!isPaused) updateCountdown("Starts in", R.raw.timerbip);
        }, 1000);
        handler.postDelayed(() -> {
            if (!isPaused) updateCountdown("3", R.raw.timerbip);
        }, 2000);
        handler.postDelayed(() -> {
            if (!isPaused) updateCountdown("2", R.raw.timerbip);
        }, 3000);
        handler.postDelayed(() -> {
            if (!isPaused) updateCountdown("1", R.raw.timerbip);
        }, 4000);
        handler.postDelayed(() -> {
            if (!isPaused) updateCountdown("GO!", R.raw.mole_timer_go);
        }, 5000);
        handler.postDelayed(() -> {
            if (!isPaused) {
                tvCountdown.setVisibility(View.INVISIBLE);
                countdownFinished = true;
                startGame();
            }
        }, 6000);
    }

    private void updateCountdown(String text, int soundResourceId) {
        if (!isPaused) {
            tvCountdown.setText(text);
            playSoundEffect(soundResourceId);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateTimer() {
        if (isPaused || isGameOver) return;
        long elapsedTime = System.currentTimeMillis() - startTime;
        remainingTime = gameDuration * 1000L - elapsedTime;
        int secondsRemaining = (int) (remainingTime / 1000);
        tvTimer.setText(secondsRemaining + "s");
    }

    private void showMoleWithDelay(final int index) {
        if (isPaused || isGameOver || !countdownFinished) return;

        int cellWidth = frameWidth / 3;
        int cellHeight = frameHeight / 5;

        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // No action needed on tick
            }

            @Override
            public void onFinish() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (isPaused || isGameOver || !countdownFinished || isFinishing() || isDestroyed())
                        return;
                }

                // Exclude positions 7 and 10 (cells 3 and 4 of column 2)
                int[] allowedIndices = {0, 1, 2, 3, 4, 5, 6, 8, 9, 11, 12, 13, 14};
                int randomIndex = allowedIndices[random.nextInt(allowedIndices.length)];
                int randomX = positions[randomIndex][0] + (cellWidth - moles[index].getWidth()) / 2;
                int randomY = positions[randomIndex][1] + (cellHeight - moles[index].getHeight()) / 2;

                // Adjust the Y position to ensure the mole is fully visible
                int moleHeight = moles[index].getHeight();
                if (randomY + moleHeight > screenHeight) {
                    randomY = screenHeight - moleHeight;
                }

                moles[index].setX(randomX);
                moles[index].setY(randomY);

                moles[index].requestLayout();

                handler.postDelayed(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (isFinishing() || isDestroyed()) return;
                    }
                    Glide.with(Boss_MA.this)
                            .asGif()
                            .load(R.drawable.mole_monster)
                            .override(150, 150)
                            .into(moles[index]);

                    moles[index].setVisibility(View.VISIBLE);
                    moleTouched[index] = false;
                    playSoundEffect(R.raw.mole_molespawn);

                    new CountDownTimer(2000, 2000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            // No action needed on tick
                        }

                        @Override
                        public void onFinish() {
                            if (!moleTouched[index]) {
                                moles[index].setVisibility(View.INVISIBLE);
                            }
                        }
                    }.start();
                }, 50); // Delay before making the mole visible
            }
        }.start();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
    }

    private void restartGame() {
        handler.removeCallbacksAndMessages(null);
        countdownFinished = false;
        tvPause.setText("Pause");
        score = 0;
        activeMoles.clear();
        currentProbability = INITIAL_PROBABILITY;
        isGameOver = false;
        isPaused = false;
        moleTouched = new boolean[moles.length];
        tvScore.setText("Score: 0");
        tvTimer.setText(gameDuration + "s");

        llPauseMenu.setVisibility(View.GONE);
        tvCountdown.setText("");

        for (ImageView mole : moles) {
            mole.setVisibility(View.INVISIBLE);
        }
        maintheme.seekTo(0);
        maintheme.start();
        startCountdown();
        startGame();
    }

    private void startGame() {
        startTime = System.currentTimeMillis();
        updateTimer();
        startGameLoop();
    }

    private void showMoles() {
        if (isPaused || isGameOver || !countdownFinished) return;

        activeMoles.clear();
        for (int i = 0; i < moles.length; i++) {
            if (random.nextFloat() < currentProbability) {
                activeMoles.add(i);
                showMoleWithDelay(i); // Call the method here
            }
        }


        // Increment probability only if the game is not paused
        if (!isPaused) {
            currentProbability = Math.min(currentProbability + PROBABILITY_INCREMENT, 1.0f);
        }
    }

    private void startGameLoop() {
        new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isGameOver || !countdownFinished) return;

                remainingTime = millisUntilFinished;
                updateTimer();

                if (remainingTime <= 0) {
                    endGame();
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                if (!isPaused && !isGameOver) {
                    endGame();
                }
            }
        }.start();

        // Use a separate handler to control mole appearance intervals
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPaused && !isGameOver && countdownFinished) {
                    showMoles();
                    handler.postDelayed(this, 1000); // Adjust the delay as needed
                }
            }
        }, 1000); // Initial delay before the first mole appears
    }


    private void resumeGame() {
        llPauseMenu.setVisibility(View.GONE);
        if (!countdownFinished) {
            tvCountdown.setVisibility(View.VISIBLE);
        }
        playSoundEffect(R.raw.button_clik);
        if (isPaused) {
            isPaused = false;
            startGame();
        }
        maintheme.start();
    }

    private void pauseGame() {
        llPauseMenu.setVisibility(View.VISIBLE);
        tvCountdown.setVisibility(View.GONE);
        playSoundEffect(R.raw.button_clik);
        maintheme.pause();
        isPaused = true;
    }

    private void endGame() {
        if (game_mode.equals("board")) {
            maintheme.stop();
            finish();
        } else {
            llPauseMenu.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.GONE);
            tvPause.setText("Game Over Score: " + score);
            playSoundEffect(R.raw.mole_victory);
            isGameOver = true;
            maintheme.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAllSounds();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseAllSounds();
        Glide.with(this).clear(ivBackground);
        for (ImageView mole : moles) {
            Glide.with(this).clear(mole);
        }
    }

    private void pauseAllSounds() {
        if (sfx != null && sfx.isPlaying()) {
            sfx.pause();
            maintheme.pause();
        }
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        // Ajoutez des données si nécessaire
        if (score >= 30) {
            resultIntent.putExtra("score", String.valueOf(score));
        } else if (score <= 30) {
            resultIntent.putExtra("score", "quit");
        }
        setResult(RESULT_OK, resultIntent);
        super.finish(); // Terminez l'Activity
    }


    @Override
    public void onBackPressed() {
        onPause();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Quit ?")
                .setMessage("Are you sure you want to quit? Your progress will be lost")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}