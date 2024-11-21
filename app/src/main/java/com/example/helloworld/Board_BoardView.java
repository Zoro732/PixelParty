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
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 1 = Laby, 2 = Run, 3 = Taquin, Shop = 4 & MiniBoss = 5
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4},
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
    private Bitmap currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_purple);
    private Typeface font = ResourcesCompat.getFont(getContext(), R.font.press2start);

    private final Handler animationHandler = new Handler(Looper.getMainLooper());

    private String playerSprite;

    private boolean isPlayerFinishedMoving = false;


    public Board_BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setPlayerSpriteSelection (String selection){
        this.playerSprite = selection;
        if (playerSprite != null) {
            switch (playerSprite){
                case "Rouge":
                    currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_red);
                    break;
                case "Violet":
                    currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_purple);
                    break;
                case "Bleu":
                    currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_blue);
                    break;
            }
        } else {
            Log.d("Board_BoardView","playerSprite is null");
        }
        Log.d("Board_BoardView", "END Received selection in setPlayerSpriteSelection: " + playerSprite);
        boardPlayer = new Board_Player(0, currentPlayerBitmap);

    }
    public String getPlayerSpriteSelection(){
        return this.playerSprite;
    }


    private void init() {
        // Initialize paints for drawing
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);

        // Initialize list for storing cases on the board
        boardCases = new ArrayList<>();
        int caseNumber = 0;

        // BFS setup: List of cases to explore, starting with an empty list
        List<Board_Case> queue = new ArrayList<>();
        Board_Case startingBoardCase = null; // Variable to hold the starting case

        // Find the starting case (caseNumber 0)
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (map[y][x] == 1) { // Only for valid tiles
                    // Initialize the case with its coordinates and initial action
                    startingBoardCase = new Board_Case(x, y, 1, mapAction[y][x]);
                    startingBoardCase.setCaseNumber(caseNumber++); // Assign a number to the case
                    boardCases.add(startingBoardCase); // Add the starting case to the list
                    queue.add(startingBoardCase); // Add it to the BFS queue
                    break;
                }
            }
            if (startingBoardCase != null) break; // Stop searching once found
        }

        // Use BFS to number remaining cases and assign actions
        while (!queue.isEmpty()) {
            Board_Case currentBoardCase = queue.remove(0); // Get the next case to explore

            // Find adjacent valid tiles that haven't been numbered yet
            List<Board_Case> adjacentBoardCases = findAdjacentValidCase(currentBoardCase);

            for (Board_Case nextBoardCase : adjacentBoardCases) {
                nextBoardCase.setCaseNumber(caseNumber++); // Number the new case
                nextBoardCase.setAction(mapAction[nextBoardCase.getY()][nextBoardCase.getX()]); // Assign the correct action
                boardCases.add(nextBoardCase); // Add the case to the board list
                queue.add(nextBoardCase); // Add to the queue for further exploration
            }
        }

        // Load dice bitmap and set up player
        diceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice_6);
        diceSpriteSheet = new SpriteSheet(diceBitmap, 1, 6);


        animationHandler.post(animationRunnable); // Start animation loop for player

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop the animation loop when the view is detached
        animationHandler.removeCallbacks(animationRunnable);
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            boardPlayer.update();
            invalidate(); // Trigger a redraw
            animationHandler.postDelayed(this, 100); // Adjust the delay for animation speed

            if (!getIsPlayerMoving()) {
                Log.d("Board_BoardView","Player is not moving");
            }
        }
    };


    // Helper function to find adjacent gray squares
    private List<Board_Case> findAdjacentValidCase(Board_Case currentBoardCase) {
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
                        adjacentBoardCases.add(new Board_Case(nx, ny, 1, 0));
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
        //boardPlayer.update();

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

    public boolean isDiceRolling() {
        return isRolling;
    }

    private void drawTile(Canvas canvas, Board_Case gameBoardCase) {
        int x = gameBoardCase.getX();
        int y = gameBoardCase.getY();
        int left = x * cellSize;
        int top = y * cellSize;
        int bottom = top + cellSize;
        int right = left + cellSize;
        int tileValue = gameBoardCase.getValue();

        // Dessiner la couleur de la case en fonction de sa valeur
        if (tileValue == 1) {
            paint.setColor(Color.parseColor("#8B4513")); // terre
        } else {
            paint.setColor(Color.parseColor("#228B22")); // herbe
        }

        // Dessiner la case
        canvas.drawRect(left, top, right, bottom, paint);

        // Afficher le texte si nécessaire (uniquement pour les cases valides)
        if (gameBoardCase.getValue() == 1) {
            paint.setTypeface(font);

            // Définir la couleur du texte en fonction de l'action
            switch (gameBoardCase.getAction()) {
                case 1:
                    paint.setColor(Color.BLUE);
                    break;
                case 2:
                    paint.setColor(Color.RED);
                    break;
                case 3:
                    paint.setColor(Color.GREEN);
                    break;
                case 4:
                    paint.setColor(Color.MAGENTA);
                    break;
                default:
                    paint.setColor(Color.WHITE);
            }

            // Positionner le texte au centre de la case
            paint.setTextAlign(Paint.Align.CENTER);
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
            boardPlayer.setCaseNumber(targetCaseNumber); // Move player
            onPlayerMovementFinished(); // Call the new method
        }


    }
    public void onPlayerMovementFinished() {
        isPlayerFinishedMoving = true;
    }

    private void sleep() {
        try {
            Thread.sleep(16); // 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentCaseAction() {
        return boardCases.get(boardPlayer.getCaseNumber()).getAction();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isRolling) {
            stopDiceRoll();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public int getPlayerCaseNumber() {
        return boardPlayer.getCaseNumber();
    }

    public void setPlayerCaseNumber(int caseNumber) {
        boardPlayer.setCaseNumber(caseNumber);
    }

    public void setPlayerMovingAnimationToTargetCase(boolean value) {
        boardPlayer.setMovingAnimationToTargetCase(value);
    }

    public boolean getIsPlayerMoving() {
        return boardPlayer.isMoving();
    }

    public void setPlayerFinishedMoving(boolean isPlayerFinishedMoving) {
        this.isPlayerFinishedMoving = isPlayerFinishedMoving;
    }

}
