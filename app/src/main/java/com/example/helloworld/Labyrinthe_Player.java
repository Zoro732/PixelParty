package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Labyrinthe_Player {
    private float x, y; // Position du joueur
    private float radius; // Rayon pour la détection de collisions
    private Bitmap currentSprite; // Sprite actuel du joueur
    private int currentSpriteIndex = 0; // Index de l'animation
    private int frameCounter = 0; // Compteur de frames pour gérer l'animation
    private final SpriteSheet spriteSheet; // Feuille de sprites pour l'animation du joueur

    public Labyrinthe_Player(float x, float y, float radius, Bitmap spriteSheetBitmap) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        // Initialiser le SpriteSheet avec, par exemple, 1 ligne et plusieurs colonnes pour chaque image
        this.spriteSheet = new SpriteSheet(spriteSheetBitmap, 1, 8); // Par exemple, 6 colonnes pour 6 frames
        this.currentSprite = spriteSheet.getSprite(0, currentSpriteIndex);
    }

    public void update() {
        // Gérer l'animation : changer de sprite toutes les 5 frames
        frameCounter++;
        int framesPerSprite = 5;
        if (frameCounter >= framesPerSprite) {
            frameCounter = 0;
            currentSpriteIndex = (currentSpriteIndex + 1) % spriteSheet.getCols();
            currentSprite = spriteSheet.getSprite(0, currentSpriteIndex);
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (currentSprite != null) {
            // Redimensionner le sprite pour l'adapter à la taille de rayon spécifiée
            int newWidth = (int) (radius * 2);
            int newHeight = (int) (radius * 2);
            Bitmap resizedSprite = Bitmap.createScaledBitmap(currentSprite, newWidth, newHeight, true);

            // Dessiner le sprite au centre de la position actuelle
            canvas.drawBitmap(resizedSprite, x - radius, y - radius, paint);
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
