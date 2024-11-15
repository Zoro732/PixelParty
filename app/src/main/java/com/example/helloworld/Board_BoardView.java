package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board_BoardView extends View {

    // Liste des cases
    public static List<Board_Case> boardCases;
    public static int[][] map = {
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    public static int[][] mapAction = {
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    public static final int numRows = map.length;
    public static final int numColumns = map[0].length;
    public static int cellSize = 100; // Taille de chaque cellule
    private Paint paint;

    private Board_Player boardPlayer;

    private Paint textPaint = new Paint();
    private Paint borderPaint = new Paint();
    private String text = "Texte au milieu de l'écran";
    private int diceResult = 0; // Variable to store the dice roll result
    private boolean isRolling = false; // Flag to track if the dice is rolling
    private final Random random = new Random();
    private SpriteSheet diceSpriteSheet;
    private Bitmap diceBitmap;
    private Bitmap playerIdleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_purple);
    private Typeface font =  ResourcesCompat.getFont(getContext(), R.font.press2start);;
    private final Handler animationHandler = new Handler(Looper.getMainLooper());

    public Board_BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            boardPlayer.update();
            invalidate(); // Trigger a redraw
            animationHandler.postDelayed(this, 100); // Adjust the delay for animation speed
        }
    };

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);

        boardCases = new ArrayList<>();
        int caseNumber = 0;
        List<Board_Case> availableBoardCases = new ArrayList<>(); // Store available cases

        // Combine the loops to initialize both the case and its action
        Board_Case startingBoardCase = null; // Variable to hold the starting case
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (map[y][x] == 1) { // Only for valid tiles
                    // Initialize the case
                    Board_Case gameBoardCase = new Board_Case(x, y, 1, 0); // Create new case with x, y, value 1, and initial action 0
                    gameBoardCase.setCaseNumber(caseNumber++); // Assign a number to the case
                    boardCases.add(gameBoardCase); // Add case to the list of all cases

                    // Assign the action to the case
                    int action = mapAction[y][x]; // Get the action for this case
                    gameBoardCase.setAction(action); // Set the action
                    Log.d("Debug", "Action set for case " + gameBoardCase.getCaseNumber() + ": " + gameBoardCase.getAction());

                    availableBoardCases.add(gameBoardCase); // Add to available cases

                    // If it's the starting case, store it
                    if (startingBoardCase == null) {
                        startingBoardCase = gameBoardCase; // Store the first valid case as starting case
                    }
                }
            }
        }

        // Generate the remaining tiles
        while (!availableBoardCases.isEmpty()) {
            Board_Case currentBoardCase = availableBoardCases.remove(0); // Get the first available case

            // Find adjacent gray squares that haven't been numbered yet
            List<Board_Case> adjacentBoardCases = findAdjacentGraySquares(currentBoardCase, caseNumber);

            if (!adjacentBoardCases.isEmpty()) {
                // Choose a random adjacent case and assign the next case number
                Board_Case nextBoardCase = adjacentBoardCases.get((int) (Math.random() * adjacentBoardCases.size()));
                nextBoardCase.setCaseNumber(caseNumber++);
                boardCases.add(nextBoardCase);
                availableBoardCases.add(nextBoardCase); // Add to available cases
            }
        }

        diceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice_6);
        diceSpriteSheet = new SpriteSheet(diceBitmap, 1, 6);

        boardPlayer = new Board_Player(0, playerIdleBitmap);
        animationHandler.post(animationRunnable);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop the animation loop when the view is detached
        animationHandler.removeCallbacks(animationRunnable);
    }

    // Helper function to find adjacent gray squares
    private List<Board_Case> findAdjacentGraySquares(Board_Case currentBoardCase, int caseNumber) {
        List<Board_Case> adjacentBoardCases = new ArrayList<>();
        int x = currentBoardCase.getX();
        int y = currentBoardCase.getY();

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
                    for (Board_Case gameBoardCase : boardCases) {
                        if (gameBoardCase.getX() == nx && gameBoardCase.getY() == ny) {
                            alreadyNumbered = true;
                            break;
                        }
                    }

                    if (!alreadyNumbered) {
                        adjacentBoardCases.add(new Board_Case(nx, ny, 1,0));
                    }
                }
            }
        }

        return adjacentBoardCases;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        cellSize = screenWidth / numColumns; // Adjust tile size

        int desiredWidth = numColumns * cellSize;
        int desiredHeight = numRows * cellSize;

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the tiles
        for (Board_Case gameBoardCase : boardCases) {
            drawTile(canvas, gameBoardCase);
        }

        // Draw the player
        boardPlayer.draw(canvas, paint);
        boardPlayer.update();

        if (isRolling || diceResult != 0) {
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(60);
            textPaint.setTextAlign(Paint.Align.CENTER);

            Bitmap originalBitmap = diceSpriteSheet.getSprite(0, diceResult - 1);

            int scaledWidth = originalBitmap.getWidth() * 2;
            int scaledHeight = originalBitmap.getHeight() * 2;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, false);

            if (scaledBitmap == null || scaledBitmap.getWidth() != scaledWidth || scaledBitmap.getHeight() != scaledHeight) {
                if (scaledBitmap != null) {
                    scaledBitmap.recycle();
                }
                scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
            }

            canvas.drawBitmap(scaledBitmap, getWidth() / 2 - scaledWidth / 2, getHeight() / 2 - scaledHeight / 2, paint);
        }
    }

    private void drawTile(Canvas canvas, Board_Case gameBoardCase) {
        int x = gameBoardCase.getX();
        int y = gameBoardCase.getY();
        int tileValue = gameBoardCase.getValue();
        int left = x * cellSize;
        int top = y * cellSize;
        int right = left + cellSize;
        int bottom = top + cellSize;

        // Dessiner la couleur de la case en fonction de sa valeur
        if (tileValue == 0) {
            paint.setColor(Color.WHITE); // Cases vides
        } else if (tileValue == 1) {
            paint.setColor(Color.DKGRAY); // Cases valides
        } else {
            paint.setColor(Color.WHITE); // Couleur par défaut
        }

        // Dessiner la case
        canvas.drawRect(left, top, right, bottom, paint);

        // Si la case est valide (tileValue == 1), afficher le numéro et l'action
        if (tileValue == 1) {
            paint.setTypeface(font); // Appliquer la police définie
            // Définir la couleur du texte en fonction de l'action
            if (gameBoardCase.getAction() == 1) {
                paint.setColor(Color.BLUE); // Texte bleu pour les cases avec action 1
            } else {
                paint.setColor(Color.WHITE); // Texte blanc pour les autres
            }

            // Positionner le texte au centre de la case
            float textX = left + cellSize / 2;
            float textY = bottom - paint.descent() - 10;

            // Afficher le numéro de la case
            canvas.drawText(String.valueOf(gameBoardCase.getCaseNumber()), textX, textY, paint);

        }
    }


    public void startDiceRoll() {
        isRolling = true;
        new Thread(() -> {
            while (isRolling) {
                diceResult = random.nextInt(6) + 1;
                postInvalidate();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopDiceRoll() {
        isRolling = false;
        // Only move if the target tile is valid (1)
        movePlayer();
        invalidate();
    }

    // Method to move the player to the valid tile
    private void movePlayer() {
        int targetCaseNumber = boardPlayer.getCaseNumber() + diceResult;
        Board_Case targetBoardCase = null;

        // Find the target case with the corresponding case number
        for (Board_Case gameBoardCase : boardCases) {
            if (gameBoardCase.getCaseNumber() == targetCaseNumber) {
                targetBoardCase = gameBoardCase;
                break;
            }
        }

        // Move only if the target case is valid (value == 1)
        if (targetBoardCase != null && targetBoardCase.getValue() == 1) {
            boardPlayer.setCaseNumber(targetCaseNumber);
            // Check if the action of the target case is 1
            if (targetBoardCase.getAction() == 1) {
                // Display a toast message if the action is 1
                Toast.makeText(getContext(), "You landed on a special case!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Invalid move, show a toast or handle the invalid move logic
            Toast.makeText(getContext(), "Invalid move!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isRolling) {
            stopDiceRoll();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
