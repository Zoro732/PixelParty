package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Arrays;

public class Obstacle {
    private int x;
    private int y;
    private final int speed;
    private int width = 100;
    private int height = 100;
    private boolean isJumpable;
    private Bitmap vehicleBitmap;
    private Bitmap image;
    private Bitmap[] vehicles; // Ajouter un tableau pour les véhicules

    // Constructeur
    public Obstacle(int x, int y, int speed, Bitmap image, Bitmap[] vehicles) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.image = image; // Store the image
        this.vehicles = vehicles; // Initialiser le tableau de véhicules
        // Déterminer si l'obstacle est sautable en fonction du bitmap
        this.isJumpable = Arrays.asList(vehicles).contains(image); // Check if image is in vehicles array

    }

    public void update() {
        y += speed;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public void reset(int newX) {
        x = newX;
        y = -height;
        isJumpable = isJumpable(); // Re-définir si l'obstacle est sautable
    }

    public boolean isJumpable() {
        return isJumpable;
    }


    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(vehicleBitmap, x, y, paint); // Utilisez le bitmap
    }

    public void setY(int currentY) {
        y = currentY;
    }

    // Méthode pour définir l'image de l'obstacle
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
}
