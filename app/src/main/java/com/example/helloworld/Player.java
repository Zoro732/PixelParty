package com.example.helloworld;

import android.graphics.Rect;

public class Player {
    private int y;  // Position verticale fixe pour la vue du dessus
    private final int laneWidth; // Largeur de chaque voie
    private int currentLane;  // Numéro de la voie où se trouve le joueur (0, 1 ou 2)
    private final int screenHeight; // Hauteur de l'écran pour la réinitialisation
    private final int originalSize = 100; // Taille d'origine du joueur
    private int currentSize; // Taille actuelle du joueur pendant le saut
    private boolean isJumping = false; // Indique si le joueur est en train de sauter
    private int jumpStartY; // Position de départ du saut
    private float jumpSpeed = 0; // Vitesse du saut
    private String jumpMessage = ""; // Message de saut

    public Player(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight; // Conservez la hauteur de l'écran
        this.laneWidth = screenWidth / 3;  // Divise l'écran en 3 voies
        this.y = screenHeight - 200;       // Position fixe en bas de l'écran
        this.currentLane = 1;              // Position de départ (voie centrale)
        this.currentSize = originalSize;   // Initialise la taille actuelle
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

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpStartY = y; // Enregistre la position de départ du saut
            jumpSpeed = -20; // Valeur négative pour le saut
            currentSize = (int) (originalSize * 1.5); // Augmente la taille du joueur pendant le saut
            jumpMessage = "Le personnage saute!"; // Met à jour le message
        }
    }

    public void update() {
        if (isJumping) {
            y += (int) jumpSpeed; // Met à jour la position y du joueur avec la vitesse de saut
            // Gravité qui attire le joueur vers le bas
            float gravity = 0.9f;
            jumpSpeed += gravity; // Applique la gravité

            // Vérifie si le joueur a touché le sol
            if (y >= jumpStartY) {
                y = jumpStartY; // Réinitialise à la position de départ
                isJumping = false; // Réinitialise l'état de saut
                currentSize = originalSize; // Réinitialise la taille du joueur
                jumpMessage = ""; // Réinitialiser le message
            }
        }
    }

    public int getX() {
        return currentLane * laneWidth + laneWidth / 2 - currentSize / 2;  // Calcule la position `x` dans la voie
    }

    public Rect getRect() {
        return new Rect(getX(), y, getX() + currentSize, y + currentSize); // Rectangle pour la détection de collision
    }

    public void resetPosition() {
        currentLane = 1;  // Réinitialise à la voie centrale
        y = screenHeight - 200; // Réinitialise la position verticale (en bas de l'écran)
        currentSize = originalSize; // Réinitialise la taille à la taille d'origine
    }

    public boolean isJumping() {
        return isJumping; // Retourne l'état de saut du joueur
    }
    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

}
