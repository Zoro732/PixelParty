package com.example.helloworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("ViewConstructor")
public class RunGame_GameView extends SurfaceView implements Runnable {
    private boolean isPlaying = true;
    private final int screenWidth, screenHeight;
    private final Paint paint;
    private final RunGame_Player runGamePlayer;
    static int laneWidth;
    private final List<RunGame_Coin> runGameCoinPool = new ArrayList<>();
    private int score = 0; // Score de pièces collectées
    static float obstacleSpeed = 15;
    private float startX = 0; // Initialisation de startX
    private float startY = 0; // Initialisation de startY
 final List<RunGame_Obstacle> runGameObstaclePool = new ArrayList<>();
    private final Bitmap[] vehicles;
    private final Bitmap coins;
    private long lastSpeedIncreaseTime = System.currentTimeMillis(); // Initialisation du temps pour l'augmentation de vitesse
    private Canvas canvas = new Canvas();
    private boolean isDead = false;

    // Ajoutez ce tableau pour suivre le nombre d'obstacles consécutifs par voie
    private int[] consecutiveObstacles = new int[3]; // 0 = voie 1, 1 = voie 2, 2 = voie 3


    public RunGame_GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        laneWidth = screenWidth / 3;
        paint = new Paint();

        // Récupération des sprite avec plusieur images dedans
        Bitmap runSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.run);
        Bitmap jumpSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.jump);

        // Creation de l'objet joueur
        runGamePlayer = new RunGame_Player(screenWidth, screenHeight, runSpriteSheet, jumpSpriteSheet);

        // Charger les images des voitures
        vehicles = new Bitmap[3];
        vehicles[0] = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car1), 100);
        vehicles[1] = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car2), 100);
        vehicles[2] = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car3), 100);


        generateObstacles();

        // Charger les images des pièces
        coins = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        generateCoins();
    }

    private boolean isCoinOverlappingWithObstacle(RunGame_Coin coin) {
        RectF coinRect = coin.getRect();
        for (RunGame_Obstacle obstacle : runGameObstaclePool) {
            RectF obstacleRect = obstacle.getRect();
            if (RectF.intersects(coinRect, obstacleRect)) {
                return true; // La pièce chevauche un obstacle
            }
        }
        return false; // Aucune intersection
    }


    private Bitmap resizeBitmap(Bitmap originalBitmap, int newWidth) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        float aspectRatio = (float) originalHeight / (float) originalWidth;
        int newHeight = Math.round(newWidth * aspectRatio); // Calculer la nouvelle hauteur pour garder le ratio
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }

    private Bitmap rotateBitmap(Bitmap originalBitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f); // Pivoter autour du centre
        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }

    private RunGame_Coin createCoin() {
        List<Integer> availableLanes = new ArrayList<>(Arrays.asList(0, 1, 2));
        int laneIndex = availableLanes.remove((int) (Math.random() * availableLanes.size()));
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;  // Calcul de la position X de la voie

        // Créer une nouvelle pièce
        RunGame_Coin newCoin = new RunGame_Coin(laneX, -100, coins);

        // Vérifier si la pièce chevauche un obstacle, et si c'est le cas, générer une nouvelle pièce
        while (isCoinOverlappingWithObstacle(newCoin)) {
            laneIndex = availableLanes.remove((int) (Math.random() * availableLanes.size()));
            laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;
            newCoin = new RunGame_Coin(laneX, -100, coins);
        }

        return newCoin;
    }


    private void generateCoins() {
        int distanceBetweenCoins = 400;
        int currentY = -100; // Position de départ pour les obstacles
        int numberOfCoinsPerGroup = 5;  // Par exemple, génère entre 1 et 5 groupes de pièces
        List<RunGame_Coin> newRunGameCoin = new ArrayList<>();

        for (int i = 0; i < numberOfCoinsPerGroup; i++) {
            RunGame_Coin runGameCoin = createCoin();
            runGameCoin.setY(currentY);
            newRunGameCoin.add(runGameCoin);

            // Générer un espacement aléatoire pour chaque obstacle
            currentY -= distanceBetweenCoins;
        }

        // Ajouter tous les nouveaux obstacles générés à la pool
        runGameCoinPool.addAll(newRunGameCoin);
    }





    private RunGame_Obstacle createObstacle() {
        List<Integer> availableLanes = new ArrayList<>(Arrays.asList(0, 1, 2));
        int laneIndex = -1;

        // Essayer de choisir une voie qui ne contient pas plus de 2 obstacles consécutifs
        for (int i = 0; i < 3; i++) {
            // Si une voie a moins de 3 obstacles consécutifs, elle est disponible
            if (consecutiveObstacles[i] < 3) {
                laneIndex = i;
                break;
            }
        }

        if (laneIndex == -1) {
            // Si toutes les voies ont 3 obstacles consécutifs, nous les réinitialisons pour éviter de bloquer la génération
            Arrays.fill(consecutiveObstacles, 0);
            laneIndex = availableLanes.get((int) (Math.random() * availableLanes.size()));
        }

        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;

        // Sélectionner aléatoirement une voiture ou un camion comme obstacle
        Bitmap[] obstacleImages = vehicles;
        Bitmap randomImage = obstacleImages[(int) (Math.random() * obstacleImages.length)];

        RunGame_Obstacle newObstacle = new RunGame_Obstacle(laneX, -100, randomImage, vehicles);

        // Mettre à jour le nombre d'obstacles consécutifs pour cette voie
        consecutiveObstacles[laneIndex]++;

        return newObstacle;
    }

    // Lors de la réinitialisation d'un obstacle hors écran, vous devrez décrémenter le nombre d'obstacles consécutifs
    private void resetObstacle(RunGame_Obstacle obstacle) {
        int laneIndex = -1;
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;  // Calcul de la position X de la voie


        // Trouver la voie de l'obstacle
        for (int i = 0; i < vehicles.length; i++) {
            if (obstacle.getImage() == vehicles[i]) {
                laneIndex = i;
                break;
            }
        }

        // Réinitialiser le compteur d'obstacles consécutifs
        if (laneIndex != -1) {
            consecutiveObstacles[laneIndex]--;
        }

        // Réinitialiser la position de l'obstacle
        obstacle.reset(laneX);  // Méthode pour réinitialiser l'obstacle sans le recréer
    }


    private void generateObstacles() {
        int numObstacles = 6; // Nombre aléatoire d'obstacles
        int currentY = -100; // Position de départ pour les obstacles
        List<RunGame_Obstacle> newRunGameObstacles = new ArrayList<>();

        for (int i = 0; i < numObstacles; i++) {
            RunGame_Obstacle runGameObstacle = createObstacle();
            runGameObstacle.setY(currentY);
            newRunGameObstacles.add(runGameObstacle);

            // Générer un espacement aléatoire pour chaque obstacle
            int spacing = 501 + (int) (Math.random() * 301);
            currentY -= 1000;

        }

        // Ajouter tous les nouveaux obstacles générés à la pool
        runGameObstaclePool.addAll(newRunGameObstacles);
    }

    private boolean checkOverlapping(RectF firstItem, RectF secondItem) {
        return RectF.intersects(firstItem, secondItem);
    }

    @Override
    public void run() {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (isPlaying) {
                    update(); // Mise à jour des objets du jeu
                    draw();   // Dessiner le jeu
                    Choreographer.getInstance().postFrameCallback(this); // Planifier le prochain frame
                }
            }
        });
    }



    public void quitGame() {
        runGameObstaclePool.clear();
        runGameCoinPool.clear();
        score = 0;
        isPlaying = false;
        // Fermer le jeu ou revenir à un autre écran
        // Par exemple, dans une application Android, vous pourriez vouloir fermer cette activité:
        ((Activity) getContext()).finish();
    }

    // For debug
    private void drawBorder(Canvas canvas, RectF rect, Paint paint) {
        paint.setStyle(Paint.Style.STROKE); // Style de peinture pour les contours
        paint.setStrokeWidth(5); // Largeur de la bordure
        paint.setColor(Color.RED); // Couleur de la bordure
        canvas.drawRect(rect, paint);
        paint.setStyle(Paint.Style.FILL); // Remettre le style de peinture à remplissage après
    }

    private void update() {
        // Mettre à jour l'état du joueur
        runGamePlayer.update();
        long currentTime = System.currentTimeMillis();

        // Préparer le rectangle du joueur pour détecter les collisions
        RectF playerRect = runGamePlayer.getRectF();

        // Augmentation de la vitesse des obstacles tous les 800 ms si la vitesse n'a pas atteint le maximum
        if (currentTime - lastSpeedIncreaseTime >= 800 && obstacleSpeed < 100) {
            obstacleSpeed += (float) score / 7;
            lastSpeedIncreaseTime = currentTime;
        }
        Log.d("RunGame_GameView", "Obstacle speed: " + obstacleSpeed);

        if (obstacleSpeed > 40) {
            obstacleSpeed = 40;
        }

        RectF tempRect;

        List<Integer> availableLanes = new ArrayList<>(Arrays.asList(0, 1, 2));
        int laneIndex = availableLanes.remove((int) (Math.random() * availableLanes.size()));
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;


        // Mise à jour des obstacles et vérification de collision
        for (int i = runGameObstaclePool.size() - 1; i >= 0; i--) {
            RunGame_Obstacle obstacle = runGameObstaclePool.get(i);
            obstacle.update();

            // Si l'obstacle est hors écran, réutiliser en réinitialisant sa position
            if (obstacle.isOffScreen(screenHeight)) {
                obstacle.reset(laneX);  // Méthode pour réinitialiser l'obstacle sans le recréer
            }

            // Détection des collisions
            tempRect = obstacle.getRect();
            if (RectF.intersects(playerRect, tempRect)) {
                if (obstacle.isJumpable() && runGamePlayer.isJumping()) {
                    // Ignore collision
                } else {
                    isDead = true;
                    endGame();
                    return;
                }
            }
        }
        // Mettre à jour les pièces
        for (int i = runGameCoinPool.size() - 1; i >= 0; i--) {
            RunGame_Coin coin = runGameCoinPool.get(i);
            coin.update();

            // Si la pièce sort de l'écran, réutiliser en haut
            if (coin.isOffScreen(screenHeight)) {
                resetCoin(coin);
            }

            // Si le joueur collecte une pièce, la réinitialiser et augmenter le score
            if (checkOverlapping(runGamePlayer.getRectF(), coin.getRect())) {
                score++;
                resetCoin(coin);
            }
        }
    }

    public int getScore(){
        return score;
    }


    // Méthodes pour dessiner les éléments séparément
    private void drawBackground() {
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        // Lignes blanches
        paint.setColor(Color.WHITE);
        int laneWidth = canvas.getWidth() / 3;
        int lineLength = 300;
        int gapLength = 130;

        for (int laneIndex = 1; laneIndex < 3; laneIndex++) {
            int lineX = laneIndex * laneWidth;
            for (int y = 0; y < canvas.getHeight(); y += lineLength + gapLength) {
                canvas.drawLine(lineX, y, lineX, y + lineLength, paint);
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            canvas = getHolder().lockCanvas();
            // Dessiner le fond
            drawBackground();
            // Dessiner les éléments en fonction de l'état de jeu
            if (isPlaying) {
                drawObstacles();
                drawCoins();
                runGamePlayer.draw(canvas, paint);
            } else if (isDead) {
                drawGameOver(canvas); // Appel à l'écran de fin
            }
            drawScore(); // Afficher le score, peu importe l'état du jeu

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void resetCoin(RunGame_Coin coin) {
        int laneIndex = (int) (Math.random() * 3); // Choisir une voie aléatoire
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - coin.getWidth() / 2; // Position dans la voie
        coin.reset(laneX); // Réinitialiser la position de la pièce
        coin.setY(-100); // Repositionner en haut de l'écran
    }


    private void drawObstacles() {
        for (RunGame_Obstacle runGameObstacle : runGameObstaclePool) {
            if (runGameObstacle.getImage() != null && !runGameObstacle.getImage().isRecycled()) {
                canvas.drawBitmap(runGameObstacle.getImage(), runGameObstacle.getX(), runGameObstacle.getY(), paint);
            }
        }
    }

    private void drawCoins() {
        for (RunGame_Coin runGameCoin : runGameCoinPool) {
            runGameCoin.draw(canvas, paint);
            //canvas.drawBitmap(runGameCoin.getImage(), runGameCoin.getX(), runGameCoin.getY(), paint);

        }
    }

    private void drawScore() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.press2start);
        paint.setTypeface(customFont);
        canvas.drawText("Score: " + score, (float) screenWidth / 2, 100, paint);
    }

    private void drawGameOver(Canvas canvas) {

        paint.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setColor(Color.RED);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over", (float) screenWidth / 2, (float) screenHeight / 3, paint);

        // Afficher le score
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, (float) screenWidth / 2, (float) screenHeight / 2 + 50, paint); // Affichage du score

        // Bouton Rejouer
        paint.setColor(Color.BLUE);
        int buttonWidth = 400;
        int buttonHeight = 150;
        int buttonX = screenWidth / 2 - buttonWidth / 2;
        int buttonY = screenHeight / 2 + 100; // Positionner le bouton en dessous du score
        canvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);

        // Texte "Rejouer"
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Rejouer", (float) screenWidth / 2, buttonY + (float) buttonHeight / 2 + 20, paint);
    }

    void endGame() {
        isPlaying = false;
        runGameCoinPool.clear(); // Supprimer toutes les pièces
        runGameObstaclePool.clear();
        obstacleSpeed = 20;
        draw();
    }

    private void sleep() {
        try {
            Thread.sleep(10); // Environ 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        run();
    }

    public void pause() {
        isPlaying = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPlaying) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Calculer les coordonnées du bouton "Rejouer"
                int buttonWidth = 400;
                int buttonHeight = 150;
                int buttonX = screenWidth / 2 - buttonWidth / 2;
                int buttonY = screenHeight / 2 + 100;

                // Vérifier si l'utilisateur a touché le bouton
                if (event.getX() >= buttonX && event.getX() <= buttonX + buttonWidth &&
                        event.getY() >= buttonY && event.getY() <= buttonY + buttonHeight) {
                    restartGame();
                }
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Enregistrer les positions initiales du toucher
                    startX = event.getX();
                    startY = event.getY();
                    // Réinitialiser l'indicateur de vibration
                    break;

                case MotionEvent.ACTION_MOVE:
                    // Vérifier si l'utilisateur effectue un mouvement vers le haut
                    float deltaY = startY - event.getY(); // Mouvement vertical
                    if (deltaY > 100) { // Si le mouvement vers le haut dépasse 100 pixels
                        runGamePlayer.jump(); // Effectuer le saut
                        startY = event.getY(); // Réinitialiser pour éviter un double saut
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    // Réinitialiser l'indicateur de vibration pour le prochain mouvement

                    // Déterminer si le mouvement final est vers la gauche ou la droite
                    float endX = event.getX();
                    float distanceX = endX - startX;

                    if (Math.abs(distanceX) > 100) { // Ajuster la sensibilité
                        if (distanceX < 0) {
                            runGamePlayer.moveLeft();
                        } else if (distanceX > 0) {
                            runGamePlayer.moveRight();
                        }
                    }
                    break;
            }
        }
        return true;
    }

    void restartGame() {
        score = 0;
        isDead = false;
        runGamePlayer.resetPosition(); // Reset player position
        runGameCoinPool.clear(); // Clear coins
        runGameObstaclePool.clear(); // Clear obstacles
        obstacleSpeed = 20; // Reset obstacle speed

        generateObstacles(); // Generate new obstacles
        generateCoins(); // Generate new coins

        isPlaying = true;
        run();
    }

    public boolean isDead() {
        return isDead;
    }
}
