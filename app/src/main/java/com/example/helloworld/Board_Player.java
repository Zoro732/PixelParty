package com.example.helloworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Board_Player {

    private int caseNumber;  // Le numéro de la case où se trouve le joueur
    private float x;  // Coordonnée x du joueur (en pixels, type float pour une animation fluide)
    private float y;  // Coordonnée y du joueur (en pixels)
    private SpriteSheet spriteSheet; // Feuille de sprites pour l'animation du joueur
    private Bitmap currentSprite; // Sprite actuel du joueur
    private int currentSpriteIndex = 0; // Index de l'animation
    private int frameCounter = 0; // Compteur de frames pour l'animation
    private final int radius = 50; // Rayon du joueur
    private float targetX; // Coordonnée cible en x (en pixels)
    private float targetY; // Coordonnée cible en y (en pixels)
    private boolean isMoving = false; // Indique si le joueur est en mouvement
    private final float moveSpeed = 30f; // Vitesse de déplacement en pixels par frame
    private List<Board_Case> currentPath; // The current path the player is following
    private int currentPathIndex; // The index of the next tile in the path

    public boolean enablePlayerMovingAnimation = true;

    public Board_Player(int startingCaseNumber, Bitmap playerIdleSprite) {
        this.caseNumber = startingCaseNumber;
        this.spriteSheet = new SpriteSheet(playerIdleSprite, 1, 8);
        this.currentSprite = spriteSheet.getSprite(0, currentSpriteIndex);
        this.x = 0; // Position initiale temporaire
        this.y = 0; // Position initiale temporaire
        updatePosition(); // Mettre à jour la position initiale en fonction de la case
    }

    public void update() {
        // Gérer l'animation du sprite
        frameCounter++;
        int framesPerSprite = 1; // Changez ce nombre pour ajuster la vitesse d'animation
        if (frameCounter >= framesPerSprite) {
            frameCounter = 0;
            currentSpriteIndex = (currentSpriteIndex + 1) % spriteSheet.getCols();
            currentSprite = spriteSheet.getSprite(0, currentSpriteIndex);
        }

        // Déplacement fluide vers la case cible
        if (isMoving) {
            float dx = targetX - x;
            float dy = targetY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > moveSpeed && enablePlayerMovingAnimation) {
                // Calculer le déplacement en normalisant la direction
                x += dx / distance * moveSpeed;
                y += dy / distance * moveSpeed;
            } else {
                // Arrêter le mouvement lorsque la cible est atteinte
                x = targetX;
                y = targetY;
                isMoving = false;

            }

            if (!isMoving && currentPath != null && currentPathIndex < currentPath.size()) {
                moveToNextTileInPath();
                isMoving = true; // Continue moving to the next tile
            }

        }
    }
    public void setMovingAnimationToTargetCase (boolean value) { //if false, no animation just a Teleportation
        enablePlayerMovingAnimation = value;
    }

    public boolean hasReachedTarget() {
        if (isMoving) {
            float dx = targetX - x;
            float dy = targetY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // Check if the player is close enough to the target
            return distance < moveSpeed;
        }
        return false; // Not moving, so hasn't reached target
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public void setCaseNumber(int targetCaseNumber) {
        // Find the path to the target tile
        List<Board_Case> path = findPathToTarget(targetCaseNumber);

        // If a path is found, start moving along the path
        if (path != null && !path.isEmpty()) {
            currentPath = path;
            currentPathIndex = 0;
            isMoving = true;
            moveToNextTileInPath(); // Move to the first tile in the path
        } else {
            // No valid path found, handle accordingly (e.g., display an error message)
            Log.d("Player", "No valid path found to target case number: " + targetCaseNumber);
        }

        Board_Case currentTile = getTileByCaseNumber(caseNumber);
        if (currentTile != null && currentTile.getAction() == 1) {
            Log.d("Player","Action performed");
        }
    }

    private Board_Case getTileByCaseNumber(int caseNumber) {
        for (Board_Case tile : Board_BoardView.boardCases) {
            if (tile.getCaseNumber() == caseNumber) {
                return tile;
            }
        }
        return null;
    }

    public void updatePosition() {
        for (Board_Case gameBoardCase : Board_BoardView.boardCases) {
            if (gameBoardCase.getCaseNumber() == caseNumber) {
                // Positionner directement le joueur sur sa case actuelle (sans animation)
                this.x = gameBoardCase.getX() * Board_BoardView.cellSize + Board_BoardView.cellSize / 2;
                this.y = gameBoardCase.getY() * Board_BoardView.cellSize + Board_BoardView.cellSize / 2;
                break;
            }
        }
    }

    private List<Board_Case> getAdjacentValidTiles(Board_Case tile) {
        List<Board_Case> adjacentTiles = new ArrayList<>();
        int x = tile.getX();
        int y = tile.getY();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (Math.abs(dx) + Math.abs(dy) != 1) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < Board_BoardView.numColumns && ny >= 0 && ny < Board_BoardView.numRows && Board_BoardView.map[ny][nx] == 1) {
                    for (Board_Case gameBoardCase : Board_BoardView.boardCases) {
                        if (gameBoardCase.getX() == nx && gameBoardCase.getY() == ny) {
                            adjacentTiles.add(gameBoardCase);
                            break;
                        }
                    }
                }
            }
        }

        return adjacentTiles;
    }

    private List<Board_Case> reconstructPath(Board_Case targetTile, Map<Board_Case, Board_Case> parentMap) {
        List<Board_Case> path = new ArrayList<>();
        Board_Case currentTile = targetTile;

        while (currentTile != null) {
            path.add(0, currentTile);
            currentTile = parentMap.get(currentTile);
        }

        return path;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (currentSprite != null) {
            // Redimensionner le sprite à la taille du joueur
            int newWidth = radius * 2;
            int newHeight = radius * 2;
            Bitmap resizedSprite = Bitmap.createScaledBitmap(currentSprite, newWidth, newHeight, true);

            // Save the current canvas state
            canvas.save();

            if (this.getCaseNumber() > 17) {
                // Flip the canvas horizontally
                canvas.scale(-1, 1, x, y); // Flip around the vertical center line of the sprite
            }

            // Dessiner le sprite à la position actuelle
            canvas.drawBitmap(resizedSprite, x - radius, y - radius, paint);

            // Restore the canvas to its original state
            canvas.restore();
        }
    }

    private void moveToNextTileInPath() {
        if (currentPath != null && currentPathIndex < currentPath.size()) {
            Board_Case nextTile = currentPath.get(currentPathIndex);
            targetX = nextTile.getX() * Board_BoardView.cellSize + Board_BoardView.cellSize / 2;
            targetY = nextTile.getY() * Board_BoardView.cellSize + Board_BoardView.cellSize / 2;
            currentPathIndex++;

            // Update the player's caseNumber to the current tile's caseNumber
            caseNumber = nextTile.getCaseNumber();
        } else {
            isMoving = false;
            currentPath = null;
            currentPathIndex = 0;
        }
    }

    private List<Board_Case> findPathToTarget(int targetCaseNumber) {
        // 1. Initialization
        Queue<Board_Case> queue = new LinkedList<>();
        Map<Board_Case, Board_Case> parentMap = new HashMap<>();

        Board_Case startTile = getTileByCaseNumber(caseNumber);
        queue.offer(startTile);
        parentMap.put(startTile, null);

        // 2. Breadth-First Search
        while (!queue.isEmpty()) {
            Board_Case currentTile = queue.poll();

            if (currentTile.getCaseNumber() == targetCaseNumber) {
                return reconstructPath(currentTile, parentMap);
            }

            for (Board_Case neighbor : getAdjacentValidTiles(currentTile)) {
                if (!parentMap.containsKey(neighbor)) {
                    queue.offer(neighbor);
                    parentMap.put(neighbor, currentTile);
                }
            }
        }

        // 3. No path found
        return null;
    }

    public int getCaseNumber() {
        return caseNumber;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
