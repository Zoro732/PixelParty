package com.example.helloworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends View {

    // Liste des cases
    public static List<Case> cases;
    private final int[][] map = {
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    private final int numRows = map.length;
    private final int numColumns = map[0].length;
    private int cellSize = 100; // Taille de chaque cellule
    private Paint paint;

    private Player player;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);

        cases = new ArrayList<>();
        int caseNumber = 1;
        List<Case> availableCases = new ArrayList<>(); // Store available cases

        // Find the starting case (caseNumber 1)
        Case startingCase = null;
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (map[y][x] == 1) {
                    startingCase = new Case(x, y, 1);
                    startingCase.setCaseNumber(caseNumber++);
                    cases.add(startingCase);
                    availableCases.add(startingCase); // Add to available cases
                    break;
                }
            }
            if (startingCase != null) break; // Stop searching once found
        }

        // Generate the remaining tiles
        while (!availableCases.isEmpty()) {
            Case currentCase = availableCases.remove(0); // Get the first available case

            // Find adjacent gray squares that haven't been numbered yet
            List<Case> adjacentCases = findAdjacentGraySquares(currentCase, caseNumber);

            if (!adjacentCases.isEmpty()) {
                // Choose a random adjacent case and assign the next case number
                Case nextCase = adjacentCases.get((int) (Math.random() * adjacentCases.size()));
                nextCase.setCaseNumber(caseNumber++);
                cases.add(nextCase);
                availableCases.add(nextCase); // Add to available cases
            }
        }

        player = new Player(1);
    }

    // Helper function to find adjacent gray squares
    private List<Case> findAdjacentGraySquares(Case currentCase, int caseNumber) {
        List<Case> adjacentCases = new ArrayList<>();
        int x = currentCase.getX();
        int y = currentCase.getY();

        // Check adjacent cells (up, down, left, right)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (Math.abs(dx) + Math.abs(dy) != 1) continue; // Skip diagonals

                int nx = x + dx;
                int ny = y + dy;

                // Check if the adjacent cell is within bounds and is a gray square
                if (nx >= 0 && nx < numColumns && ny >= 0 && ny < numRows && map[ny][nx] == 1) {
                    // Check if the adjacent cell hasn't been numbered yet
                    boolean alreadyNumbered = false;
                    for (Case gameCase : cases) {
                        if (gameCase.getX() == nx && gameCase.getY() == ny) {
                            alreadyNumbered = true;
                            break;
                        }
                    }

                    if (!alreadyNumbered) {
                        adjacentCases.add(new Case(nx, ny, 1));
                    }
                }
            }
        }

        return adjacentCases;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        cellSize = screenWidth / numColumns; // Ajuster la taille des cases

        int desiredWidth = numColumns * cellSize;
        int desiredHeight = numRows * cellSize;

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dessiner les cases
        for (Case gameCase : cases) {
            drawTile(canvas, gameCase);
        }

        // Dessiner le joueur (représenté par un cercle)
        paint.setColor(Color.RED); // Couleur du joueur

        // Calculer la position du joueur sur le plateau
        int playerX = player.getX() * cellSize + cellSize / 2;  // Centrer le joueur horizontalement dans la case
        int playerY = player.getY() * cellSize + cellSize / 2;  // Centrer le joueur verticalement dans la case

        // Dessiner un cercle représentant le joueur
        canvas.drawCircle(playerX, playerY, cellSize / 3, paint);  // Utiliser cellSize / 3 pour la taille du joueur
        Log.d("Debug", "Drawing player at (" + playerX + ", " + playerY + ")");
    }

    private void drawTile(Canvas canvas, Case gameCase) {
        int x = gameCase.getX();
        int y = gameCase.getY();
        int tileValue = gameCase.getValue();
        int left = x * cellSize;
        int top = y * cellSize;
        int right = left + cellSize;
        int bottom = top + cellSize;

        // Changer la couleur en fonction de la valeur de la tuile
        if (tileValue == 0) {
            paint.setColor(Color.WHITE); // Couleur pour les tuiles '0' (vide)
        } else if (tileValue == 1) {
            paint.setColor(Color.DKGRAY); // Couleur pour les tuiles '1' (obstacle)
        } else {
            paint.setColor(Color.WHITE); // Par défaut, couleur blanche
        }

        // Dessiner la case
        canvas.drawRect(left, top, right, bottom, paint);

        // Afficher le numéro uniquement pour les cases grises (obstacles)
        if (tileValue == 1) {
            paint.setColor(Color.BLACK); // Couleur du texte (numéro de la case)
            canvas.drawText(String.valueOf(gameCase.getCaseNumber()), left + cellSize / 2, top + cellSize / 1.5f, paint);
        }
    }

    public void movePlayer() {
        player.moveToNextCase();
        Log.d("Debug", "Moving player to case ");
        invalidate();  // Redessiner la vue pour afficher la nouvelle position du joueur
    }
}
