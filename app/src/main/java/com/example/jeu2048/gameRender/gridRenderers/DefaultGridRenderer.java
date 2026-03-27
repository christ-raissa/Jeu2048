package com.example.jeu2048.gameRender.gridRenderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.jeu2048.gameRender.GridRenderer;

public class DefaultGridRenderer extends GridRenderer {

    private Paint backgroundPaint;
    private Paint slotPaint;

    public DefaultGridRenderer() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(255, 200, 200, 200));

        slotPaint = new Paint();
        slotPaint.setColor(Color.argb(255, 80, 80, 80));
    }

    @Override
    public void drawGrid(Canvas canvas, float top, float left, int cellWidth, int cellHeight, int gridWidth, int gridHeight, float tilePaddingWidth, float tilePaddingHeight) {
        canvas.drawRoundRect(left, top, gridWidth * cellWidth, gridHeight * cellHeight, 5, 5, backgroundPaint);

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                top = y * cellHeight;
                left = x * cellWidth;

                float right = left + cellWidth;
                float bottom = top + cellHeight;

                top = top + tilePaddingHeight / 2.0f;
                bottom = bottom - tilePaddingHeight / 2.0f;
                left = left + tilePaddingHeight / 2.0f;
                right = right - tilePaddingHeight / 2.0f;

                canvas.drawRoundRect(left, top, right, bottom, 3, 3, slotPaint);
            }
        }

    }
}
