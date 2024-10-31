package com.example.helloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private int screenWidth, screenHeight;
    private Paint paint;
    private Player player;
    private int obstacleX, obstacleY;
    private float startX;
    private int laneWidth;
    private List<Obstacle> obstacles; // Liste des obstacles

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        laneWidth =  screenWidth / 3;

        // Initialisation du joueur et de l'obstacle
        player = new Player(screenWidth, screenHeight - 200);
        obstacleX = screenWidth / 3;
        obstacleY = screenHeight - 200;

        paint = new Paint();

        obstacles = new ArrayList<>(); // Initialise la liste d'obstacles

        // Créer quelques obstacles aléatoires
        for (int i = 0; i < 3; i++) { // Créer 3 obstacles
            int laneIndex = (int)(Math.random() * 3); // Générer un index de voie aléatoire
            int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - (100 / 2); // Centrer l'obstacle de 100 pixels de large
            obstacles.add(new Obstacle(laneX, (int)(Math.random() * -500))); // Commence au-dessus de l'écran avec une position Y aléatoire
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
        // Met à jour les obstacles
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            obstacle.update(); // Met à jour la position de l'obstacle

            // Vérifie si l'obstacle est en dehors de l'écran
            if (obstacle.isOffScreen(screenHeight)) {
                // Réinitialise la position de l'obstacle en haut de l'écran avec une nouvelle voie
                int newLaneIndex = (int)(Math.random() * 3); // Générer un nouvel index de voie
                int newLaneX = (newLaneIndex * laneWidth) + (laneWidth / 2) - (100 / 2); // Centrer le nouvel obstacle
                obstacle.reset(newLaneX); // Positionne l'obstacle sur la nouvelle voie
            }

            // Collision avec l'obstacle
            if (Rect.intersects(player.getRect(), obstacle.getRect())) {
                isPlaying = false; // Arrêter le jeu si collision
                //Toast.makeText(getContext(), "Perdu !", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(2000); // Délai de 100 millisecondes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isPlaying = true; // relance le jeux

            }
        }
    }



    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE); // Fond de l'écran

            // Couleurs des voies
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            // Couleur pour la voie gauche
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(0, 0, laneWidth, getHeight(), paint);

            // Couleur pour la voie centrale
            paint.setColor(Color.DKGRAY);
            canvas.drawRect(laneWidth, 0, laneWidth * 2, getHeight(), paint);

            // Couleur pour la voie droite
            paint.setColor(Color.GRAY);
            canvas.drawRect(laneWidth * 2, 0, laneWidth * 3, getHeight(), paint);

            // Dessiner le joueur
            paint.setColor(Color.GREEN); // Couleur du joueur
            canvas.drawRect(player.getRect(), paint);

            // Dessiner les obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas, paint); // Dessine chaque obstacle
            }

            // Dessiner les obstacles (ajoute ton code pour les obstacles ici)
            getHolder().unlockCanvasAndPost(canvas);
        }

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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                if (endX < startX) {
                    player.moveLeft();  // Glissement vers la gauche
                } else if (endX > startX) {
                    player.moveRight(); // Glissement vers la droite
                }
                break;
        }
        return true;
    }

}

