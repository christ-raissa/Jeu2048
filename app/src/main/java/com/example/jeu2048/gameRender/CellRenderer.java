package com.example.jeu2048.gameRender;

import android.graphics.Canvas;

public abstract class CellRenderer {
    public abstract void drawCell(Canvas canvas, int top, int left, int cellWidth, int cellHeight, long value);
}
