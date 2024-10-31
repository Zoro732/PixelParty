package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;  // Importer Paint
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying;
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
    private int tileSize_W, tileSize_H;
    private Vibrator vibrator;
    private boolean goalReached = false; // Drapeau pour suivre l'état de la collision avec le point d'arrivée
    private CountDownTimer countDownTimer;
    private int DefaultUserPosition[] = {10,10};
    private int defaultTimerValue = 40; // Valeur par défaut du compteur
    private int timerValue = defaultTimerValue; // Valeur du compteur
    private Paint timerPaint; // Objet Paint pour le compteur

    public GameView(Context context) {
        super(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Obtenir les dimensions de l'écran
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels; // Largeur de l'écran
        screenHeight = displayMetrics.heightPixels; // Hauteur de l'écran

        // Position initiale de la boule (centrée)
        ballX = DefaultUserPosition[0];
        ballY = DefaultUserPosition[1];
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

        // Initialiser le compteur
        timerPaint = new Paint();
        timerPaint.setColor(Color.WHITE); // Couleur du texte
        timerPaint.setTextSize(50); // Taille de la police
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/press_start_2p.ttf");
//        timerPaint.setTypeface(typeface); // Charger la police
        startTimer(); // Démarrer le compteur

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timerValue * 1000, 1000) { // Compte à rebours de timerValue secondes
            @Override
            public void onTick(long millisUntilFinished) {
                timerValue--; // Décrémenter la valeur du compteur chaque seconde
            }

            @Override
            public void onFinish() {
                timerValue = 0; // La valeur finale
                ballX = DefaultUserPosition[0];
                ballY = DefaultUserPosition[1];
                timerValue = defaultTimerValue;

                long[] pattern = {0, 200, 100, 300}; // 0ms avant de commencer, 200ms de vibration, 100ms de pause, 200ms de vibration

                // Appliquer le motif de vibration
                if (vibrator != null) {
                    vibrator.vibrate(pattern, -1); // -1 pour ne pas répéter le motif
                }
                startTimer();
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


    // Méthode pour charger un nouveau spritesheet
    @Override
    public void run() {
        while (isPlaying) {
            draw();
            sleep();
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

            canvas.drawText( "" + timerValue, screenWidth - 200, screenHeight/2, timerPaint); // Position du texte

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
            ballX = DefaultUserPosition[0];
            ballY = DefaultUserPosition[1];
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
            ballX = DefaultUserPosition[0];
            ballY = DefaultUserPosition[1] ;
            timerValue = defaultTimerValue;
            Toast.makeText(getContext(), "Arrivée atteinte !", Toast.LENGTH_SHORT).show();
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

}
