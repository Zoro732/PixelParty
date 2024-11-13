package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Labyrinthe_Goal {
    private int x,y;
    private float radius;
    private Bitmap keyCurrentSprite;
    private int currentSpriteIndex = 0;
    private int frameCounter = 0;
    private final SpriteSheet keySpriteSheet; // Cointains all the sprites of key


    public Labyrinthe_Goal(int x, int y, float radius, Bitmap keySpriteSheet){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.keySpriteSheet = new SpriteSheet(keySpriteSheet,1,24);
        this.keyCurrentSprite = keySpriteSheet;
    }

    public void update() {
        // Update sprite animation
        frameCounter++;
        // Number of frames to display each sprite
        int framesPerSprite = 3;
        if (frameCounter >= framesPerSprite) {
            frameCounter = 0;
            currentSpriteIndex = (currentSpriteIndex + 1) % keySpriteSheet.getCols();
        }
    }

    // Classe Goal : modifiez la méthode draw pour centrer l'image et redimensionner le sprite de la clé
    public void draw(Canvas canvas, Paint paint) {
        keyCurrentSprite = keySpriteSheet.getSprite(0, currentSpriteIndex);
        if (keyCurrentSprite != null) {
            // Redimensionner le sprite de la clé
            Bitmap resizedSprite = Bitmap.createScaledBitmap(keyCurrentSprite, (int) (radius * 2), (int) (radius * 2), true);

            // Calcul pour centrer le sprite du goal
            int spriteX = x - resizedSprite.getWidth() / 2;
            int spriteY = y - resizedSprite.getHeight() / 2;

            // Dessiner le sprite redimensionné et centré
            canvas.drawBitmap(resizedSprite, spriteX, spriteY, paint);
        }
    }

}
