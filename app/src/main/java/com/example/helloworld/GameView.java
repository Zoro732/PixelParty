package com.example.helloworld;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private boolean isPlaying;
    private Paint obstacle_color; // Objet Paint pour dessiner
    private Paint end_color;
    private Bitmap spriteImage;  // Image du sprite
    //private String selection = "sprite_name";  // Nom du sprite (sans l'extension .png)
    private float spriteX, spriteY;  // Position initiale du sprite
    private float speedX = 0; // Vitesse sur l'axe X
    private float speedY = 0; // Vitesse sur l'axe Y
    private float friction = 0.98f; // Coefficient de friction pour ralentir le sprite
    private float accelerationFactor = 2.0f; // Facteur d'accélération pour le gyroscope

    // Attributs pour la largeur et la hauteur de l'écran
    private int screenWidth, screenHeight;
    private static final int GRID_ROWS = 10; // Nombre de lignes
    private static final int GRID_COLS = 15; // Nombre de colonnes
    private float goalX; // Position X du point d'arrivée
    private float goalY; // Position Y du point d'arrivée
    private float goalRadius = 30; // Rayon du point d'arrivée
    private int[][] map = {
            {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1},
            {1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1},
            {1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };
    private int tileSize_W, tileSize_H;
    private Vibrator vibrator;
    private boolean goalReached = false; // Drapeau pour suivre l'état de la collision avec le point d'arrivée
    private CountDownTimer countDownTimer;
    private int DefaultUserPosition[] = {10, 10};
    private int defaultTimerValue = 40; // Valeur par défaut du compteur
    private int timerValue = defaultTimerValue;
    private String spriteSelection;
    private Paint timerPaint = new Paint(); // Objet Paint pour le compteur

    private Bitmap tileImagePath;
    private Bitmap tileImageWall;
    // Constructeur de la vue du jeu
    public GameView(Context context, String selection) {
        super(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.spriteSelection = selection;
         // Récupération de la variable
        // Obtenir les dimensions de l'écran
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels; // Largeur de l'écran
        screenHeight = displayMetrics.heightPixels; // Hauteur de l'écran

        // Position initiale du sprite
        spriteX = DefaultUserPosition[0];
        spriteY = DefaultUserPosition[1];
        tileSize_W = screenWidth / GRID_COLS;
        tileSize_H = screenHeight / GRID_ROWS;

        tileImagePath = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.blank_tile), tileSize_W);
        tileImageWall = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.simple_wall), tileSize_W);

        // Charger l'image du sprite à partir du nom "selection" (sans l'extension)
        loadSpriteImage(context, spriteSelection);

        // Initialiser les objets Paint
        obstacle_color = new Paint();
        end_color = new Paint();

        end_color.setColor(Color.GREEN); // Définir la couleur du point d'arrivée
        obstacle_color.setColor(Color.BLUE); // Définir la couleur des obstacles

        startTimer(); // Démarrer le compteur
    }

    // Méthode pour charger l'image du sprite en fonction du nom
    private void loadSpriteImage(Context context, String selecSprite) {
        // Récupérer le ressource ID de l'image à partir de son nom
        int resId = context.getResources().getIdentifier(selecSprite, "drawable", context.getPackageName());

        if (resId != 0) {
            // Si l'image existe, charger l'image
            spriteImage = BitmapFactory.decodeResource(context.getResources(), resId);
        } else {
            // Si l'image n'existe pas, afficher un message et utiliser une image par défaut
            Toast.makeText(context, "Sprite non trouvé : " + selecSprite, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            draw();
            sleep();
        }
    }

    private Bitmap resizeBitmap(Bitmap originalBitmap, float newWidth) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        float aspectRatio = (float) originalHeight / (float) originalWidth;
        int newHeight = Math.round(newWidth * aspectRatio); // Calculer la nouvelle hauteur pour garder le ratio
        return Bitmap.createScaledBitmap(originalBitmap, (int) tileSize_W, (int) tileSize_H, false);
    }

    private Bitmap rotateBitmap(Bitmap originalBitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f); // Pivoter autour du centre
        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.BLACK); // Fond noir
            // Dessiner les obstacles
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[y].length; x++) {
                    int tileValue = map[y][x]; // Récupérer la valeur de la tuile

                    // Gestion des différents cas pour chaque valeur de la tuile
                    switch (tileValue) {
                        case 0:
                            // Dessiner l'image PNG redimensionnée pour les tuiles '0'
                            canvas.drawBitmap(tileImagePath, x * tileSize_W, y * tileSize_H, null);
                            break;

                        case 1:
                            // Dessiner un obstacle
                            canvas.drawBitmap(tileImageWall, x * tileSize_W, y * tileSize_H, null);
                            break;

                        default:
                            // Si la valeur n'est pas définie, on peut choisir de dessiner un carré par défaut
                            break;
                    }
                }
            }

            // Dessiner le sprite à la place de la boule
            if (spriteImage != null) {
                // Dessiner le sprite
                canvas.drawBitmap(spriteImage, spriteX - spriteImage.getWidth() / 2, spriteY - spriteImage.getHeight() / 2, null);
            }

            // Dessiner le point d'arrivée
            canvas.drawCircle(goalX, goalY, goalRadius, end_color); // Point d'arrivée en vert

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(16); // 60 FPS
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

    public void moveSprite(float x, float y) {
        // Mise à jour de la vitesse en fonction du gyroscope
        speedX += x * accelerationFactor;
        speedY -= y * accelerationFactor;

        // Appliquer la friction
        speedX *= friction;
        speedY *= friction;

        // Calculer la position potentielle du sprite
        float newSpriteX = spriteX + speedX;
        float newSpriteY = spriteY + speedY;

        // Vérifier les collisions avec les bords de la vue
        // Limites gauche et droite
        if (newSpriteX < spriteImage.getWidth() / 2) {
            newSpriteX = spriteImage.getWidth() / 2; // Rester à l'intérieur de la limite gauche
            speedX = 0; // Réinitialiser la vitesse en X
        } else if (newSpriteX > getWidth() - spriteImage.getWidth() / 2) {
            newSpriteX = getWidth() - spriteImage.getWidth() / 2; // Rester à l'intérieur de la limite droite
            speedX = 0; // Réinitialiser la vitesse en X
        }

        // Limites haut et bas
        if (newSpriteY < spriteImage.getHeight() / 2) {
            newSpriteY = spriteImage.getHeight() / 2; // Rester à l'intérieur de la limite supérieure
            speedY = 0; // Réinitialiser la vitesse en Y
        } else if (newSpriteY > getHeight() - spriteImage.getHeight() / 2) {
            newSpriteY = getHeight() - spriteImage.getHeight() / 2; // Rester à l'intérieur de la limite inférieure
            speedY = 0; // Réinitialiser la vitesse en Y
        }

        // Mise à jour de la position du sprite
        spriteX = newSpriteX;
        spriteY = newSpriteY;

        invalidate(); // Redessiner la vue
    }

    private void checkCollisionWithGoal() {
        // Calculer la distance entre le sprite et le point d'arrivée
        float dx = spriteX - goalX;
        float dy = spriteY - goalY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Vérifier si la distance est inférieure ou égale à la somme des rayons
        if (distance <= spriteImage.getWidth() / 2 + goalRadius && !goalReached) {
            // Si le sprite touche le point d'arrivée, signaler que l'objectif est atteint
            goalReached = true;
            Toast.makeText(getContext(), "Objectif atteint !", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timerValue * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerValue = (int) millisUntilFinished / 1000; // Mise à jour du temps restant
            }

            @Override
            public void onFinish() {
                Toast.makeText(getContext(), "Temps écoulé !", Toast.LENGTH_SHORT).show();
            }
        };
        countDownTimer.start();
    }

    public void setSelection(String selection) {
        this.spriteSelection = selection;
        loadSpriteImage(getContext(), selection);  // Recharger l'image avec le nouveau nom de sprite
    }
}
