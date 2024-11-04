package com.example.helloworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Obstacle {
    private int x, y; // Position de l'obstacle
    private int width = 100; // Largeur de l'obstacle
    private int height = 100; // Hauteur de l'obstacle
    private int laneIndex; // Indice de la voie où se trouve l'obstacle
    private int speed;

    public Obstacle(int x, int y, int initialSpeed) {
        this.x = x;
        this.y = y;
        this.speed = initialSpeed; // Vitesse initiale définie lors de la création
    }


    public void update() {
        // Déplace l'obstacle vers le bas
        y += speed; // Vitesse de l'obstacle
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public boolean isOffScreen(int screenHeight) {
        return y > screenHeight; // Vérifie si l'obstacle est en dehors de l'écran
    }

    public void reset(int newX, int i) {
        x = newX; // Positionne l'obstacle sur la nouvelle voie
        y = -height; // Réinitialise l'obstacle au-dessus de l'écran
        speed += 2; // Augmente la vitesse à chaque réinitialisation pour rendre le jeu plus difficile
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED); // Couleur de l'obstacle
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public int getLaneIndex() {
        return laneIndex; // Retourne l'indice de la voie
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex; // Permet de modifier l'indice de la voie si nécessaire
    }

    public int getY() {
        return y;
    }
}
