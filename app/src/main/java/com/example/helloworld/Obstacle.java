package com.example.helloworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Obstacle {
    private int x;
    private int y;
    private final int speed;
    private int width = 100;
    private int height = 100;
    private boolean isJumpable;
    private int color; // Nouvelle variable pour stocker la couleur

    public Obstacle(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;

        // Déterminer aléatoirement si l'obstacle est sautable
        this.isJumpable = Math.random() < 0.5; // 50% de chance
        this.color = isJumpable ? Color.BLUE : Color.RED; // Bleu si sautable, Rouge sinon
    }

    public void update() {
        y += speed;
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public void reset(int newX) {
        x = newX;
        y = -height;
        isJumpable = Math.random() < 0.5; // Re-définir aléatoirement si l'obstacle est sautable
        color = isJumpable ? Color.BLUE : Color.RED; // Mise à jour de la couleur
    }

    public boolean isJumpable() {
        return this.color == Color.BLUE;
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color); // Utiliser la couleur de l'obstacle
        canvas.drawRect(getRect(), paint);
    }

    public void setY(int currentY) {
        y = currentY;
    }
}
