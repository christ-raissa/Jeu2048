package com.example.jeu2048.gameRender;

import android.graphics.Canvas;

public abstract class GridRenderer {
    public abstract void drawGrid(Canvas canvas, int top, int left, int cellWidth, int cellHeight, int gridWidth, int gridHeight);
}
