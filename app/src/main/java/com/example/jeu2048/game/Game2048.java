package com.example.jeu2048.game;

import androidx.annotation.NonNull;

import com.example.jeu2048.game.result.MoveResult;
import com.example.jeu2048.game.result.TileMod;
import com.example.jeu2048.game.result.TileMove;
import com.example.jeu2048.game.result.TilePop;
import com.example.jeu2048.game.result.TileSpawn;
import com.example.jeu2048.game.result.TileUpgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048 {
    private static class LineAndMods {
        public long[] line;
        public ArrayList<TileMod> mods;
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
    private final Random random;

    public Game2048(int width, int height, long winValue) {
        this.width = width;
        this.height = height;

        this.winValue = winValue;
        this.score = 0;

        this.random = new Random();

        initializeGrid();
    }

    public Game2048() {
        this(BASE_GRID_WIDTH, BASE_GRID_HEIGHT, BASE_WIN_VALUE);
    }

    public MoveResult initTiles() {
        MoveResult moveResult = new MoveResult();
        moveResult.addMod(spawnValue());
        moveResult.addMod(spawnValue());
        return moveResult;
    }

    private void initializeGrid() {
        grid = new long[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY;
            }
        }
    }

    public TileMod spawnValue() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == EMPTY) {
                    emptyCells.add(new int[]{y, x});
                }
            }
        }

        if (emptyCells.isEmpty()) return new TileMod();

        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        long value = (random.nextInt(10) == 0) ? 4 : 2;
        grid[cell[0]][cell[1]] = value;
        return new TileSpawn(cell[1], cell[0], value);
    }

    public MoveResult spawnValues(int amount) {
        MoveResult result = new MoveResult();
        for (int i = 0; i < amount; i++)
            result.addMod(spawnValue());
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

        moveResult.addMod(spawnValue());

        return moveResult;
    }

    private MoveResult moveLeft() {
        MoveResult result = new MoveResult();
        for (int y = 0; y < height; y++) {
            long[] line = new long[width];
            for (int x = 0; x < width; x++) {
                line[x] = grid[y][x];
            }
            LineAndMods res = updateLine(line, 0, y, width - 1, y);
            for (int x = 0; x < width; x++) {
                grid[y][x] = res.line[x];
            }

            result.addMods(res.mods);
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
            LineAndMods res = updateLine(line, width - 1, y, 0, y);
            for (int x = 0; x < width; x++) {
                grid[y][x] = res.line[width - 1 - x];
            }

            result.addMods(res.mods);
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
            LineAndMods res = updateLine(line, x, 0, x, height - 1);
            for (int y = 0; y < height; y++) {
                grid[y][x] = res.line[y];
            }

            result.addMods(res.mods);
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
            LineAndMods res = updateLine(line, x, height - 1, x, 0);
            for (int y = 0; y < height; y++) {
                grid[y][x] = res.line[height - 1 - y];
            }

            result.addMods(res.mods);
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

    private LineAndMods compressLine(long[] line, int startx, int starty, int endx, int endy) {
        LineAndMods lam = new LineAndMods();
        lam.mods = new ArrayList<>();

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
                    lam.mods.add(tileMove);
                }

                compressed[pos++] = line[i];
            }
        }

        lam.line = compressed;

        return lam;
    }

    private LineAndMods fuseLine(long[] line, int startx, int starty, int endx, int endy) {
        LineAndMods lam = new LineAndMods();
        lam.mods = new ArrayList<>();

        int dirX = getDir(startx, endx);
        int dirY = getDir(starty, endy);

        for (int i = 0; i < line.length - 1; i++) {
            if (line[i] != EMPTY && line[i] == line[i + 1]) {
                line[i] = line[i] + line[i + 1];
                line[i + 1] = EMPTY;
                score += line[i];

                // MOD UPGRADE AND POP
                lam.mods.add(new TileUpgrade(startx + dirX * i, starty + dirY * i, line[i] / 2, line[i]));
                lam.mods.add(new TilePop(startx + dirX * (i+1), starty + dirY * (i+1)));

                i++;
            }
        }

        lam.line = line;

        return lam;
    }

    private LineAndMods updateLine(long[] line, int startx, int starty, int endx, int endy) {
        LineAndMods lamTot = new LineAndMods();

        LineAndMods lam1 = compressLine(line, startx, starty, endx, endy);
        LineAndMods lam2 = fuseLine(lam1.line, startx, starty, endx, endy);
        LineAndMods lam3 = compressLine(lam2.line, startx, starty, endx, endy);

        lamTot.line = lam3.line;
        lamTot.mods = new ArrayList<>();
        lamTot.mods.addAll(lam1.mods);
        lamTot.mods.addAll(lam2.mods);
        lamTot.mods.addAll(lam3.mods);

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