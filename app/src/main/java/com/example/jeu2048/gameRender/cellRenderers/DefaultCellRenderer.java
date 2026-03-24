package com.example.jeu2048.gameRender.cellRenderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.jeu2048.gameRender.CellRenderer;

public class DefaultCellRenderer extends CellRenderer {

    private Paint backgroundPaint;
    private Paint textPaint;
    private Rect textBounds;

    public DefaultCellRenderer() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);

        textBounds = new Rect();
    }

    @Override
    public void drawCell(Canvas canvas, float top, float left, float cellWidth, float cellHeight, long value) {
        if (value < 1) {
            return;
        }

        float right = left + cellWidth;
        float bottom = top + cellHeight;

        String text = String.valueOf(value);

        textPaint.setTextSize(cellWidth * 0.35f);

        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        float x = left + cellWidth / 2f;
        float y = top + cellHeight / 2f + textBounds.height() / 2f;

        canvas.drawRect(left, top, right, bottom, backgroundPaint);
        canvas.drawText(text, x, y, textPaint);
    }
}
