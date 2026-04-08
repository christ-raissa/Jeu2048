package com.example.jeu2048.game;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jeu2048.game.result.MoveResult;
import com.example.jeu2048.game.result.TileMod;
import com.example.jeu2048.game.result.TileMove;
import com.example.jeu2048.game.result.TilePop;
import com.example.jeu2048.game.result.TileSpawn;
import com.example.jeu2048.game.result.TileUpgrade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game2048 implements Serializable {
    private static class LineAndResult {
        public long[] line;
        public MoveResult result = new MoveResult();
    }

    private static final int BASE_GRID_WIDTH = 4;
    private static final int BASE_GRID_HEIGHT = 4;

    private static final long BASE_WIN_VALUE = 2048;

    private static final long EMPTY = 0;

    private final int width;
    private final int height;
    private long[][] grid;
    private final long winValue;

    private long score;
    private int numMoves = 0;

    private transient Random random;
    private long randomSeed;

    public Game2048(int width, int height, long winValue) {
        this.width = width;
        this.height = height;
        this.winValue = winValue;

        this.score = 0;

        this.randomSeed = System.currentTimeMillis();
        this.random = new Random(randomSeed);

        initializeGrid();
    }

    public Game2048() {
        this(BASE_GRID_WIDTH, BASE_GRID_HEIGHT, BASE_WIN_VALUE);
    }

    private void initializeGrid() {
        grid = new long[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY;
            }
        }
    }

    public TileSpawn spawnValue() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == EMPTY) {
                    emptyCells.add(new int[]{y, x});
                }
            }
        }

        if (emptyCells.isEmpty()) return null;

        initRandom();

        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        long value = (random.nextInt(10) == 0) ? 4 : 2;
        grid[cell[0]][cell[1]] = value;
        return new TileSpawn(cell[0], cell[1], value);
    }

    public MoveResult spawnValues(int amount) {
        MoveResult result = new MoveResult();
        for (int i = 0; i < amount; i++) {
            TileSpawn spawn = spawnValue();
            if (spawn != null) {
                result.addSpawn(spawn);
            }
        }
        return result;
    }

    public MoveResult makeMove(GameMoveDirection direction) {
        MoveResult moveResult = new MoveResult();
        switch (direction) {
            case UP:
                moveResult = moveUp();
                break;
            case DOWN:
                moveResult = moveDown();
                break;
            case RIGHT:
                moveResult = moveRight();
                break;
            case LEFT:
                moveResult = moveLeft();
                break;
        }

        TileSpawn spawn = spawnValue();
        if (spawn != null) {
            moveResult.addSpawn(spawn);
        }

        numMoves++;

        return moveResult;
    }

    private MoveResult moveLeft() {
        MoveResult result = new MoveResult();

        for (int y = 0; y < height; y++) {
            long[] line = new long[width];
            for (int x = 0; x < width; x++) {
                line[x] = grid[y][x];
            }
            LineAndResult lar = updateLine(line, 0, y, width - 1, y);
            for (int x = 0; x < width; x++) {
                grid[y][x] = lar.line[x];
            }

            result.concat(lar.result);
        }
        return result;
    }

    private MoveResult moveRight() {
        MoveResult result = new MoveResult();

        for (int y = 0; y < height; y++) {
            long[] line = new long[width];
            for (int x = 0; x < width; x++) {
                line[width - 1 - x] = grid[y][x];
            }
            LineAndResult lar = updateLine(line, width - 1, y, 0, y);
            for (int x = 0; x < width; x++) {
                grid[y][x] = lar.line[width - 1 - x];
            }

            result.concat(lar.result);
        }
        return result;
    }

    private MoveResult moveUp() {
        MoveResult result = new MoveResult();

        for (int x = 0; x < width; x++) {
            long[] line = new long[height];
            for (int y = 0; y < height; y++) {
                line[y] = grid[y][x];
            }
            LineAndResult lar = updateLine(line, x, 0, x, height - 1);
            for (int y = 0; y < height; y++) {
                grid[y][x] = lar.line[y];
            }

            result.concat(lar.result);
        }
        return result;
    }

    private MoveResult moveDown() {
        MoveResult result = new MoveResult();

        for (int x = 0; x < width; x++) {
            long[] line = new long[height];
            for (int y = 0; y < height; y++) {
                line[height - 1 - y] = grid[y][x];
            }
            LineAndResult lar = updateLine(line, x, height - 1, x, 0);
            for (int y = 0; y < height; y++) {
                grid[y][x] = lar.line[height - 1 - y];
            }

            result.concat(lar.result);
        }

        return result;
    }

    private int getDir(int start, int end) {
        int res = end - start;

        if (res == 0) {
            return 0;
        } else if (res < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    private LineAndResult compressLine(long[] line, int startx, int starty, int endx, int endy, boolean first) {
        LineAndResult lar = new LineAndResult();

        int dirX = getDir(startx, endx);
        int dirY = getDir(starty, endy);

        long[] compressed = new long[line.length];
        int pos = 0;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != EMPTY) {

                // MOD MOVE
                TileMove tileMove = new TileMove(startx + dirX * i, starty + dirY * i,
                        startx + dirX * pos, starty + dirY * pos
                );
                if ((tileMove.getFromX() != tileMove.getToX()) || (tileMove.getFromY() != tileMove.getToY())) {
                    if (first) {
                        lar.result.addFirstPartMove(tileMove);
                    } else {
                        lar.result.addLastPartMove(tileMove);
                    }
                }

                compressed[pos++] = line[i];
            }
        }

        // Log.d("GAME2048", "compressLine: " + Arrays.toString(line) + " to " + Arrays.toString(compressed));

        lar.line = compressed;

        return lar;
    }

    private LineAndResult fuseLine(long[] line, int startx, int starty, int endx, int endy) {
        LineAndResult lar = new LineAndResult();

        int dirX = getDir(startx, endx);
        int dirY = getDir(starty, endy);

        long[] fused = line.clone();

        for (int i = 0; i < fused.length - 1; i++) {
            if (fused[i] != EMPTY && fused[i] == fused[i + 1]) {
                fused[i] = fused[i] + fused[i + 1];
                fused[i + 1] = EMPTY;
                score += fused[i];

                // MOD UPGRADE AND POP
                lar.result.addUpgrade(new TileUpgrade(startx + dirX * i, starty + dirY * i, fused[i] / 2, fused[i]));
                lar.result.addPop(new TilePop(startx + dirX * (i+1), starty + dirY * (i+1)));

                i++;
            }
        }

        // Log.d("GAME2048", "fuseLine: " + Arrays.toString(line) + " to " + Arrays.toString(fused));

        lar.line = fused;

        return lar;
    }

    private LineAndResult updateLine(long[] line, int starty, int startx, int endy, int endx) {
        LineAndResult lamTot = new LineAndResult();

        // Log.d("GAME2048", "updateLine: Updating line...");
        LineAndResult lar1 = compressLine(line, startx, starty, endx, endy, true);
        LineAndResult lar2 = fuseLine(lar1.line, startx, starty, endx, endy);
        LineAndResult lar3 = compressLine(lar2.line, startx, starty, endx, endy, false);
        // Log.d("GAME2048", "updateLine: Line updated\n");

        lamTot.line = lar3.line;
        lamTot.result.concat(lar1.result);
        lamTot.result.concat(lar2.result);
        lamTot.result.concat(lar3.result);

        return lamTot;
    }

    public boolean isWon() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] >= winValue) return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        // Check for any empty cell
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == EMPTY) return false;
            }
        }
        // Check for any possible horizontal merge
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                if (grid[y][x] == grid[y][x + 1]) return false;
            }
        }
        // Check for any possible vertical merge
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == grid[y + 1][x]) return false;
            }
        }
        return true;
    }

    public long getScore() {
        return score;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                repr.append(grid[y][x]);
                if (x < width - 1) repr.append("; ");
            }
            repr.append("\n");
        }
        return repr.toString();
    }

    public void initRandom() {
        if (random == null) {
            random = new Random(randomSeed);
        }
    }

    public long[][] getGrid() {
        return grid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setGrid(long[][] grid) {
        this.grid = grid;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public void setRandomSeed(long seed) {
        this.randomSeed = seed;
        this.random = new Random(seed);
    }
}