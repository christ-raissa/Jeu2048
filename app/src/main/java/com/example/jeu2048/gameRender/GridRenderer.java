package com.example.jeu2048.gameRender;

import android.graphics.Canvas;

public abstract class GridRenderer {
    public abstract void drawGrid(Canvas canvas, float top, float left, int cellWidth, int cellHeight, int gridWidth, int gridHeight, float tilePaddingWidth, float tilePaddingHeight);
}
