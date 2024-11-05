package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player {
    private int y;
    private final int laneWidth;
    private int currentLane;
    private final int screenHeight;
    private final int screenWidth;
    private final int originalSize = 100;
    private int currentSize;
    private boolean isJumping = false;
    private int jumpStartY;
    private float jumpSpeed = 0;

    // Sprite animation fields
    private SpriteSheet runSpriteSheet;
    private SpriteSheet jumpSpriteSheet;
    private int currentSpriteIndex = 0;
    private int frameCounter = 0;
    private int framesPerSprite = 5; // Number of frames to display each sprite
    private Bitmap currentSprite;

    public Player(Context context, int screenWidth, int screenHeight, Bitmap runSpriteSheet, Bitmap jumpSpriteSheet) {
        this.screenHeight = screenHeight;
        this.laneWidth = screenWidth / 3;
        this.y = screenHeight - 200;
        this.screenWidth = screenWidth;
        this.currentLane = 1;
        this.currentSize = originalSize;

        // Initialize SpriteSheets
        this.runSpriteSheet = new SpriteSheet(runSpriteSheet, 4, 8);
        this.jumpSpriteSheet = new SpriteSheet(jumpSpriteSheet, 4, 6);
        this.currentSprite = runSpriteSheet.getSprite(3, 0);
    }

    public void moveLeft() {
        if (currentLane > 0) {
            currentLane--;
        }
    }

    public void moveRight() {
        if (currentLane < 2) {
            currentLane++;
        }
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpStartY = y;
            jumpSpeed = -20;
            currentSize = (int) (originalSize * 1.5); // Adjust size during jump if needed
        }
    }

    public void update() {
        if (isJumping) {
            y += (int) jumpSpeed;
            float gravity = 0.9f;
            jumpSpeed += gravity;

            if (y >= jumpStartY) {
                y = jumpStartY;
                isJumping = false;
                currentSize = originalSize;
            }
        }

        // Update sprite animation
        frameCounter++;
        if (frameCounter >= framesPerSprite) {
            frameCounter = 0;
            if (isJumping) {
                currentSpriteIndex = (currentSpriteIndex + 1) % jumpSpriteSheet.getCols();
            } else {
                currentSpriteIndex = (currentSpriteIndex + 1) % runSpriteSheet.getCols();
            }
        }

    }

    public void draw(Canvas canvas, Paint paint) {
        if (isJumping) {
            currentSprite = jumpSpriteSheet.getSprite(3, currentSpriteIndex); // 4th row (index 3)
        } else {
            currentSprite = runSpriteSheet.getSprite(3, currentSpriteIndex); // 4th row (index 3)
        }
        if (currentSprite != null) {

            // Draw hitbox border (for debugging)
            paint.setColor(Color.GREEN); // Choose a color for the border
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2); // Adjust border thickness as needed
            canvas.drawRect(getRect(), paint); // Draw the rectangle using the hitbox Rect

            int newWidth = currentSprite.getWidth() * 2; // Double the original width
            int newHeight = currentSprite.getHeight() * 2; // Double the original height

            // Resize the sprite
            Bitmap resizedSprite = Bitmap.createScaledBitmap(currentSprite, newWidth, newHeight, true);

            // Draw the resized sprite
            canvas.drawBitmap(resizedSprite, getX(), y, paint);

            // Reset paint style for other drawing
            paint.setStyle(Paint.Style.FILL);
        }
    }

    public int getX() {
        return screenWidth / 2 - currentSprite.getWidth() / 2;
    }

    public Rect getRect() {
        return new Rect(getX(), y, getX() + currentSize, y + currentSize);
    }

    public void resetPosition() {
        currentLane = 1;
        y = screenHeight - 200;
        currentSize = originalSize;
    }

    public boolean isJumping() {
        return isJumping;
    }
}