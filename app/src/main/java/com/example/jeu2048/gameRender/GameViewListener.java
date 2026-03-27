package com.example.jeu2048.gameRender;

public interface GameViewListener {
    public void OnScoreChange(long from, long to);
    public void OnStart();
    public void OnGameOver();
    public void OnGameWon();
}
