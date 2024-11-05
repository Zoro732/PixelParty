package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private int screenWidth, screenHeight;
    private Paint paint;
    private Player player;
    private int laneWidth;
    private List<Obstacle> obstacles;
    private List<Coin> coins;
    private int score = 0; // Score de pièces collectées
    static float obstacleSpeed = 15;
    private float startX = 0; // Initialisation de startX
    private float startY = 0; // Initialisation de startY
    private Sensor accelerometer;
    private float jumpThreshold = 1.0f; // Seuil pour détecter le saut
    private boolean isJumping = false;
    private Vibrator vibrator; // Déclaration de l'objet Vibrator
    private static final String TAG = "AccelerometerValues";
    private String jumpMessage = "";
    private int obstacleSpacing = 400; // Initial spacing
    private long lastObstacleTime = 0; // Time of last obstacle creation
    private List<Obstacle> obstaclePool = new ArrayList<>();
    private Bitmap backgroundBitmap; // For background and lanes

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        laneWidth = screenWidth / 3;
        player = new Player(screenWidth, screenHeight - 200);
        paint = new Paint();
        generateObstacles();
        generateCoins();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        }

    private void generateCoins() {
        coins = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // Crée 3 pièces
            int laneIndex = (int) (Math.random() * 3);
            int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 25; // Centrer la pièce
            coins.add(new Coin(laneX, -200 - (i * 300), obstacleSpeed)); // Espacement vertical
        }
    }

    private Obstacle createObstacle() {
        List<Integer> availableLanes = new ArrayList<>(Arrays.asList(0, 1, 2));
        int laneIndex = availableLanes.remove((int) (Math.random() * availableLanes.size()));
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;
        boolean isJumpable = Math.random() < 0.5;
        return new Obstacle(laneX, -100, (int) obstacleSpeed); // Initial Y position is -100
    }

    private void generateObstacles() {
        obstacles = new ArrayList<>();

        // Generate a random number of obstacles between 3 and 6
        int numObstacles = 3 + (int) (Math.random() * 4);

        int currentY = -100; // Starting Y position for the first obstacle

        for (int i = 0; i < numObstacles; i++) {
            Obstacle obstacle = createObstacle(); // Use the createObstacle() method
            obstacle.setY(currentY); // Set the Y position based on spacing

            obstacles.add(obstacle);

            // Generate random spacing between 100 and 300
            int spacing = 100 + (int) (Math.random() * 201);
            currentY -= spacing; // Update Y position for the next obstacle
        }
    }

    private Obstacle getObstacle() {
        if (obstaclePool.isEmpty()) {
            // Create a new obstacle using the createObstacle() method
            return createObstacle();
        } else {
            // Reuse an obstacle from the pool
            return obstaclePool.remove(0);
        }
    }

    private void releaseObstacle(Obstacle obstacle) {
        obstaclePool.add(obstacle);
    }


    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        player.update();
        long currentTime = System.currentTimeMillis();

        RectF playerRect = new RectF(player.getRect()); // Create player RectF once

        for (int i = obstacles.size() - 1; i >= 0; i--) { // Iterate backwards for efficient removal
            Obstacle obstacle = obstacles.get(i);
            obstacle.update();

            if (obstacle.isOffScreen(screenHeight) && currentTime - lastObstacleTime > obstacleSpacing) {
                int laneIndex = (int) (Math.random() * 3);
                int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;

                releaseObstacle(obstacle);
                obstacles.set(i, getObstacle()); // Replace obstacle in the list
                obstacles.get(i).reset(laneX);

                lastObstacleTime = currentTime;
                obstacleSpacing -= 10;
                if (obstacleSpacing < 100) {
                    obstacleSpacing = 100;
                }
            }

            RectF obstacleRect = new RectF(obstacle.getRect()); // Create obstacle RectF
            if (RectF.intersects(playerRect, obstacleRect)) {
                if (obstacle.isJumpable() && player.isJumping()) {
                    // Ignore collision
                } else {
                    endGame();
                    return;
                }
            }
        }

        for (int i = coins.size() - 1; i >= 0; i--) { // Iterate backwards for efficient removal
            Coin coin = coins.get(i);
            coin.setSpeed(obstacleSpeed);
            coin.update();

            if (coin.isOffScreen(screenHeight)) {
                int newLaneIndex = (int) (Math.random() * 3);
                int newLaneX = (newLaneIndex * laneWidth) + (laneWidth / 2) - 25;
                coin.reset(newLaneX);
            }

            RectF coinRect = new RectF(coin.getRect()); // Create coin RectF
            if (RectF.intersects(playerRect, coinRect)) {
                score++;
                int newLaneIndex = (int) (Math.random() * 3);
                int newLaneX = (newLaneIndex * laneWidth) + (laneWidth / 2) - 25;
                coin.reset(newLaneX);
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE);
            if (getHolder().getSurface().isValid()) {
                canvas.drawColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.LTGRAY);
                canvas.drawRect(0, 0, laneWidth, getHeight(), paint);
                paint.setColor(Color.DKGRAY);
                canvas.drawRect(laneWidth, 0, laneWidth * 2, getHeight(), paint);
                paint.setColor(Color.GRAY);
                canvas.drawRect(laneWidth * 2, 0, laneWidth * 3, getHeight(), paint);
            }
            // Obstacle draw
            if (isPlaying) {
                for (Obstacle obstacle : obstacles) {
                    obstacle.draw(canvas, paint);
                }
            } else {
                drawGameOver(canvas);
            }

            // Player
            paint.setColor(Color.GREEN);
            canvas.drawRect(player.getRect(), paint);

            // Coins draw
            for (Coin coin : coins) {
                coin.draw(canvas, paint);
            }
            // Score draw
            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, screenWidth - 200, 100, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameOver(Canvas canvas) {
        paint.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setColor(Color.RED);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over", screenWidth / 2, screenHeight / 3, paint);

        // Afficher le score
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, screenWidth / 2, screenHeight / 2 + 50, paint); // Affichage du score

        // Bouton Rejouer
        paint.setColor(Color.BLUE);
        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = screenWidth / 2 - buttonWidth / 2;
        int buttonY = screenHeight / 2 + 100; // Positionner le bouton en dessous du score
        canvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);

        // Texte "Rejouer"
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Rejouer", screenWidth / 2, buttonY + buttonHeight / 2 + 20, paint);
    }

    private void endGame() {
        isPlaying = false;
        coins.clear(); // Supprimer toutes les pièces
        draw();
    }

    private void sleep() {
        try {
            Thread.sleep(17); // Environ 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPlaying) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Calculer les coordonnées du bouton "Rejouer"
                int buttonWidth = 400;
                int buttonHeight = 150;
                int buttonX = screenWidth / 2 - buttonWidth / 2;
                int buttonY = screenHeight / 2 + 100; // Positionner le bouton en dessous du score

                // Vérifier si l'utilisateur a touché le bouton
                if (event.getX() >= buttonX && event.getX() <= buttonX + buttonWidth &&
                        event.getY() >= buttonY && event.getY() <= buttonY + buttonHeight) {
                    restartGame();
                }
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY(); // Enregistrer la position Y au début
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Vérifier si l'utilisateur effectue un mouvement vers le haut
                    float deltaY = startY - event.getY(); // Calculer le mouvement vertical
                    if (deltaY > 100) { // Si le mouvement vers le haut est suffisant
                        player.jump(); // Effectue le saut
                        startY = event.getY(); // Réinitialiser la position de départ Y pour éviter de sauter plusieurs fois
                        if (vibrator != null) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    if (endX < startX && player.isJumping() == false) {
                        player.moveLeft();
                    } else if (endX > startX && player.isJumping() == false) {
                        player.moveRight();
                    }
                    break;
            }
        }
        return true;
    }

    private void restartGame() {
        score = 0;
        player.resetPosition(); // Reset player position
        generateObstacles(); // Generate new obstacles
        generateCoins(); // Generate new coins
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
