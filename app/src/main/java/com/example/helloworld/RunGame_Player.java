package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RunGame_Player {
    private int y;
    private int currentLane;
    private final int screenHeight;
    private boolean isJumping = false;
    private int jumpStartY;
    private float jumpSpeed = 0;
    private final int laneWidth;

    // Sprite animation fields
    private final SpriteSheet runSpriteSheet;
    private final SpriteSheet jumpSpriteSheet;
    private int currentSpriteIndex = 0;
    private int frameCounter = 0;
    private Bitmap currentSprite;
    private float sizeMultiplier = 3;

    public RunGame_Player(int screenWidth, int screenHeight, Bitmap runSpriteSheet, Bitmap jumpSpriteSheet) {
        this.screenHeight = screenHeight;
        this.laneWidth = screenWidth / 3;
        this.y = screenHeight - 200;
        this.currentLane = 1;

        // Initialize SpriteSheets
        this.runSpriteSheet = new SpriteSheet(runSpriteSheet, 4, 8);
        this.jumpSpriteSheet = new SpriteSheet(jumpSpriteSheet, 4, 6);

        this.currentSprite = runSpriteSheet;
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
        }
    }

    public void update() {
        if (isJumping) {
            y += (int) jumpSpeed;
            float gravity = 2;
            jumpSpeed += gravity;
            sizeMultiplier =4;
            currentSprite = jumpSpriteSheet.getSprite(3, currentSpriteIndex);
            if (y >= jumpStartY) {
                y = jumpStartY;
                isJumping = false;
            }
        } else {
            sizeMultiplier = 3;
            currentSprite = runSpriteSheet.getSprite(3, currentSpriteIndex);
        }

        // Update sprite animation
        frameCounter++;
        // Number of frames to display each sprite
        int framesPerSprite = 2;
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
            int newWidth = (int) (currentSprite.getWidth() * sizeMultiplier); // Double the original width
            int newHeight = (int) (currentSprite.getHeight() * sizeMultiplier); // Double the original height

            // Resize the sprite
            Bitmap resizedSprite = Bitmap.createScaledBitmap(currentSprite, newWidth, newHeight, false);

            // Draw the resized sprite
            canvas.drawBitmap(resizedSprite, getX(), getY(), paint);

            // Reset paint style for other drawing
            paint.setStyle(Paint.Style.FILL);
        }
    }

    public int getX() {
        return (int) (currentLane * laneWidth + (laneWidth - currentSprite.getWidth() * sizeMultiplier) / 2);
    }
    public int getY() {
        if (isJumping) return y - 400;
        return y - 300;
    }

    public RectF getRectF() {
        if (currentSprite != null) {
            int hitboxSize = 100; // Set the desired hitbox size
            int hitboxX = (int) (getX() + (currentSprite.getWidth() * sizeMultiplier - hitboxSize) / 2); // Center horizontally
            int hitboxY = (int) (getY() + (currentSprite.getHeight() * sizeMultiplier - hitboxSize) / 2); // Center vertically
            return new RectF(hitboxX, hitboxY, hitboxX + hitboxSize, hitboxY + hitboxSize);
        } else {
            // Handle the case where currentSprite is null (e.g., return a default rectangle)
            return new RectF(0, 0, 0, 0);
        }
    }

    public void resetPosition() {
        currentLane = 1;
        y = screenHeight - 200;
    }

    public boolean isJumping() {
        return isJumping;
    }
}