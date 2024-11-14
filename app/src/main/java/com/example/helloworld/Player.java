package com.example.helloworld;

import android.util.Log;

public class Player {

    private int caseNumber;  // Le numéro de la case où se trouve le joueur
    private int x;  // Coordonnée x du joueur
    private int y;  // Coordonnée y du joueur

    public Player(int startingCaseNumber) {
        this.caseNumber = startingCaseNumber;
        this.x = 0;  // Initialisation temporaire
        this.y = 0;  // Initialisation temporaire
    }

    // Getter pour le caseNumber
    public int getCaseNumber() {
        return caseNumber;
    }

    // Setter pour le caseNumber
    public void setCaseNumber(int caseNumber) {
        this.caseNumber = caseNumber;
        updatePosition(); // Met à jour la position du joueur en fonction du numéro de la case
    }

    // Met à jour la position du joueur en fonction de la case
    public void updatePosition() {
        // Recherche de la position (x, y) de la case correspondante au caseNumber
        for (int i = 0; i < BoardView.cases.size(); i++) {
            Case gameCase = BoardView.cases.get(i);
            if (gameCase.getCaseNumber() == caseNumber) {
                this.x = gameCase.getX();
                this.y = gameCase.getY();
                break;
            }
        }
    }

    public void moveToNextCase() {
        // Cherche la prochaine case grise (avec caseNumber suivant)
        boolean foundNextCase = false;
        for (int i = 0; i < BoardView.cases.size(); i++) {
            Case gameCase = BoardView.cases.get(i);
            if (gameCase.getCaseNumber() == caseNumber + 1 && gameCase.getValue() == 1) {  // Vérifiez si la case est libre (valeur 1)
                // Si on trouve la prochaine case, on met à jour la case du joueur
                setCaseNumber(gameCase.getCaseNumber());
                foundNextCase = true;
                break;
            }
        }
        if (!foundNextCase) {
            // Si la prochaine case n'est pas trouvée, on peut afficher un message ou un comportement alternatif
            Log.d("Debug", "No next case found");
        }
    }


    // Récupérer les coordonnées de la case où se trouve le joueur
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
