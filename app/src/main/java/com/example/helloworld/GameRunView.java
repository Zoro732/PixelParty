package com.example.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameRunView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private int screenWidth;
    private int screenHeight;
    private Paint paint;
    private Player player;
    static int laneWidth;
    private List<Coin> coinPool = new ArrayList<>();
    private int score = 0; // Score de pièces collectées
    static float obstacleSpeed = 30;
    private float startX = 0; // Initialisation de startX
    private float startY = 0; // Initialisation de startY
    private float jumpThreshold = 1.0f; // Seuil pour détecter le saut
    private boolean isJumping = false;
    private static Vibrator vibrator; // Déclaration de l'objet Vibrator
    private String jumpMessage = "";
    private int obstacleSpacing = 1000; // Initial spacing
    private long lastObstacleTime = 0; // Time of last obstacle creation
    private long lastCoinTime = 0;
    private List<Obstacle> obstaclePool = new ArrayList<>();
    private Bitmap backgroundBitmap; // For background and lanes
    private Bitmap[] vehicles;
    private Bitmap[] trucks;
    private Bitmap coins;  // Assurez-vous que la taille du tableau est définie avant de l'utiliser    private Map<String, int[]> spriteConfigurations = new HashMap<>();
    private long lastSpeedIncreaseTime = System.currentTimeMillis(); // Initialisation du temps pour l'augmentation de vitesse
    private boolean toggleVirationOnjump = false;
    private boolean hasVibratedForJump = false;


    public GameRunView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        laneWidth = screenWidth / 3;
        paint = new Paint();

        // Récupération des sprite avec plusieur images dedans
        Bitmap runSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.run);
        Bitmap jumpSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.jump);

        // Creation de l'objet joueur
        player = new Player(screenWidth, screenHeight, runSpriteSheet, jumpSpriteSheet);

        // Charger les images des voitures
        vehicles = new Bitmap[3];
        vehicles[0] = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car1), 100);
        vehicles[1] = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car2), 100);
        vehicles[2] = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car3), 100);

        // Charger les images des camions
        trucks = new Bitmap[3];
        trucks[0] = rotateBitmap(resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.truck1), 100), 180);
        trucks[1] = rotateBitmap(resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.truck2), 100), 180);
        trucks[2] = rotateBitmap(resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.truck3), 100), 180);

        generateObstacles();

        // Charger les images des pièces
        coins = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        generateCoins();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    protected static void vibrateOnJump() {
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public Bitmap[] extractSprites(Bitmap source, int spriteWidth, int spriteHeight) {
        // Calculez le nombre de sprites dans l'image
        int cols = source.getWidth() / spriteWidth; // Nombre de colonnes
        int rows = source.getHeight() / spriteHeight; // Nombre de lignes

        // Créez un tableau pour stocker les sprites extraits
        Bitmap[] sprites = new Bitmap[cols * rows];

        // Découpez chaque sprite
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * spriteWidth; // Position X du sprite
                int y = row * spriteHeight; // Position Y du sprite
                sprites[row * cols + col] = Bitmap.createBitmap(source, x, y, spriteWidth, spriteHeight);
            }
        }

        return sprites; // Retourne le tableau de sprites
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

    private Coin createCoin() {
        List<Integer> availableLanes = new ArrayList<>(Arrays.asList(0, 1, 2));
        int laneIndex = availableLanes.remove((int) (Math.random() * availableLanes.size()));
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;  // Calcul de la position X de la voie
        // Créer une nouvelle pièce
        return new Coin(laneX, -100, coins);
    }

    private void generateCoins() {

        int distanceBetweenCoins = 400;
        int currentY = -100; // Position de départ pour les obstacles
        int numberOfCoinsPerGroup = 10;  // Par exemple, génère entre 1 et 5 groupes de pièces
        List<Coin> newCoin = new ArrayList<>();

        int laneIndex = (int) (Math.random() * 3);
        for (int i = 0; i < numberOfCoinsPerGroup; i++) {
            Coin coin = createCoin();
            coin.setY(currentY);
            newCoin.add(coin);

            // Générer un espacement aléatoire pour chaque obstacle
            currentY -= distanceBetweenCoins;
        }
        // Ajouter tous les nouveaux obstacles générés à la pool
        coinPool.addAll(newCoin);

    }

    private Obstacle createObstacle() {
        List<Integer> availableLanes = new ArrayList<>(Arrays.asList(0, 1, 2));
        int laneIndex = availableLanes.remove((int) (Math.random() * availableLanes.size()));
        int laneX = (laneIndex * laneWidth) + (laneWidth / 2) - 50;

        // Randomly select either a car or a truck
        Bitmap[] obstacleImages = Math.random() < 0.5 ? vehicles : trucks;
        Bitmap randomImage = obstacleImages[(int) (Math.random() * obstacleImages.length)];

        return new Obstacle(laneX, -100, randomImage, vehicles);
    }

    private void generateObstacles() {
        int numObstacles = 6; // Nombre aléatoire d'obstacles
        int currentY = -100; // Position de départ pour les obstacles
        List<Obstacle> newObstacles = new ArrayList<>();

        for (int i = 0; i < numObstacles; i++) {
            Obstacle obstacle = createObstacle();
            obstacle.setY(currentY);
            newObstacles.add(obstacle);

            // Générer un espacement aléatoire pour chaque obstacle
            int spacing = 301 + (int) (Math.random() * 201);
            currentY -= spacing;
        }

        // Ajouter tous les nouveaux obstacles générés à la pool
        obstaclePool.addAll(newObstacles);
    }

    private boolean checkOverlapping(RectF firstItem, RectF secondItem) {
        if (RectF.intersects(firstItem, secondItem)) {
            return true;
        }
        return false;
    }

    private Obstacle getObstacle() {
        if (obstaclePool.isEmpty()) {
            // Create a new obstacle using the createObstacle() method
            return createObstacle();
        } else {
            // Reuse an obstacle from the pool
            return obstaclePool.remove(0);
        }
    }

    private void releaseObstacle(Obstacle obstacle) {
        obstaclePool.add(obstacle);
    }

    private void releaseCoin(Coin coin) {
        coinPool.add(coin);
    }


    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
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
        player.update();
        long currentTime = System.currentTimeMillis();
        RectF playerRect = new RectF(player.getRect()); // Rect pour le joueur

        // Vérifier si une seconde s'est écoulée depuis la dernière augmentation de vitesse
        if (currentTime - lastSpeedIncreaseTime >= 800 && obstacleSpeed < 100) { // 1000 ms = 1 seconde
            obstacleSpeed += score / 7; // Augmenter la vitesse des obstacles
            lastSpeedIncreaseTime = currentTime; // Mettre à jour le temps de la dernière augmentation
            Log.d("Debug", "speed" + obstacleSpeed);
        }

        // Mise à jour des obstacles
        for (int i = obstaclePool.size() - 1; i >= 0; i--) {
            Obstacle obstacle = obstaclePool.get(i);
            obstacle.update();

            if (obstacle.isOffScreen(screenHeight) && currentTime - lastObstacleTime > obstacleSpacing) {
                // Re-générer les obstacles une fois qu'ils sortent de l'écran
                obstaclePool.remove(i); // Enlever l'obstacle de la liste
                obstaclePool.add(createObstacle()); // Ajouter un nouvel obstacle
                lastObstacleTime = currentTime; // Mettre à jour le temps
                obstacleSpacing = Math.max(100, obstacleSpacing - 10); // Diminuer l'espacement entre obstacles
            }
            RectF obstacleRect = new RectF(obstacle.getRect()); // Create obstacle RectF
            if (RectF.intersects(playerRect, obstacleRect)) {
                if (obstacle.isJumpable() && player.isJumping()) {
                    // Ignore collision
                } else {
                    endGame();
                    return;
                }
            }
        }

        // Mise à jour des pièces
        for (int i = coinPool.size() - 1; i >= 0; i--) {
            Coin coin = coinPool.get(i);
            coin.update();

            if (coin.isOffScreen(screenHeight)) {
                coinPool.remove(i); // Retirer les pièces qui sortent de l'écran
                coinPool.add(createCoin());
            }

            RectF coinRect = new RectF(coin.getRect()); // Create coin RectF
            if (checkOverlapping(playerRect, coinRect)) {
                score++;
                int newLaneIndex = (int) (Math.random() * 3);
                int newLaneX = (newLaneIndex * laneWidth) + (laneWidth / 2) - 50;
                coin.reset(newLaneX);
            }
        }
    }


    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            // Dessiner le fond de l'autoroute
            paint.setColor(Color.DKGRAY);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            // Dessiner les lignes blanches discontinues
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

            // Dessiner les obstacles avec leurs bordures
            if (isPlaying) {

                for (Obstacle obstacle : obstaclePool) {
                    if (obstacle.getImage() != null && !obstacle.getImage().isRecycled()) {
                        canvas.drawBitmap(obstacle.getImage(), obstacle.getX(), obstacle.getY(), paint);
                        //drawBorder(canvas, new RectF(obstacle.getRect()), paint); // Dessiner la bordure de l'obstacle
                    }
                }
            } else {
                drawGameOver(canvas);
            }

            for (Coin coin : coinPool) {
                coin.draw(canvas, paint);
                //drawBorder(canvas, new RectF(coin.getRect()), paint); // Dessiner la bordure de la pièce
            }

            // Dessiner le joueur avec sa bordure
            player.draw(canvas, paint);
            //drawBorder(canvas, new RectF(player.getRect()), paint); // Dessiner la bordure du joueur

            // Afficher le score
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            // Charger une police personnalisée
            Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.press_start);
            paint.setTypeface(customFont);

            // Définir d'autres propriétés de la police (taille, style, etc.)
            paint.setTextSize(50); // Ajustez la taille du texte
            paint.setColor(Color.WHITE); // Ajustez la couleur du texte

            // Dessiner le texte avec la nouvelle police
            canvas.drawText("Score: " + score, screenWidth/2, 100, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameOver(Canvas canvas) {

        paint.setColor(Color.argb(150, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setColor(Color.RED);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over", screenWidth / 2, screenHeight / 3, paint);

        // Afficher le score
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, screenWidth / 2, screenHeight / 2 + 50, paint); // Affichage du score

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
        canvas.drawText("Rejouer", screenWidth / 2, buttonY + buttonHeight / 2 + 20, paint);
    }

    private void endGame() {
        isPlaying = false;
        coinPool.clear(); // Supprimer toutes les pièces
        obstaclePool.clear();
        obstacleSpeed = 30;
        draw();
    }

    private void sleep() {
        try {
            Thread.sleep(17); // Environ 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



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
                    hasVibratedForJump = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    // Vérifier si l'utilisateur effectue un mouvement vers le haut
                    float deltaY = startY - event.getY(); // Mouvement vertical
                    if (deltaY > 100) { // Si le mouvement vers le haut dépasse 100 pixels
                        player.jump(); // Effectuer le saut
                        startY = event.getY(); // Réinitialiser pour éviter un double saut

                        // Vérifier si la vibration est activée et si elle n'a pas encore été effectuée
                        if (vibrator != null && toggleVirationOnjump && !hasVibratedForJump) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            hasVibratedForJump = true; // Marquer la vibration comme effectuée
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    // Réinitialiser l'indicateur de vibration pour le prochain mouvement
                    hasVibratedForJump = false;

                    // Déterminer si le mouvement final est vers la gauche ou la droite
                    float endX = event.getX();
                    float distanceX = endX - startX;

                    if (Math.abs(distanceX) > 100) { // Ajuster la sensibilité
                        if (distanceX < 0) {
                            player.moveLeft();
                        } else if (distanceX > 0) {
                            player.moveRight();
                        }
                    }
                    break;
            }
        }
        return true;
    }




    private void restartGame() {
        score = 0;
        player.resetPosition(); // Reset player position
        generateObstacles(); // Generate new obstacles
        generateCoins(); // Generate new coins
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
