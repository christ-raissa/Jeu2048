package com.example.jeu2048.gameRender.tileRenderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.jeu2048.gameRender.TileRenderer;

public class DefaultTileRenderer extends TileRenderer {

    private Paint backgroundPaint;
    private Paint textPaint;
    private Rect textBounds;

    public DefaultTileRenderer() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);

        textBounds = new Rect();
    }

    @Override
    public void drawCell(Canvas canvas, float top, float left, float cellWidth, float cellHeight, long value, float tilePaddingWidth, float tilePaddingHeight) {
        if (value < 1) {
            return;
        }

        float right = left + cellWidth;
        float bottom = top + cellHeight;

        top = top + tilePaddingHeight / 2.0f;
        bottom = bottom - tilePaddingHeight / 2.0f;
        left = left + tilePaddingHeight / 2.0f;
        right = right - tilePaddingHeight / 2.0f;

        String text = String.valueOf(value);

        textPaint.setTextSize(cellWidth * 0.35f);

        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        float x = left + cellWidth / 2f;
        float y = top + cellHeight / 2f + textBounds.height() / 2f;

        canvas.drawRect(left, top, right, bottom, backgroundPaint);
        canvas.drawText(text, x, y, textPaint);
    }
}
