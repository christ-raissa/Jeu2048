package com.example.jeu2048.game.result;

import androidx.annotation.NonNull;

public class TileUpgrade extends TileMod {
    int x;
    int y;

    long from;
    long to;

    public TileUpgrade(int x, int y, long from, long to) {
        this.x = x;
        this.y = y;
        this.from = from;
        this.to = to;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    @NonNull
    @Override
    public String toString() {
        return "TileUpgrade{" +
                "x=" + x +
                ", y=" + y +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
