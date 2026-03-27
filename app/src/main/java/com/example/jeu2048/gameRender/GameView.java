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
import com.example.jeu2048.gameRender.tileRenderers.DefaultTileRenderer;
import com.example.jeu2048.gameRender.gridRenderers.DefaultGridRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class GameView extends View {
    private enum AnimationState {
        FIRST_MOVE,
        MIDDLE,
        LAST_MOVE,
        END
    }


    private final static long firstMoveTimeMillis = 100L;
    private final static long middleTimeMillis = 100L;
    private final static long lastMoveTimeMillis = 100L;
    private final static long endTimeMillis = 500L;

    private final static long firstMoveThreshold = firstMoveTimeMillis;
    private final static long middleThreshold = firstMoveThreshold + middleTimeMillis;
    private final static long lastMoveThreshold = middleThreshold + lastMoveTimeMillis;
    private final static long endThreshold = lastMoveThreshold + endTimeMillis;

    private static final int SWIPE_THRESHOLD = 100;  // min distance (px)
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;  // min speed (px/s)

    private Game2048 game;

    float cellWidth = 0;
    float cellHeight = 0;

    private AnimationState lastAnimationState;

    private ArrayList<DrawableTile> drawableTiles = new ArrayList<>();
    private MoveResult lastMoveResult;

    private TileRenderer cellRenderer;
    private GridRenderer gridRenderer;

    private float tilePaddingWidth = 20;
    private float tilePaddingHeight = 20;

    private GestureDetector gestureDetector;

    private long startTime;
    private boolean firstAnimate;

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
        game = new Game2048(4, 4, 16);
        MoveResult spawnResult = game.spawnValues(2);
        Log.d("GAME2048", "initGame: \n" + game.toString());
        startTilesAnimation(spawnResult);
    }

    private void initDraw() {
        cellRenderer = new DefaultTileRenderer();
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
        Log.d("GAME2048", "updateAfterMove: \n" + game.toString());
        startTilesAnimation(results);
    }

    private void updateAfterAnimation() {
        if (game.isWon()) {
            Log.d("GAME2048", "GameView: You won!!! New game starting");
            game = new Game2048();
        }
        syncTiles();
        invalidate();
    }

    private void startTilesAnimation(@NonNull MoveResult result) {
        animating = true;
        firstAnimate = true;
        Log.d("GAME2048", "Starting animation with results : " + result.toString());
        lastMoveResult = result;
        invalidate();
    }

    private void animateTiles() {
        if (firstAnimate) {
            lastAnimationState = AnimationState.FIRST_MOVE;
            startTime = System.currentTimeMillis();
            firstAnimate = false;
        }

        long currentTime = System.currentTimeMillis();
        long animateTime = currentTime - startTime;
        AnimationState animState = getAnimationState(animateTime);

        if (animState != lastAnimationState) {
            switch (lastAnimationState) {
                case FIRST_MOVE:
                    drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getFirstMoves(), 1, cellWidth, cellHeight);
                    break;
                case MIDDLE:
                    drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getUpgrades(), 1, cellWidth, cellHeight);
                    drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getPops(), 1, cellWidth, cellHeight);
                    break;
                case LAST_MOVE:
                    drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getLastMoves(), 1, cellWidth, cellHeight);
                    break;
                case END:
                    drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getSpawns(), 1, cellWidth, cellHeight);
                    break;
            }
            rebaseDrawableTiles();
        }
        lastAnimationState = animState;

        Log.d("GAME2048", "animateTiles: time is " + animateTime + " for " + drawableTiles.toString());

        if (animState == null) {
            endTilesAnimation();
            return;
        }

        double progress;

        switch (animState) {
            case FIRST_MOVE:
                progress = Math.max((animateTime) / (double) firstMoveTimeMillis , 0);
                drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getFirstMoves(), progress, cellWidth, cellHeight);
                break;
            case MIDDLE:
                progress = Math.max((animateTime - firstMoveThreshold) / (double) middleTimeMillis , 0);
                drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getUpgrades(), progress, cellWidth, cellHeight);
                drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getPops(), progress, cellWidth, cellHeight);
                break;
            case LAST_MOVE:
                progress = Math.max((animateTime - middleThreshold) / (double) lastMoveTimeMillis , 0);
                drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getLastMoves(), progress, cellWidth, cellHeight);
                break;
            case END:
                progress = Math.max((animateTime - lastMoveThreshold) / (double) endTimeMillis , 0);
                drawableTiles = TileAnimator.animateTiles(drawableTiles, lastMoveResult.getSpawns(), progress, cellWidth, cellHeight);
                break;
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
            animateTiles();
        }
        drawGame(canvas);
    }

    private void drawGame(Canvas canvas) {
        gridRenderer.drawGrid(canvas, gridRect.top, gridRect.left, (int) cellWidth, (int) cellHeight, gridRect.width(), gridRect.height(), tilePaddingWidth, tilePaddingHeight);
        for (DrawableTile cell : drawableTiles) {
            cellRenderer.drawCell(canvas, cell.getAnimateX(), cell.getAnimateY(), cell.getWidth(), cell.getHeight(), cell.getValue(), tilePaddingWidth, tilePaddingHeight);
        }
    }

    private void syncTiles() {
        if (game == null) return;

        drawableTiles = new ArrayList<>();

        long[][] grid = game.getGrid();
        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                if (grid[x][y] > 0) {
                    drawableTiles.add(new DrawableTile(x * cellWidth, y * cellHeight, cellWidth, cellHeight, grid[x][y]));
                }
            }
        }
    }

    private void rebaseDrawableTiles() {
        for (DrawableTile tile : drawableTiles) {
            tile.setX(tile.getAnimateX());
            tile.setY(tile.getAnimateY());
        }
    }

    private AnimationState getAnimationState(long animationTime) {

        if (animationTime < firstMoveThreshold) {
            return AnimationState.FIRST_MOVE;
        } else if (animationTime < middleThreshold) {
            return AnimationState.MIDDLE;
        } else if (animationTime < lastMoveThreshold) {
            return AnimationState.LAST_MOVE;
        } else if (animationTime < endThreshold) {
            return AnimationState.END;
        } else {
            return null;
        }
    }
}