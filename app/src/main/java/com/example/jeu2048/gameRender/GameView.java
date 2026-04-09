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
import com.example.jeu2048.settings.SettingsHelper;
import com.example.jeu2048.theme.Theme;

import java.util.ArrayList;

public class GameView extends View {
    private enum AnimationState {
        FIRST_MOVE,
        MIDDLE,
        LAST_MOVE,
        END
    }


    private long firstMoveTimeMillis;
    private long middleTimeMillis;
    private long lastMoveTimeMillis;
    private long endTimeMillis;

    private long firstMoveThreshold;
    private long middleThreshold;
    private long lastMoveThreshold;
    private long endThreshold;

    private static final int SWIPE_THRESHOLD = 100;  // min distance (px)
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;  // min speed (px/s)

    private Game2048 game;

    float cellWidth = 0;
    float cellHeight = 0;

    private AnimationState lastAnimationState;

    private ArrayList<DrawableTile> drawableTiles = new ArrayList<>();
    private MoveResult lastMoveResult;

    private long oldScore;
    private GameMode mode;
    private long gameStartTime;
    private long gameEndTime;

    SettingsHelper settingsHelper;
    private Theme theme;

    private final static float tilePaddingWidth = 20;
    private final static float tilePaddingHeight = 20;

    private GestureDetector gestureDetector;

    private long animStartTime;
    private boolean firstAnimate;

    private final Rect gridRect = new Rect();

    private Boolean animating = false;

    private boolean paused = false;

    private int gameWidth = 4;
    private int gameHeight = 4;

    private boolean isSolo = true;

    private final ArrayList<GameViewListener> subs = new ArrayList<>();

    private void emitScoreChange(long from, long to) {
        for (GameViewListener sub : subs) {
            sub.OnScoreChange(from, to);
        }
    }

    private void emitGameOver() {
        for (GameViewListener sub : subs) {
            sub.OnGameOver(gameEndTime);
        }
    }

    private void emitGameWon() {
        for (GameViewListener sub : subs) {
            sub.OnGameWon(gameEndTime);
        }
    }

    private void emitStart() {
        for (GameViewListener sub : subs) {
            sub.OnStart();
        }
    }

    public void sub(GameViewListener newSub) {
        subs.add(newSub);
    }



    public GameView(Context context) {
        super(context);
        init(true);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(true);
    }

    public void init(boolean solo) {
        // Log.d("GAME2048", "GameView: Creating gameView!");
        settingsHelper = new SettingsHelper(this.getContext());

        animating = false;

        isSolo = solo;

        initGame();
        initDraw();
        initSwipe();
    }

    private void updateGridRect(int w, int h) {
        if (game == null) return;

        int cellSize = Math.min(w / game.getWidth(), h / game.getWidth());
        cellWidth = cellSize;
        cellHeight = cellSize;

        int gridW = cellSize * game.getWidth();
        int gridH = cellSize * game.getHeight();
        gridRect.set(0, 0, gridW, gridH);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!gridRect.contains((int) event.getX(), (int) event.getY())) {
            return false; // outside grid, ignore
        }
        gestureDetector.onTouchEvent(event);
        return true; // must return true so we keep receiving events
    }

    public void initGame() {
        gameWidth = isSolo ? settingsHelper.getSingleCols() : settingsHelper.getMultiCols();
        gameHeight = isSolo ? settingsHelper.getSingleRows() : settingsHelper.getMultiRows();
        mode = isSolo ? settingsHelper.getSingleMode() : settingsHelper.getMultiMode();

        switch (mode) {
            case ScoreObjective:
                game = new Game2048(gameWidth, gameHeight, isSolo ? settingsHelper.getSingleTargetScore() : settingsHelper.getMultiTargetScore());
                break;
            case TimeLimit:
                game = new Game2048(gameWidth, gameHeight, 1_000_000_000); // Should never win by points
                break;
        }

        gameStartTime = System.currentTimeMillis();
        gameEndTime = 0;

        updateGridRect(getWidth(), getHeight());

        MoveResult spawnResult = game.spawnValues(2);
        startTilesAnimation(spawnResult);
        emitStart();
    }

    private void initDraw() {
        theme = settingsHelper.getTheme(this.getContext());

        if (settingsHelper.areAnimationsEnabled()) {
            Log.d("GAME2048", "initDraw: animation ok : " + settingsHelper.getAnimationSpeed());
            firstMoveTimeMillis = (long) (settingsHelper.getAnimationSpeed() * 1000);  // deplacement
            middleTimeMillis = (long) (settingsHelper.getAnimationSpeed() * 1000);
            lastMoveTimeMillis = (long) (settingsHelper.getAnimationSpeed() * 1000);
            endTimeMillis = (long) (settingsHelper.getAnimationSpeed()) * 1000;     // apparition

        } else {
            firstMoveTimeMillis = 0;
            middleTimeMillis = 0;
            lastMoveTimeMillis = 0;
            endTimeMillis = 0;
        }

        firstMoveThreshold = firstMoveTimeMillis;
        middleThreshold = firstMoveThreshold + middleTimeMillis;
        lastMoveThreshold = middleThreshold + lastMoveTimeMillis;
        endThreshold = lastMoveThreshold + endTimeMillis;
    }
    private void onSwipeLeft()  {
        if (game.isGameOver()) return;
        Log.d("GAME2048", "Swipe LEFT");
        oldScore = game.getScore();
        MoveResult result = game.makeMove(GameMoveDirection.LEFT);
        updateAfterMove(result);
    }

    private void onSwipeRight() {
        Log.d("GAME2048", "Swipe RIGHT");
        oldScore = game.getScore();
        MoveResult result = game.makeMove(GameMoveDirection.RIGHT);
        updateAfterMove(result);
    }
    private void onSwipeUp()    {
        Log.d("GAME2048", "Swipe UP");
        oldScore = game.getScore();
        MoveResult result = game.makeMove(GameMoveDirection.UP);
        updateAfterMove(result);
    }
    private void onSwipeDown()  {
        Log.d("GAME2048", "Swipe DOWN");
        oldScore = game.getScore();
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

                        if (paused) return false;
                        if (animating) return false;

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

    private void updateAfterMove(MoveResult results) {
        // Log.d("GAME2048", "updateAfterMove: \n" + game.toString());
        startTilesAnimation(results);
    }

    private void updateGameEndTime() {
        if (gameStartTime > 0) {
            gameEndTime += System.currentTimeMillis() - gameStartTime;
            gameStartTime = System.currentTimeMillis();
        }
    }

    private void updateAfterAnimation() {
        boolean isFinished = false;

        // 1. Vérification Victoire/Défaite
        if ((mode == GameMode.ScoreObjective && game.isWon()) ||
                (mode == GameMode.TimeLimit &&
                        System.currentTimeMillis() - gameStartTime > (isSolo ? settingsHelper.getSingleTimeLimit() : settingsHelper.getMultiTimeLimit()) * 1000L)) {
            updateGameEndTime();
            emitGameWon();
            isFinished = true;
        } else if (game.isGameOver()) {
            updateGameEndTime();
            emitGameOver();
            isFinished = true;
        }

        // 3. Mise à jour des scores
        emitScoreChange(oldScore, game.getScore());
        syncTiles();

        if (!isFinished) {
            invalidate();
        }
    }

    public long getMaxTile() {
        if (game == null) return 0;

        long max = 0;
        long[][] grid = game.getGrid();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > max) {
                    max = grid[i][j];
                }
            }
        }
        return max;
    }

    private void startTilesAnimation(@NonNull MoveResult result) {
        animating = true;
        firstAnimate = true;
        // Log.d("GAME2048", "Starting animation with results : " + result.toString());
        lastMoveResult = result;
        invalidate();
    }

    private void animateTiles() {
        if (firstAnimate) {
            lastAnimationState = AnimationState.FIRST_MOVE;
            animStartTime = System.currentTimeMillis();
            firstAnimate = false;
        }

        long currentTime = System.currentTimeMillis();
        long animateTime = currentTime - animStartTime;
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

        // Log.d("GAME2048", "animateTiles: time is " + animateTime + " for " + drawableTiles.toString());

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
        theme.getGridRenderer().drawGrid(canvas, gridRect.top, gridRect.left, (int) cellWidth, (int) cellHeight, game.getWidth(), game.getHeight(), tilePaddingWidth, tilePaddingHeight);
        for (DrawableTile tile : drawableTiles) {
            theme.getTileRenderer().drawTile(canvas, tile.getAnimateX(), tile.getAnimateY(), tile.getWidth(), tile.getHeight(), tile.getValue(), tilePaddingWidth, tilePaddingHeight);
        }
    }

    void syncTiles() {
        if (game == null) return;

        drawableTiles = new ArrayList<>();

        long[][] grid = game.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > 0) {
                    drawableTiles.add(new DrawableTile(i * cellWidth, j * cellHeight, cellWidth, cellHeight, grid[i][j]));
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

    public long getScore() {
        return game.getScore();
    }

    public int getNumMoves() {
        return game.getNumMoves();
    }

    public void setPaused(boolean paused) {
        this.paused = paused;

        if (paused) {
            updateGameEndTime();
            gameStartTime = -1;
        } else {
            gameStartTime = System.currentTimeMillis();
        }
    }

    public void setEndGameTime(long time) {
        gameEndTime = time;
    }

    public Game2048 getGame() {
        return game;
    }

    public GameMode getMode() {
        return mode;
    }

    public long getGameEndTime() {
        updateGameEndTime();
        return gameEndTime;
    }

    public void setGame(Game2048 game) {
        this.game = game;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public void setSolo(boolean isSolo) {
        this.isSolo = isSolo;
    }
}