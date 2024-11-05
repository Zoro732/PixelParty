package com.example.helloworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Coin {
    private int x, y;
    private final int size = 50; // Taille de la pièce
    private float speed; // Vitesse de la pièce

    public Coin(int x, int y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed; // Initialiser avec la vitesse donnée
    }

    public void setSpeed(float speed) {
        this.speed = speed; // Permet de mettre à jour la vitesse des pièces
    }

    public void update() {
        y += (int) GameView.obstacleSpeed; // Utilisez la vitesse actuelle des pièces
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight;
    }

    public void reset(int newX) {
        x = newX;
        y = -size;
        speed += 2; // Augmente la vitesse à chaque réinitialisation pour rendre le jeu plus difficile
    }

    public Rect getRect() {
        return new Rect(x, y, x + size, y + size);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.YELLOW); // Couleur de la pièce
        canvas.drawCircle(x + (float) size / 2, y + (float) size / 2, (float) size / 2, paint);
    }
}

