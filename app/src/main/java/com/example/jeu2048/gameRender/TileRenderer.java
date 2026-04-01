package com.example.jeu2048.gameRender;

import android.graphics.Canvas;

public abstract class TileRenderer {
    public abstract void drawTile(Canvas canvas, float top, float left, float cellWidth, float cellHeight, long value, float tilePaddingWidth, float tilePaddingHeight);
}
