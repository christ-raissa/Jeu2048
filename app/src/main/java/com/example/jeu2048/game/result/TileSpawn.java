package com.example.jeu2048.game.result;

import androidx.annotation.NonNull;

public class TileSpawn extends TileMod {
    int x;
    int y;

    long value;

    public TileSpawn(int x, int y, long value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return "TileSpawn{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }
}
