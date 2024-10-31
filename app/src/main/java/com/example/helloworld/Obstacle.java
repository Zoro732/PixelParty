package com.example.helloworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Obstacle {
    private int x, y; // Position de l'obstacle
    private int width = 100; // Largeur de l'obstacle
    private int height = 100; // Hauteur de l'obstacle

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        // Déplace l'obstacle vers le bas
        y += 15; // Vitesse de l'obstacle
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight; // Vérifie si l'obstacle est en dehors de l'écran
    }

    public void reset(int newX) {
        int adjustedX = x ; // Ajuste la position x
        y = -height; // Réinitialise l'obstacle au-dessus de l'écran
        x = newX; // Positionne l'obstacle sur la nouvelle voie
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED); // Couleur de l'obstacle
        canvas.drawRect(x, y, x + width, y + height, paint);
    }
}
