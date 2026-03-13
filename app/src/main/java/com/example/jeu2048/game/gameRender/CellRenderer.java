package com.example.jeu2048.game.gameRender;

import android.graphics.Canvas;

public abstract class CellRenderer {

    public CellRenderer(int top, int left, int cellWidth, int cellHeight, long value) {

    }

    public abstract void drawCell(Canvas canvas);
}
