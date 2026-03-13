package com.example.jeu2048.game;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048 {
    private static final int BASE_GRID_WIDTH = 4;
    private static final int BASE_GRID_HEIGHT = 4;

    private static final long BASE_WIN_VALUE = 2048;

    private static final long EMPTY = 0;

    private int width;
    private int height;
    private long[][] grid;
    private long winValue;
    private long score;
    private final Random random;

    public Game2048(int width, int height, long winValue) {
        this.width = width;
        this.height = height;

        this.winValue = winValue;
        this.score = 0;


        this.random = new Random();

        initializeGrid();

        spawnValue();
        spawnValue();
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

    public void spawnValue() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == EMPTY) {
                    emptyCells.add(new int[]{y, x});
                }
            }
        }

        if (emptyCells.isEmpty()) return;

        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        grid[cell[0]][cell[1]] = (random.nextInt(10) == 0) ? 4 : 2;
    }

    public void moveLeft() {
        for (int y = 0; y < height; y++) {
            long[] line = new long[width];
            for (int x = 0; x < width; x++) {
                line[x] = grid[y][x];
            }
            line = updateLine(line);
            for (int x = 0; x < width; x++) {
                grid[y][x] = line[x];
            }
        }

        spawnValue();
    }

    public void moveRight() {
        for (int y = 0; y < height; y++) {
            long[] line = new long[width];
            for (int x = 0; x < width; x++) {
                line[width - 1 - x] = grid[y][x];
            }
            line = updateLine(line);
            for (int x = 0; x < width; x++) {
                grid[y][x] = line[width - 1 - x];
            }
        }

        spawnValue();
    }

    public void moveUp() {
        for (int x = 0; x < width; x++) {
            long[] line = new long[height];
            for (int y = 0; y < height; y++) {
                line[y] = grid[y][x];
            }
            line = updateLine(line);
            for (int y = 0; y < height; y++) {
                grid[y][x] = line[y];
            }
        }

        spawnValue();
    }

    public void moveDown() {
        for (int x = 0; x < width; x++) {
            long[] line = new long[height];
            for (int y = 0; y < height; y++) {
                line[height - 1 - y] = grid[y][x];
            }
            line = updateLine(line);
            for (int y = 0; y < height; y++) {
                grid[y][x] = line[height - 1 - y];
            }
        }

        spawnValue();
    }

    private long[] compressLine(long[] line) {
        long[] compressed = new long[line.length];
        int pos = 0;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != EMPTY) {
                compressed[pos++] = line[i];
            }
        }
        return compressed;
    }

    private long[] fuseLine(long[] line) {
        for (int i = 0; i < line.length - 1; i++) {
            if (line[i] != EMPTY && line[i] == line[i + 1]) {
                line[i] = line[i] + line[i + 1];
                line[i + 1] = EMPTY;
                score += line[i];
                i++;
            }
        }
        return line;
    }

    private long[] updateLine(long[] line) {
        line = compressLine(line);
        line = fuseLine(line);
        line = compressLine(line);
        return line;
    }

    public boolean isWon() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] >= winValue) return true;
            }
        }
        return false;
    }

    /**
     * Returns true if no moves are possible (grid full and no adjacent equal tiles).
     */
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
                if (x < width - 1) repr.append("; ");
                repr.append(grid[y][x]);
            }
            repr.append("\n");
        }
        return repr.toString();
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
}