package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.Arrays;

public class Obstacle {
    private final int x;
    private int y;
    private final boolean isJumpable;
    private final Bitmap image;

    // Constructeur
    public Obstacle(int x, int y, Bitmap image, Bitmap[] vehicles) {
        this.x = x;
        this.y = y;
        this.image = image; // Store the image
        // Déterminer si l'obstacle est sautable en fonction du bitmap
        this.isJumpable = Arrays.asList(vehicles).contains(image); // Check if image is in vehicles array

    }

    public void update() {
        y += (int) GameView.obstacleSpeed;
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

    public Rect getRect() {
        return new Rect(x, y, x + getImageWidth(), y + getImageHeight());
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
}
