package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;  // Importer Paint
import android.util.DisplayMetrics;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying;
    private Bitmap spriteSheet;
    private Bitmap[] frames;
    private int frameIndex = 0;
    private int frameCount = 4;  // Nombre de frames dans ton spritesheet
    private int frameWidth;
    private int frameHeight;
    private long lastFrameTime;
    private int frameDuration = 100; // Durée d'affichage de chaque frame en millisecondes
    private float speedFactor = 100; // Facteur de vitesse pour ajuster le mouvement
    private Paint ball_color; // Objet Paint pour dessiner
    private Paint obstacle_color; // Objet Paint pour dessiner
    private float ballX = 200;  // Position initiale de la boule
    private float ballY = 200;
    private float ballRadius = 50; // Rayon de la boule
    private float speedX = 0; // Vitesse sur l'axe X
    private float speedY = 0; // Vitesse sur l'axe Y
    private float friction = 0.98f; // Coefficient de friction pour ralentir la boule
    private float accelerationFactor = 2.0f; // Facteur d'accélération pour le gyroscope
    // Attributs pour la largeur et la hauteur de l'écran
    private int screenWidth, screenHeight;
    private static final int GRID_SIZE = 30; // Taille de la grille (10x10)
    private static final int CELL_SIZE = 90; // Taille de chaque cellule en pixels
    private boolean[][] grid; // Représentation de la grille
    private float goalX; // Position X du point d'arrivée
    private float goalY; // Position Y du point d'arrivée
    private float goalRadius = 30; // Rayon du point d'arrivée
    private int[][] map = {
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 1, 1, 1, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 1, 1, 1, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
            {0, 1, 1, 1, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 0, 1, 0},
    };



    public GameView(Context context) {
        super(context);

        // Charger le spritesheet initial
        loadSpriteSheet(R.drawable.attack);

        // Obtenir les dimensions de l'écran
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels; // Largeur de l'écran
        screenHeight = displayMetrics.heightPixels; // Hauteur de l'écran

        // Position initiale de la boule (centrée)
        ballX = screenWidth / 2;
        ballY = screenHeight / 2;

        // Initialiser l'objet Paint
        ball_color = new Paint();
        obstacle_color = new Paint();
        obstacle_color.setColor(Color.BLUE); // Définir la couleur des obstacles
        ball_color.setColor(Color.RED); // Définir la couleur de la boule
        grid = new boolean[GRID_SIZE][GRID_SIZE];
        generateRandomObstacles();
        goalX = GRID_SIZE * CELL_SIZE - goalRadius; // Coin droit
        goalY = goalRadius; // Haut de l'écran
    }

    // Méthode pour charger un nouveau spritesheet
    public void changeSpriteSheet(int newSpriteResource) {
        loadSpriteSheet(newSpriteResource);
        frameIndex = 0;  // Réinitialiser l'animation
    }
    private void generateRandomObstacles() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                // 20% de chances d'être un obstacle
                grid[i][j] = Math.random() < 0.3;
            }
        }
    }

    // Méthode pour charger un spritesheet et découper les frames
    private void loadSpriteSheet(int spriteResource) {
        spriteSheet = BitmapFactory.decodeResource(getResources(), spriteResource);

        // Déterminer la hauteur fixe d'une frame
        frameHeight = spriteSheet.getHeight(); // Fixe ou change si nécessaire
        frameWidth = spriteSheet.getWidth() / frameCount; // Largeur d'une frame

        // Calculer le nombre de frames
        frameCount = spriteSheet.getWidth() / frameWidth; // Nombre de frames dans le spritesheet

        // Découper chaque frame dans un tableau
        frames = new Bitmap[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = Bitmap.createBitmap(spriteSheet, i * frameWidth, 0, frameWidth, frameHeight);
        }
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
        // Mettre à jour l'animation du sprite
        if (System.currentTimeMillis() - lastFrameTime > frameDuration) {
            frameIndex = (frameIndex + 1) % frameCount;
            lastFrameTime = System.currentTimeMillis();
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.BLACK); // Fond noir

            // Dessiner les obstacles
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (grid[i][j]) {
                        canvas.drawRect(i * CELL_SIZE, j * CELL_SIZE,
                                (i + 1) * CELL_SIZE, (j + 1) * CELL_SIZE,
                                obstacle_color);
                    }
                }
            }

            // Dessiner la boule
            canvas.drawCircle(ballX, ballY, ballRadius, ball_color); // Boule en bleu

            // Dessiner le point d'arrivée
            canvas.drawCircle(goalX, goalY, goalRadius, ball_color); // Point d'arrivée en vert

            getHolder().unlockCanvasAndPost(canvas);
        }
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
        // Met à jour la vitesse en fonction des valeurs du gyroscope
        speedX += x * accelerationFactor; // Ajoute l'accélération en X
        speedY -= y * accelerationFactor; // Ajoute l'accélération en Y (inversé pour mouvement naturel)

        // Appliquer la friction
        speedX *= friction;
        speedY *= friction;

        // Mettre à jour la position de la boule en fonction de la vitesse
        ballX += speedX;
        ballY += speedY;

        // Limiter la boule à l'intérieur de la vue
        if (ballX < ballRadius) {
            ballX = ballRadius;
            speedX = 0; // Stoppe la boule si elle touche le bord
        }
        if (ballX > getWidth() - ballRadius) {
            ballX = getWidth() - ballRadius;
            speedX = 0; // Stoppe la boule si elle touche le bord
        }
        if (ballY < ballRadius) {
            ballY = ballRadius;
            speedY = 0; // Stoppe la boule si elle touche le bord
        }
        if (ballY > getHeight() - ballRadius) {
            ballY = getHeight() - ballRadius;
            speedY = 0; // Stoppe la boule si elle touche le bord
        }

        invalidate(); // Redessiner la vue
    }

}
