package com.example.helloworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
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

    private int numberOfMapCase = -1;
    public static int[][] mapAction = {
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 2, 0, 3, 2, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 0, 3, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3},
            {0, 0, 0, 0, 0, 1, 1, 3, 0, 1, 2, 2, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 5, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // FOR DEBUG OF COURSE
//    public static int[][] mapAction = {
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//    };

    public static final int numRows = map.length;
    public static final int numColumns = map[0].length;
    public static int cellSize = 100; // Taille de chaque cellule
    private Paint paint;
    private Board_Player boardPlayer;

    private int diceResult = 0; // Variable to store the dice roll result
    public boolean isRolling = false; // Flag to track if the dice is rolling
    private final Random random = new Random();
    private SpriteSheet diceSpriteSheet;
    private Bitmap currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_purple);
    private final Typeface font = ResourcesCompat.getFont(getContext(), R.font.press2start);

    private final Handler animationHandler = new Handler(Looper.getMainLooper());

    public int itemAction = 0; // 0=no action pending, 1 = dice +1, -1= dice -1 of oppenent

    private Bitmap devilHeadBitmap;


    public Board_BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Storing number of cases of the map
        for (int[] ints : map) {
            for (int anInt : ints) {
                if (anInt == 1) {
                    numberOfMapCase++; // Increment counter
                }
            }
        }
        init();
    }


    public void setItemAction(int ItemAction) {
        this.itemAction = ItemAction;
    }

    public void setPlayerSpriteSelection(String selection) {
        if (selection != null) {
            switch (selection) {
                case "Red":
                    currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_red);
                    break;
                case "Purple":
                    currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_purple);
                    break;
                case "Blue":
                    currentPlayerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player_move_blue);
                    break;
            }

        }
        boardPlayer = new Board_Player(0, currentPlayerBitmap);
    }

    private void init() {

        devilHeadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.devil_head);

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
        Bitmap diceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dice_6);
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

        if (isRolling || diceResult != 0) {

            Bitmap originalBitmap = diceSpriteSheet.getSprite(0, diceResult - 1);
            int scaledWidth = originalBitmap.getWidth() * 2;
            int scaledHeight = originalBitmap.getHeight() * 2;
            @SuppressLint("DrawAllocation") Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, false);

            if (scaledBitmap == null || scaledBitmap.getWidth() != scaledWidth || scaledBitmap.getHeight() != scaledHeight) {
                if (scaledBitmap != null) {
                    scaledBitmap.recycle();
                }
                scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
            }

            canvas.drawBitmap(scaledBitmap, (float) getWidth() / 2 - (float) scaledWidth / 2, (float) getHeight() / 2 - (float) scaledHeight / 2, paint);
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

        // Définir les paramètres de la peinture pour les bordures
        paint.setStyle(Paint.Style.STROKE); // Dessiner uniquement les contours
        paint.setStrokeWidth(10); // Épaisseur des contours
        paint.setColor(gameBoardCase.getCaseNumber() == 30 ? Color.RED : Color.WHITE);

        // Dessiner les contours de la case
        canvas.drawRect(left, top, right, bottom, paint);

        // Draw the devil head on case 30
        if (gameBoardCase.getCaseNumber() == 30) {
            int textSize = (int) paint.getTextSize();
            Bitmap scaledDevilHead = Bitmap.createScaledBitmap(devilHeadBitmap, textSize, textSize, false);
            int bitmapLeft = left + (cellSize - textSize) / 2;
            int bitmapTop = top + (cellSize - textSize) / 2;
            canvas.drawBitmap(scaledDevilHead, bitmapLeft, bitmapTop, paint);
        }

        // Afficher le texte si nécessaire (uniquement pour les cases valides)
        if (gameBoardCase.getValue() == 1) {
            paint.setTypeface(font);
            paint.setStyle(Paint.Style.FILL); // Revenir au style de remplissage pour le texte

            // Définir la couleur du texte en fonction de l'action
            switch (gameBoardCase.getAction()) {
                case 1:
                    paint.setColor(Color.YELLOW);
                    break;
                case 2:
                    paint.setColor(Color.CYAN);
                    break;
                case 3:
                    paint.setColor(Color.GREEN);
                    break;
                case 4:
                    paint.setColor(Color.MAGENTA);
                    break;
                case 5:
                    paint.setColor(Color.RED);
                    break;
                default:
                    paint.setColor(Color.WHITE);
            }

            // Positionner le texte au centre de la case
            paint.setTextAlign(Paint.Align.CENTER);
            float textX = left + (float) cellSize / 2;
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
        int targetCaseNumber;
        if (itemAction != 0) {
            targetCaseNumber = boardPlayer.getCaseNumber() + diceResult + itemAction;
            itemAction = 0;
        } else {
            targetCaseNumber = boardPlayer.getCaseNumber() + diceResult;
        }

        if (diceResult > (numberOfMapCase - boardPlayer.getCaseNumber())) {
            targetCaseNumber = numberOfMapCase;
        }

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
        }
    }



    public int getCurrentCaseAction() {
        return boardCases.get(boardPlayer.getCaseNumber()).getAction();
    }

    @SuppressLint("ClickableViewAccessibility")
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
        return !boardPlayer.isMoving();
    }

}
