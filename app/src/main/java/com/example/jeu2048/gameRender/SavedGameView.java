package com.example.jeu2048.gameRender;

import com.example.jeu2048.game.Game2048;
import com.example.jeu2048.gameRender.GameMode;

import java.io.Serializable;

public class SavedGameView implements Serializable {

    private Game2048 game;

    private GameMode mode;

    private long gameEndTime;

    private long elapsedTime;

    public SavedGameView() {}

    public static SavedGameView fromGameView(GameView view) {
        SavedGameView save = new SavedGameView();

        save.game = view.getGame();
        save.mode = view.getMode();

        save.gameEndTime = view.getGameEndTime();

        return save;
    }

    public void setElapsedTime(long time) {
        elapsedTime = time;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void applyTo(GameView view) {

        game.initRandom(); // VERY IMPORTANT after deserialization

        view.setGame(game);
        view.setMode(mode);

        view.setEndGameTime(gameEndTime);

        view.syncTiles();
        view.invalidate();
    }
}