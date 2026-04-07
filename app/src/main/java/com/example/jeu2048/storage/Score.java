package com.example.jeu2048.storage;

public class Score {

    public String name;
    public long score;
    public  long moves;
    public String resultat;
    public long duree;

    public Score(String name, long score, long moves, String resultat, long duree){
        this.name = name;
        this.score = score;
        this.moves = moves;
        this.resultat = resultat;
        this.duree = duree;
    }
}
