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
import com.example.jeu2048.game.result.MoveResult;
import com.example.jeu2048.gameRender.cellRenderers.DefaultCellRenderer;
import com.example.jeu2048.gameRender.gridRenderers.DefaultGridRenderer;

import java.util.ArrayList;

public class GameView extends View {

    private static final int SWIPE_THRESHOLD = 100;  // min distance (px)
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;  // min speed (px/s)

    private Game2048 game;

    float cellWidth = 0;
    float cellHeight = 0;

    private ArrayList<DrawableTile> cells;
    private MoveResult lastMoveResult;

    private CellRenderer cellRenderer;
    private GridRenderer gridRenderer;
    private GestureDetector gestureDetector;

    private final long animationTimeMillis = 1000;
    private long startTime;

    private final Rect gridRect = new Rect();

    private Boolean animating = false;

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
        MoveResult spawnResult = game.spawnValues(2);
        syncCells();
        startTilesAnimation(spawnResult);
    }

    private void initDraw() {
        cellRenderer = new DefaultCellRenderer();
        gridRenderer = new DefaultGridRenderer();
    }

    private void onSwipeLeft()  {
        Log.d("GAME2048", "Swipe LEFT");
        MoveResult result = game.makeMove(GameMoveDirection.LEFT);
        updateAfterMove(result);
    }

    private void onSwipeRight() {
        Log.d("GAME2048", "Swipe RIGHT");
        MoveResult result = game.makeMove(GameMoveDirection.RIGHT);
        updateAfterMove(result);
    }
    private void onSwipeUp()    {
        Log.d("GAME2048", "Swipe UP");
        MoveResult result = game.makeMove(GameMoveDirection.UP);
        updateAfterMove(result);
    }
    private void onSwipeDown()  {
        Log.d("GAME2048", "Swipe DOWN");
        MoveResult result = game.makeMove(GameMoveDirection.DOWN);
        updateAfterMove(result);
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
        cellWidth = cellSize;
        cellHeight = cellSize;
        int gridW = cellSize * game.getWidth();
        int gridH = cellSize * game.getHeight();
        gridRect.set(0, 0, gridW, gridH);
    }

    private void updateAfterMove(MoveResult results) {
        if (results.getMods().isEmpty()) {
            return;
        }
        startTilesAnimation(results);
    }

    private void updateAfterAnimation() {
        if (game.isWon()) {
            Log.d("GAME2048", "GameView: You won!!! New game starting");
            game = new Game2048();
        }
        syncCells();
        invalidate();
    }

    private void startTilesAnimation(@NonNull MoveResult result) {
        animating = true;
        Log.d("GAME2048", "Starting animation with results : " + result.toString());
        lastMoveResult = result;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    private void animateTiles() {
        long currentTime = System.currentTimeMillis();
        double progress = Math.max((currentTime - startTime) / animationTimeMillis, 0);

        if (progress > 1) {
            endTilesAnimation();
            return;
        }

        invalidate();
    }

    private void endTilesAnimation() {
        animating = false;
        updateAfterAnimation();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (animating) {
            Log.d("GAME2048", "GameView: Animating...");
            animateTiles();
        }
        drawGame(canvas);
    }

    private void drawGame(Canvas canvas) {
        gridRenderer.drawGrid(canvas, 0, 0, (int) cellWidth, (int) cellHeight, gridRect.width(), gridRect.height());
        for (DrawableTile cell : cells) {
            cellRenderer.drawCell(canvas, cell.getY(), cell.getX(), cell.getWidth(), cell.getHeight(), cell.getValue());
        }
    }

    private void syncCells() {
        if (game == null) return;

        cells = new ArrayList<>();

        long[][] grid = game.getGrid();
        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                cells.add(new DrawableTile(x * cellWidth, y * cellHeight, cellWidth, cellHeight, grid[y][x]));
            }
        }
    }
}