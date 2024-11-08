package com.example.helloworld;

import android.graphics.Bitmap;

public class SpriteSheet {
    private Bitmap spriteSheet;
    private int rows;
    private int cols;
    private int spriteWidth;
    private int spriteHeight;

    public SpriteSheet(Bitmap spriteSheet, int rows, int cols) {
        this.spriteSheet = spriteSheet;
        this.rows = rows;
        this.cols = cols;
        this.spriteWidth = spriteSheet.getWidth() / cols;
        this.spriteHeight = spriteSheet.getHeight() / rows;
    }

    public Bitmap getSprite(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            int x = col * spriteWidth;
            int y = row * spriteHeight;
            return Bitmap.createBitmap(spriteSheet, x, y, spriteWidth, spriteHeight);
        } else {
            return null; // Or throw an exception
        }
    }

    public int getCols() {
        return cols;
    }
    public int getRows() {
        return rows;
    }

    public int getWidth() {
        return spriteWidth;
    }

}