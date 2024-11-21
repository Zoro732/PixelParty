package com.example.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

public class Labyrinthe_GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying;
    private float ballX, ballY;  // Position initiale de la boule
    private final float ballRadius = 60; // Rayon de la boule
    private float speedX = 0; // Vitesse sur l'axe X
    private float speedY = 0; // Vitesse sur l'axe Y
    private final float friction = 0.98f; // Coefficient de friction pour ralentir la boule
    private final float accelerationFactor = 2f; // Facteur d'accélération pour le gyroscope

    private final Paint paint;
    // Attributs pour la largeur et la hauteur de l'écran
    private final int screenWidth;
    private final int screenHeight;
    private int goalX;
    private int goalY;
    private static final int GRID_COLS = 15;// Nombre de colonnes
    private static final int GRID_ROWS = 9; // Nombre de lignes
    private final float goalRadius = 45; // Rayon du point d'arrivée
    private final int[][] map = {
            {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1},
            {1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
            {1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1},
            {1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };

//   private final int[][] map = { // FOR DEBUG
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//    };

    private final float tileSize_W;
    private final float tileSize_H;
    private final Vibrator vibrator;
    private final boolean goalReached = false; // Drapeau pour suivre l'état de la collision avec le point d'arrivée
    private CountDownTimer countDownTimer;
    private final int[] DefaultUserPosition = {10, 10};
    private final int defaultTimerValue = 60; // Valeur par défaut du compteur
    private int timerValue = defaultTimerValue; // Valeur du compteur

    private final Bitmap tileImagePath;
    private final Bitmap tileImageWall;

    private final Bitmap keyImage;
    private Labyrinthe_Goal labyrintheGoal;

    private final Labyrinthe_Player labyrinthePlayer;

    private Canvas canvas = new Canvas();

    private boolean isPaused = false; // État du jeu

    private final Paint timerTextPaint = new Paint();
    private boolean isTimerRunning = false; // Flag to track timer state

    private Bitmap playerSpriteSheet;

    public boolean win = false;
    private boolean loose = false;

    public Labyrinthe_GameView(Context context, String selection) {
        super(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Obtenir les dimensions de l'écran
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels; // Largeur de l'écran
        screenHeight = displayMetrics.heightPixels; // Hauteur de l'écran

        // Position initiale de la boule (centrée)
        ballX = DefaultUserPosition[0];
        ballY = DefaultUserPosition[1];
        tileSize_W = (float) screenWidth / GRID_COLS;
        tileSize_H = (float) screenHeight / GRID_ROWS;

        tileImagePath = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.blank_tile));
        tileImageWall = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.simple_wall));
        keyImage = BitmapFactory.decodeResource(getResources(), R.drawable.key);

        randomGoalPositionning();

        // Charger l'image du joueur et initialiser l'objet Player
        if (selection != null) {
            switch (selection) {
                case "Rouge":
                    playerSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_red);
                    break;
                case "Violet":
                    playerSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_purple);
                    break;
                default:
                    playerSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_blue);
                    break;
            }
        } else {
            Log.d("Labyrinthe_GameView", "selection is null");
            System.exit(1);
        }

        labyrinthePlayer = new Labyrinthe_Player(DefaultUserPosition[0], DefaultUserPosition[1], ballRadius, playerSpriteSheet);

        paint = new Paint();

        // Charger une police personnalisée
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.press2start);
        paint.setTypeface(customFont);

        startTimer(timerValue); // Démarrer le compteur

    }

    private Labyrinthe_Goal randomGoalPositionning() {
        // Tableau des coordonnées de points d'arrivée
        int[][] arrivalPoints = {
                calculateArrivalPoint(7, 8),  // Point d'arrivée à la position (8, 9)
                calculateArrivalPoint(14, 1), // Point d'arrivée à la position (15, 11)
                calculateArrivalPoint(14, 7)  // Point d'arrivée à la position (15, 8)
        };

        // Sélectionner un index aléatoire pour le point d'arrivée
        Random random = new Random();
        int randomIndex = random.nextInt(arrivalPoints.length); // Obtient un index aléatoire

        // Attribuer les coordonnées à goalX et goalY en utilisant le même index
        goalX = arrivalPoints[randomIndex][0];
        goalY = arrivalPoints[randomIndex][1];
        labyrintheGoal = new Labyrinthe_Goal(goalX, goalY, goalRadius, keyImage);
        // Creating animation with key
        return labyrintheGoal;
    }

    private int[] calculateArrivalPoint(int xMultiplier, int yMultiplier) {
        int x = (int) ((int) (tileSize_W * xMultiplier) + goalRadius);
        int y = (int) ((int) (tileSize_H * yMultiplier) + goalRadius+10);
        return new int[]{x, y};
    }

    private void startTimer(long initalTime) {
        if (!isTimerRunning) {
            isTimerRunning = true;
            countDownTimer = new CountDownTimer(initalTime * 1000, 1000) { // Compte à rebours de timerValue secondes
                public void onTick(long millisUntilFinished) {
                    timerValue = (int) (millisUntilFinished / 1000);
                    invalidate(); // Force redraw//
                }

                public void onFinish() {
                    ballX = DefaultUserPosition[0];
                    ballY = DefaultUserPosition[1];
                    timerValue = defaultTimerValue;

                    isTimerRunning = false;
                    loose = true;
                    long[] pattern = {0, 200, 100, 300}; // 0ms avant de commencer, 200ms de vibration, 100ms de pause, 200ms de vibration

                    // Appliquer le motif de vibration
                    if (vibrator != null) {
                        vibrator.vibrate(pattern, -1); // -1 pour ne pas répéter le motif
                    }
                    startTimer(timerValue);
                    // Vous pouvez ajouter ici un code pour gérer la fin du jeu si nécessaire
                    Toast.makeText(getContext(), "Temps écoulé !", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(100); // Délai de 100 millisecondes
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }

    // Méthode pour charger un nouveau spritesheet
    @Override
    public void run() {
        while (isPlaying || isPaused) { // Continue running only if playing or just paused
            if (!isPaused) {
                update(); // Update game state only when not paused
                draw();

            }
            sleep();  // Control frame rate
        }
    }

    private Bitmap resizeBitmap(Bitmap originalBitmap) {
        return Bitmap.createScaledBitmap(originalBitmap, (int) tileSize_W, (int) tileSize_H, false);
    }

    private void update() {
        if (!isPaused) { // Update animations only if not paused
            labyrintheGoal.update();
            labyrinthePlayer.update();
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            canvas = getHolder().lockCanvas();
            if (isPaused) {

            } else {
                // Dessiner les tuiles en fonction de mapSprite
                for (int y = 0; y < map.length; y++) {
                    for (int x = 0; x < map[y].length; x++) {
                        int tileValue = map[y][x]; // Récupérer la valeur de la tuile
                        // Gestion des différents cas pour chaque valeur de la tuile
                        switch (tileValue) {
                            case 0:
                                // Dessiner l'image PNG redimensionnée pour les tuiles '0'
                                canvas.drawBitmap(tileImagePath, x * tileSize_W, y * tileSize_H, null);
                                break;
                            case 1:
                                // Dessiner un obstacle
                                canvas.drawBitmap(tileImageWall, x * tileSize_W, y * tileSize_H, null);
                                break;
                            default:
                                // Si la valeur n'est pas définie, on peut choisir de dessiner un carré par défaut
                                break;
                        }
                    }
                }

                // Dessiner le joueur
                labyrinthePlayer.draw(canvas, null);

                // Dessiner le point d'arrivée
                labyrintheGoal.draw(canvas, null);

            }
            // Charger une police personnalisée
            Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.press2start);
            // Draw timer text
            timerTextPaint.setTypeface(customFont); // Assuming customFont is defined elsewhere
            timerTextPaint.setTextSize(70);
            timerTextPaint.setColor(Color.WHITE);
            canvas.drawText("" + timerValue, screenWidth - 200, (float) screenHeight / 1.8f, timerTextPaint);
            getHolder().unlockCanvasAndPost(canvas);

        }
    }

    public boolean isWin() {
        return win;
    }
    public boolean isLoosed() {
        return loose;
    }

    private void sleep() {
        try {
            Thread.sleep(16); // 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
        resumeGame();
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveBall(float x, float y) {
        if (!isPaused) {

            // Mise à jour de la vitesse en fonction du gyroscope
            speedX += x * accelerationFactor;
            speedY -= y * accelerationFactor;

            // Appliquer la friction
            speedX *= friction;
            speedY *= friction;

            // Calculer la position potentielle du centre de la hitbox
            float newBallX = ballX + speedX;
            float newBallY = ballY + speedY;

            // Définir la taille de la hitbox carrée (exemple : 2 * ballRadius pour avoir un carré de côté égal au diamètre de la boule)
            float hitboxSize = ballRadius;

            // Calculer les coordonnées des bords de la hitbox carrée
            float leftEdge = newBallX - hitboxSize / 2;
            float rightEdge = newBallX + hitboxSize / 2;
            float topEdge = newBallY - hitboxSize / 2;
            float bottomEdge = newBallY + hitboxSize / 2;

            // Limiter les mouvements pour rester dans les bords de l'écran
            if (leftEdge < 0) {
                newBallX = hitboxSize / 2;
                speedX = 0;
            } else if (rightEdge > getWidth()) {
                newBallX = getWidth() - hitboxSize / 2;
                speedX = 0;
            }

            if (topEdge < 0) {
                newBallY = hitboxSize / 2;
                speedY = 0;
            } else if (bottomEdge > getHeight()) {
                newBallY = getHeight() - hitboxSize / 2;
                speedY = 0;
            }

            // Convertir les coordonnées des bords de la hitbox en indices de la grille
            int leftCell = (int) (leftEdge / tileSize_W);
            int rightCell = (int) (rightEdge / tileSize_W);
            int topCell = (int) (topEdge / tileSize_H);
            int bottomCell = (int) (bottomEdge / tileSize_H);

            // Vérifier les collisions pour les coins de la hitbox carrée
            if ((leftCell >= 0 && leftCell < map[0].length && topCell >= 0 && topCell < map.length && map[topCell][leftCell] == 1) ||
                    (leftCell >= 0 && leftCell < map[0].length && bottomCell >= 0 && bottomCell < map.length && map[bottomCell][leftCell] == 1) ||
                    (rightCell >= 0 && rightCell < map[0].length && topCell >= 0 && topCell < map.length && map[topCell][rightCell] == 1) ||
                    (rightCell >= 0 && rightCell < map[0].length && bottomCell >= 0 && bottomCell < map.length && map[bottomCell][rightCell] == 1)) {

                // Collision détectée, activer la vibration
                if (vibrator != null) vibrator.vibrate(100);

                // Revenir à la position précédente pour éviter le passage
                ballX = DefaultUserPosition[0];
                ballY = DefaultUserPosition[1];
                speedX = 0;
                speedY = 0;
            } else {
                // Mettre à jour la position si pas de collision
                ballX = newBallX;
                ballY = newBallY;
            }

            // Mettre à jour la position de l'image du joueur
            labyrinthePlayer.setPosition(ballX, ballY);

            invalidate(); // Redessiner la vue
            checkCollisionWithGoal();
        }

    }

    private void checkCollisionWithGoal() {
        // Calculer la distance entre la boule et le point d'arrivée
        float dx = labyrinthePlayer.getX() - goalX;
        float dy = labyrinthePlayer.getY() - goalY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Vérifier si la distance est inférieure ou égale à la somme des rayons
        if (distance <= ballRadius + goalRadius && !goalReached) {
            // Collision détectée
            ballX = DefaultUserPosition[0];
            ballY = DefaultUserPosition[1];

            win = true;
            try {
                Thread.sleep(100); // Délai de 100 millisecondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long[] pattern = {0, 300, 100, 100}; // 0ms avant de commencer, 200ms de vibration, 100ms de pause, 200ms de vibration

            // Appliquer le motif de vibration
            if (vibrator != null) {
                vibrator.vibrate(pattern, -1); // -1 pour ne pas répéter le motif
            }
        }
    }

    void restartGame() {
        // Réinitialiser les positions et autres paramètres du jeu
        ballX = DefaultUserPosition[0];
        ballY = DefaultUserPosition[1];
        win = false;
        loose = false;
        invalidate();  // Redessine l'écran pour redémarrer le jeu
        // Redémarrer d'autres éléments nécessaires comme le joueur, les objectifs, etc.
        timerValue = defaultTimerValue;
        startTimer(timerValue); // Redémarrer le timer
        randomGoalPositionning();
        resume(); // Reprendre le jeu

    }

    void resumeGame() {
        isPaused = false;  // Reprend le jeu en mettant l'état de pause à false
        startTimer(timerValue); // Redémarrer le timer
        invalidate();  // Redessine l'écran pour revenir au jeu normal

    }

    public void pauseGame() {
        isPaused = true;  // Met le jeu en pause
        countDownTimer.cancel();
        isTimerRunning = false; // Reset timer running flag
        invalidate();  // Redessine l'écran pour afficher le menu de pause
    }

    void quitGame() {
        // Arrêter les threads
        try {
            isPlaying = false; // Arrêter la boucle du jeu
            if (gameThread != null) {
                gameThread.join(500); // Attendre jusqu'à 500ms pour que le thread se termine
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Annuler le CountDownTimer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Libérer les autres ressources
        if (vibrator != null) {
            vibrator.cancel();
        }

        // Quitter l'application proprement
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public int getRemainingTime() {
        return defaultTimerValue - timerValue;
    }
}
