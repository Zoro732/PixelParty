package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;  // Importer Paint
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;


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
    private Paint end_color;
    private float ballX,ballY;  // Position initiale de la boule
    private float ballRadius = 20; // Rayon de la boule
    private float speedX = 0; // Vitesse sur l'axe X
    private float speedY = 0; // Vitesse sur l'axe Y
    private float friction = 0.98f; // Coefficient de friction pour ralentir la boule
    private float accelerationFactor = 2.0f; // Facteur d'accélération pour le gyroscope
    // Attributs pour la largeur et la hauteur de l'écran
    private int screenWidth, screenHeight;
    private static final int GRID_ROWS = 10; // Nombre de lignes
    private static final int GRID_COLS = 15;// Nombre de colonnes
    private float goalX; // Position X du point d'arrivée
    private float goalY; // Position Y du point d'arrivée
    private float goalRadius = 30; // Rayon du point d'arrivée
    private int[][] map = {
            {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
            {1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1},
            {1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };
    private int[][] arrivalPoints;
    private int tileSize_W, tileSize_H;
    private Vibrator vibrator;
    private boolean goalReached = false; // Drapeau pour suivre l'état de la collision avec le point d'arrivée

    public GameView(Context context) {
        super(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Charger le spritesheet initial
        loadSpriteSheet(R.drawable.attack);

        // Obtenir les dimensions de l'écran
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels; // Largeur de l'écran
        screenHeight = displayMetrics.heightPixels; // Hauteur de l'écran
        // Position initiale de la boule (centrée)
        ballX = 10 ;
        ballY = 10 ;
        tileSize_W = screenWidth / GRID_COLS;
        tileSize_H = screenHeight / GRID_ROWS;

        // Initialiser l'objet Paint
        ball_color = new Paint();
        obstacle_color = new Paint();
        end_color = new Paint();

        end_color.setColor(Color.GREEN); // Définir la couleur du point d'arrivée
        obstacle_color.setColor(Color.BLUE); // Définir la couleur des obstacles
        ball_color.setColor(Color.RED); // Définir la couleur de la boule

        // Tableau des coordonnées de points d'arrivée
        int[][] arrivalPoints = {
                {screenWidth / 2, screenHeight - 200},  // Point d'arrivée à la position (2, 3)
                {screenWidth - 100, screenHeight - 260},  // Point d'arrivée à la position (5, 6)
                {screenWidth - 100, (int) (screenHeight*0.15)},  // Point d'arrivée à la position (7, 2)
        };

        // Sélectionner un index aléatoire pour le point d'arrivée
        Random random = new Random();
        int randomIndex = random.nextInt(arrivalPoints.length); // Obtient un index aléatoire

        // Attribuer les coordonnées à goalX et goalY en utilisant le même index
        goalX = arrivalPoints[randomIndex][0];
        goalY = arrivalPoints[randomIndex][1];
    }

    // Méthode pour charger un nouveau spritesheet
    public void changeSpriteSheet(int newSpriteResource) {
        loadSpriteSheet(newSpriteResource);
        frameIndex = 0;  // Réinitialiser l'animation
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
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[y].length; x++) {
                    if (map[y][x] == 1) {
                        // Dessiner un obstacle
                        canvas.drawRect(x * tileSize_W, y * tileSize_H, (x + 1) * tileSize_W, (y + 1) * tileSize_H, obstacle_color);
                    }
                }
            }
            // Dessiner la boule
            canvas.drawCircle(ballX, ballY, ballRadius, ball_color); // Boule en bleu

            // Dessiner le point d'arrivée
            canvas.drawCircle(goalX, goalY, goalRadius, end_color); // Point d'arrivée en vert

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
        // Mise à jour de la vitesse en fonction du gyroscope
        speedX += x * accelerationFactor;
        speedY -= y * accelerationFactor;

        // Appliquer la friction
        speedX *= friction;
        speedY *= friction;

        // Calculer la position potentielle de la boule
        float newBallX = ballX + speedX;
        float newBallY = ballY + speedY;

        // Vérifier les collisions avec les bords de la vue
        // Limites gauche et droite
        if (newBallX < ballRadius) {
            newBallX = ballRadius; // Rester à l'intérieur de la limite gauche
            speedX = 0; // Réinitialiser la vitesse en X
        } else if (newBallX > getWidth() - ballRadius) {
            newBallX = getWidth() - ballRadius; // Rester à l'intérieur de la limite droite
            speedX = 0; // Réinitialiser la vitesse en X
        }

        // Limites haut et bas
        if (newBallY < ballRadius) {
            newBallY = ballRadius; // Rester à l'intérieur de la limite supérieure
            speedY = 0; // Réinitialiser la vitesse en Y
        } else if (newBallY > getHeight() - ballRadius) {
            newBallY = getHeight() - ballRadius; // Rester à l'intérieur de la limite inférieure
            speedY = 0; // Réinitialiser la vitesse en Y
        }

        // Mise à jour de la position de la boule
        ballX = newBallX;
        ballY = newBallY;

        // Calculer les coordonnées des bords de la boule
        float leftEdge = newBallX - ballRadius;
        float rightEdge = newBallX + ballRadius;
        float topEdge = newBallY - ballRadius;
        float bottomEdge = newBallY + ballRadius;

        // Convertir les coordonnées des bords en indices de grille
        int leftCell = (int) (leftEdge / tileSize_W);
        int rightCell = (int) (rightEdge / tileSize_W);
        int topCell = (int) (topEdge / tileSize_H);
        int bottomCell = (int) (bottomEdge / tileSize_H);

        // Vérifier les collisions avec les cellules de la grille sur chaque bord de la boule
        if ((leftCell >= 0 && leftCell < map[0].length && topCell >= 0 && topCell < map.length && map[topCell][leftCell] == 1) ||
                (leftCell >= 0 && leftCell < map[0].length && bottomCell >= 0 && bottomCell < map.length && map[bottomCell][leftCell] == 1) ||
                (rightCell >= 0 && rightCell < map[0].length && topCell >= 0 && topCell < map.length && map[topCell][rightCell] == 1) ||
                (rightCell >= 0 && rightCell < map[0].length && bottomCell >= 0 && bottomCell < map.length && map[bottomCell][rightCell] == 1)) {

            // Collision détectée, activer la vibration
            if (vibrator != null) vibrator.vibrate(100);

            // Revenir à la position précédente pour éviter le passage
            ballX = 10 ;
            ballY = 10 ;
            speedX = 0;
            speedY = 0;
        } else {
            // Mettre à jour la position si pas de collision
            ballX = newBallX;
            ballY = newBallY;
        }

        invalidate(); // Redessiner la vue
        checkCollisionWithGoal();
    }

    private void checkCollisionWithGoal() {
        // Calculer la distance entre la boule et le point d'arrivée
        float dx = ballX - goalX;
        float dy = ballY - goalY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Vérifier si la distance est inférieure ou égale à la somme des rayons
        if (distance <= ballRadius + goalRadius && !goalReached) {
            // Collision détectée
            ballX = 10 ;
            ballY = 10 ;
            Toast.makeText(getContext(), "Arrivée atteinte !", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(100); // Délai de 100 millisecondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
