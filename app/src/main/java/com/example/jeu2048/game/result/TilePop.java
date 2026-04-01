package com.example.jeu2048.game.result;

import androidx.annotation.NonNull;

public class TilePop extends TileMod {
    int x;
    int y;

    public TilePop(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @NonNull
    @Override
    public String toString() {
        return "TilePop{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
