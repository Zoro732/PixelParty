package com.example.helloworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Coin {
    private int x, y;
    private int size = 50; // Taille de la pièce

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += 10; // Vitesse de déplacement des pièces
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public void reset(int newX) {
        x = newX;
        y = -size;
    }

    public Rect getRect() {
        return new Rect(x, y, x + size, y + size);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.YELLOW); // Couleur de la pièce
        canvas.drawCircle(x + size / 2, y + size / 2, size / 2, paint);
    }
}
