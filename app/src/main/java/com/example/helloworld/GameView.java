package com.example.helloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private int screenWidth, screenHeight;
    private Paint paint;
    private Player player;
    private int laneWidth;
    private List<Obstacle> obstacles;
    private float obstacleSpeed = 10;
    private float speedIncrement = 4;
    private float startX = 0; // Initialisation de startX

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        laneWidth = screenWidth / 3;
        player = new Player(screenWidth, screenHeight - 200);
        paint = new Paint();
        generateObstacles();
    }

    private void generateObstacles() {
        obstacles = new ArrayList<>();
        boolean[] occupiedLanes = new boolean[3];
        for (int i = 0; i < 3; i++) {
            int laneIndex;
            do {
                laneIndex = (int) (Math.random() * 3);
            } while (occupiedLanes[laneIndex]);
            occupiedLanes[laneIndex] = true;
            int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;
            obstacles.add(new Obstacle(laneX, -100 - (i * 200), (int) obstacleSpeed));
        }
        obstacleSpeed += speedIncrement;
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
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
            if (obstacle.isOffScreen(screenHeight)) {
                int laneIndex = (int) (Math.random() * 3);
                int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;
                obstacle.reset(laneX, -100);
            }
            if (Rect.intersects(player.getRect(), obstacle.getRect())) {
                endGame();
                return;
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE);

            // Dessin des voies
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(0, 0, laneWidth, getHeight(), paint);
            paint.setColor(Color.DKGRAY);
            canvas.drawRect(laneWidth, 0, laneWidth * 2, getHeight(), paint);
            paint.setColor(Color.GRAY);
            canvas.drawRect(laneWidth * 2, 0, laneWidth * 3, getHeight(), paint);

            // Dessin du joueur et des obstacles
            paint.setColor(Color.GREEN);
            canvas.drawRect(player.getRect(), paint);
            if (isPlaying) {
                for (Obstacle obstacle : obstacles) {
                    obstacle.draw(canvas, paint);
                }
            } else {
                drawGameOver(canvas);
            }
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

        // Bouton Rejouer
        paint.setColor(Color.BLUE);
        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = screenWidth / 2 - buttonWidth / 2;
        int buttonY = screenHeight / 2;
        canvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);

        // Texte "Rejouer"
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Rejouer", screenWidth / 2, buttonY + buttonHeight / 2 + 20, paint);
    }

    private void endGame() {
        isPlaying = false;
        draw(); // Dessine "Game Over" immédiatement
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
        if (!isPlaying) { // Redémarrer si le jeu est terminé
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int buttonX = screenWidth / 2 - 200;
                int buttonY = screenHeight / 2;
                if (event.getX() >= buttonX && event.getX() <= buttonX + 400 &&
                        event.getY() >= buttonY && event.getY() <= buttonY + 150) {
                    restartGame();
                }
            }
        } else { // Déplacement si le jeu est en cours
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    if (endX < startX) player.moveLeft();
                    else if (endX > startX) player.moveRight();
                    break;
            }
        }
        return true;
    }

    private void restartGame() {
        isPlaying = true;
        player.resetPosition();
        obstacles.clear();
        generateObstacles();
        resume();
    }
}
