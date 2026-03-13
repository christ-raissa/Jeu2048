package com.example.jeu2048.game.gameRender.cellRenderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.jeu2048.game.gameRender.CellRenderer;

public class BaseCellRenderer extends CellRenderer {

    private Paint backgroundPaint;
    private Paint textPaint;
    private Rect textBounds;

    public BaseCellRenderer() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);

        textBounds = new Rect();
    }

    @Override
    public void drawCell(Canvas canvas, int top, int left, int cellWidth, int cellHeight, long value) {
        if (value < 1) {
            return;
        }

        int right = left + cellWidth;
        int bottom = top + cellHeight;

        String text = String.valueOf(value);

        textPaint.setTextSize(cellWidth * 0.35f);

        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        float x = left + cellWidth / 2f;
        float y = top + cellHeight / 2f + textBounds.height() / 2f;

        canvas.drawRect(left, top, right, bottom, backgroundPaint);
        canvas.drawText(text, x, y, textPaint);
    }
}
