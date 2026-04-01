package com.example.jeu2048.game.result;

import androidx.annotation.NonNull;

public class TileMove extends TileMod {
    int fromX;
    int fromY;
    int toX;
    int toY;

    public TileMove(int fromX, int fromY, int toX, int toY) {
        this.fromY = fromY;
        this.fromX = fromX;
        this.toX = toX;
        this.toY = toY;
    }

    public int getFromX() {
        return fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public int getToX() {
        return toX;
    }

    public int getToY() {
        return toY;
    }

    @NonNull
    @Override
    public String toString() {
        return "TileMove{" +
                "fromX=" + fromX +
                ", fromY=" + fromY +
                ", toX=" + toX +
                ", toY=" + toY +
                '}';
    }
}
