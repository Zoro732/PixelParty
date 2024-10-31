package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
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
    private float spriteX = 100, spriteY = 100;  // Position initiale du sprite

    // Attributs pour la largeur et la hauteur de l'écran
    private int screenWidth, screenHeight;

    public GameView(Context context) {
        super(context);

        // Charger le spritesheet initial
        loadSpriteSheet(R.drawable.attack);

        // Obtenir les dimensions de l'écran
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Dans le constructeur GameView

        screenWidth = displayMetrics.widthPixels; // Largeur de l'écran
        screenHeight = displayMetrics.heightPixels; // Hauteur de l'écran
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
            canvas.drawColor(Color.BLACK);
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



}
