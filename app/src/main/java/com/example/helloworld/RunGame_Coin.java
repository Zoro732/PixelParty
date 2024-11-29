package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RunGame_Coin {
    private int x, y;
    private int frameCounter = 0;
    private int currentCoinIndex = 0;
    private final SpriteSheet coinSpriteSheet;
    private Bitmap currentCoinImage;

    public RunGame_Coin(int x, int y, Bitmap coinSpriteSheet) {
        this.x = x;
        this.y = y;
        this.coinSpriteSheet = new SpriteSheet(coinSpriteSheet, 1, 5);
    }


    public void update() {
        // Animation du sprite
        frameCounter++;
        int framesPerSprite = 4;
        if (frameCounter >= framesPerSprite) {
            frameCounter = 0;
            currentCoinIndex = (currentCoinIndex + 1) % coinSpriteSheet.getCols();
        }

        // Déplace la pièce vers le bas (utiliser obstacleSpeed pour la vitesse)
        y += (int) RunGame_GameView.obstacleSpeed; // On déplace la pièce selon la vitesse
        // Si la pièce dépasse l'écran, elle doit être réinitialisée
    }

    public boolean isOffScreen(int screenHeight) {
        //Log.d("DEBUG", " y = " + y + " screenheight" + screenHeight);
        return y > screenHeight;
    }

    public void reset(int newX) {
        this.x = newX;
        // Taille de la pièce
        this.y = -100;
    }

    public RectF getRect() {
        // Assurez-vous que l'image actuelle n'est pas nulle avant d'obtenir sa taille
        if (currentCoinImage == null) {
            currentCoinImage = coinSpriteSheet.getSprite(0, currentCoinIndex);
        }
        return new RectF(x, y, x + currentCoinImage.getWidth(), y + currentCoinImage.getHeight());
    }

    public void draw(Canvas canvas, Paint paint) {
        currentCoinImage = coinSpriteSheet.getSprite(0, currentCoinIndex);
        int newWidth = currentCoinImage.getWidth() * 2;
        int newHeight = currentCoinImage.getHeight() * 2;

        currentCoinImage = Bitmap.createScaledBitmap(currentCoinImage, newWidth, newHeight, false);
        canvas.drawBitmap(currentCoinImage, x, y, paint);
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
    public int getWidth() {
        return currentCoinImage.getWidth();
    }
}

