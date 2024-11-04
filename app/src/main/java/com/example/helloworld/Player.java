package com.example.helloworld;

import android.graphics.Rect;

public class Player {
    private int y;  // Gardons `y` constant pour la vue du dessus
    private int laneWidth;
    private int currentLane;  // Numéro de la voie où se trouve le joueur (0, 1 ou 2)

    public Player(int screenWidth, int screenHeight) {
        this.laneWidth = screenWidth / 3;  // Divise l'écran en 3 voies
        this.y = screenHeight - 200;       // Position fixe en bas de l'écran
        this.currentLane = 1;              // Position de départ (voie centrale)
    }

    public void moveLeft() {
        if (currentLane > 0) {
            currentLane--;  // Déplace à gauche
        }
    }

    public void moveRight() {
        if (currentLane < 2) {
            currentLane++;  // Déplace à droite
        }
    }

    public int getX() {
        return currentLane * laneWidth + laneWidth / 2 - 50;  // Calcule la position `x` dans la voie
    }

    public Rect getRect() {
        return new Rect(getX(), y, getX() + 100, y + 100);
    }

    public void resetPosition() {
        currentLane = 1;  // Réinitialise à la voie centrale
    }

}

