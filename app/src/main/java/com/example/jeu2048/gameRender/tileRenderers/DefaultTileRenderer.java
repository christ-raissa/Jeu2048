package com.example.jeu2048.gameRender.tileRenderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.jeu2048.gameRender.TileRenderer;

public class DefaultTileRenderer extends TileRenderer {

    private Paint backgroundPaint;
    private Paint textPaint;

    public DefaultTileRenderer() {
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void changePaint(long value) {
        // Default text color
        textPaint.setColor(Color.parseColor("#776e65"));

        switch ((int) value) {
            case 2:
                backgroundPaint.setColor(Color.parseColor("#eee4da"));
                break;
            case 4:
                backgroundPaint.setColor(Color.parseColor("#ede0c8"));
                break;
            case 8:
                backgroundPaint.setColor(Color.parseColor("#f2b179"));
                textPaint.setColor(Color.WHITE);
                break;
            case 16:
                backgroundPaint.setColor(Color.parseColor("#f59563"));
                textPaint.setColor(Color.WHITE);
                break;
            case 32:
                backgroundPaint.setColor(Color.parseColor("#f67c5f"));
                textPaint.setColor(Color.WHITE);
                break;
            case 64:
                backgroundPaint.setColor(Color.parseColor("#f65e3b"));
                textPaint.setColor(Color.WHITE);
                break;
            case 128:
                backgroundPaint.setColor(Color.parseColor("#edcf72"));
                textPaint.setColor(Color.WHITE);
                break;
            case 256:
                backgroundPaint.setColor(Color.parseColor("#edcc61"));
                textPaint.setColor(Color.WHITE);
                break;
            case 512:
                backgroundPaint.setColor(Color.parseColor("#edc850"));
                textPaint.setColor(Color.WHITE);
                break;
            case 1024:
                backgroundPaint.setColor(Color.parseColor("#edc53f"));
                textPaint.setColor(Color.WHITE);
                break;
            case 2048:
                backgroundPaint.setColor(Color.parseColor("#edc22e"));
                textPaint.setColor(Color.WHITE);
                break;
            default:
                backgroundPaint.setColor(Color.parseColor("#3c3a32"));
                textPaint.setColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void drawCell(Canvas canvas, float top, float left, float cellWidth, float cellHeight, long value, float tilePaddingWidth, float tilePaddingHeight) {
        if (value < 1) {
            return;
        }

        changePaint(value);

        float right = left + cellWidth;
        float bottom = top + cellHeight;

        top += tilePaddingHeight / 2.0f;
        bottom -= tilePaddingHeight / 2.0f;
        left += tilePaddingWidth / 2.0f;
        right -= tilePaddingWidth / 2.0f;

        String text = String.valueOf(value);

        float textSize = cellWidth * 0.35f;
        if (value >= 1000) {
            textSize = cellWidth * 0.25f;
        }
        textPaint.setTextSize(textSize);

        float centerX = (left + right) / 2f;
        float centerY = (top + bottom) / 2f;

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = centerY - (fm.ascent + fm.descent) / 2f;

        canvas.drawRect(left, top, right, bottom, backgroundPaint);
        canvas.drawText(text, centerX, textY, textPaint);
    }
}