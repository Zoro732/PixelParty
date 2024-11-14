package com.example.helloworld;

public class Case {

    private int x;
    private int y;
    private int value;  // 0 pour une case vide, 1 pour une case grise (obstacle)
    private int caseNumber;  // Numéro de la case (uniquement pour les cases grises)

    public Case(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    // Getter et Setter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(int caseNumber) {
        this.caseNumber = caseNumber;
    }
}
