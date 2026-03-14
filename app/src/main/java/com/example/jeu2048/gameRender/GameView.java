package com.example.jeu2048.gameRender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import com.example.jeu2048.game.Game2048;
import com.example.jeu2048.game.GameMoveDirection;
import com.example.jeu2048.gameRender.cellRenderers.BaseCellRenderer;

public class GameView extends View {

    private static final int SWIPE_THRESHOLD = 100;  // min distance (px)
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;  // min speed (px/s)

    private Game2048 game;
    private CellRenderer cellRenderer;
    private GestureDetector gestureDetector;
    private final Rect gridRect = new Rect();

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Log.d("GAME2048", "GameView: Creating gameView!");

        initGame();
        initDraw();
        initSwipe();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!gridRect.contains((int) event.getX(), (int) event.getY())) {
            return false; // outside grid, ignore
        }
        gestureDetector.onTouchEvent(event);
        return true; // must return true so we keep receiving events
    }

    private void initGame() {
        game = new Game2048(8, 8, 16);
    }

    private void initDraw() {
        cellRenderer = new BaseCellRenderer();
    }

    private void onSwipeLeft()  {
        Log.d("GAME2048", "Swipe LEFT");
        game.makeMove(GameMoveDirection.LEFT);
        updateAfterMove();
    }

    private void onSwipeRight() {
        Log.d("GAME2048", "Swipe RIGHT");
        game.makeMove(GameMoveDirection.RIGHT);
        updateAfterMove();
    }
    private void onSwipeUp()    {
        Log.d("GAME2048", "Swipe UP");
        game.makeMove(GameMoveDirection.UP);
        updateAfterMove();
    }
    private void onSwipeDown()  {
        Log.d("GAME2048", "Swipe DOWN");
        game.makeMove(GameMoveDirection.DOWN);
        updateAfterMove();
    }

    private void updateAfterMove() {
        invalidate();
        if (game.isWon()) {
            game = new Game2048();
        }
    }

    private void initSwipe() {
        gestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2,
                                           float velocityX, float velocityY) {
                        if (e1 == null) return false;
                        float diffX = e2.getX() - e1.getX();
                        float diffY = e2.getY() - e1.getY();

                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) onSwipeRight(); else onSwipeLeft();
                                return true;
                            }
                        } else {
                            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffY > 0) onSwipeDown(); else onSwipeUp();
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateGridRect(w, h);
    }

    private void updateGridRect(int w, int h) {
        if (game == null) return;
        int cellSize = (Math.min(w, h)) / game.getWidth();
        int gridW = cellSize * game.getWidth();
        int gridH = cellSize * game.getHeight();
        gridRect.set(0, 0, gridW, gridH);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Log.d("GAME2048", "GameView: Trying to draw...");
        if (game == null) return;

        int cellSize;
        if (getWidth() < getHeight()) {
            cellSize = getWidth() / game.getWidth();
        } else {
            cellSize = getHeight() / game.getWidth();
        }
        int cellWidth  = cellSize;
        int cellHeight = cellSize;

        long[][] grid = game.getGrid();
        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                cellRenderer.drawCell(canvas, y * cellHeight, x * cellWidth, cellWidth, cellHeight, grid[y][x]);
            }
        }
    }
}