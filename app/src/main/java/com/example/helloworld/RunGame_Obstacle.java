package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Arrays;

public class RunGame_Obstacle {
    private int x;
    private int y;
    private final boolean isJumpable;
    private final Bitmap image;
    private int speed;
    private boolean hasPlayedSound = false;  // Variable pour suivre si le son a été joué

    // Constructeur
    public RunGame_Obstacle(int x, int y, Bitmap image, Bitmap[] vehicles) {
        this.x = x;
        this.y = y;
        this.image = image; // Store the image
        // Déterminer si l'obstacle est sautable en fonction du bitmap
        this.isJumpable = Arrays.asList(vehicles).contains(image); // Check if image is in vehicles array
    }

    public void update() {
        y += (int) RunGame_GameView.obstacleSpeed;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public boolean isJumpable() {
        return isJumpable;
    }

    public RectF getRect() {
        return new RectF(x, y, x + getImageWidth(), y + getImageHeight());
    }

    public int getImageHeight() {
        return image.getHeight();
    }
    public int getImageWidth() {
        return image.getWidth();
    }


    public void setY(int currentY) {
        y = currentY;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public void reset(int laneX) {
        // Positionner l'obstacle juste au-dessus de l'écran
        this.y = -100; // Assure que l'obstacle redémarre juste au-dessus de l'écran
        this.x = laneX; // Positionner dans une voie aléatoire

        // Réinitialiser la vitesse ou tout autre attribut, si nécessaire
        this.speed = (int) RunGame_GameView.obstacleSpeed/* vitesse de base, si applicable */;
    }

    public int getHeight() {
        return image.getHeight(); // Assuming 'image' is the Bitmap of the obstacle
    }

}
