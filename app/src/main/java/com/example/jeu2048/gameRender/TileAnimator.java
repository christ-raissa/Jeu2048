package com.example.jeu2048.gameRender;

import android.util.Log;

import com.example.jeu2048.game.result.TileMod;
import com.example.jeu2048.game.result.TileMove;
import com.example.jeu2048.game.result.TilePop;
import com.example.jeu2048.game.result.TileSpawn;
import com.example.jeu2048.game.result.TileUpgrade;

import java.util.ArrayList;

public class TileAnimator {
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static DrawableTile animateMove(
            DrawableTile toAnimate,
            TileMove tileMove,
            double progress,
            float tileWidth,
            float tileHeight) {

        if (toAnimate == null) return null; // ✅ FIX

        float targetX = tileMove.getToX() * tileWidth;
        float targetY = tileMove.getToY() * tileHeight;

        float currentX = toAnimate.getX();
        float currentY = toAnimate.getY();

        float newX = lerp(currentX, targetX, (float) progress);
        float newY = lerp(currentY, targetY, (float) progress);

        toAnimate.setAnimateX(newX);
        toAnimate.setAnimateY(newY);

        if (progress >= 1.0f) {
            toAnimate.setAnimateX(targetX);
            toAnimate.setAnimateY(targetY);
        }

        return toAnimate;
    }
    public static DrawableTile animatePop(DrawableTile toAnimate, TilePop tilePop, double progress, float tileWidth, float tileHeight) {
        if (progress > .8) {
            return null;
        }
        return toAnimate;
    }


    public static DrawableTile animateSpawn(DrawableTile toAnimate, TileSpawn tileSpawn, double progress, float tileWidth, float tileHeight) {

        // Création initiale de la tuile
        if (toAnimate == null) {
            toAnimate = new DrawableTile(
                    tileSpawn.getX() * tileWidth,
                    tileSpawn.getY() * tileHeight,
                    tileWidth,
                    tileHeight,
                    tileSpawn.getValue()
            );

            toAnimate.setScale(0f);
        }
        float scale = (float) Math.min(1.0, progress);
        scale = (float) Math.sin(scale * Math.PI / 2);
        toAnimate.setScale(scale);
        if (progress == 1) {
            toAnimate.setScale(1f);
        }

        return toAnimate;
    }

    public static DrawableTile animateUpgrade(DrawableTile toAnimate, TileUpgrade tileUpgrade, double progress, float tileWidth, float tileHeight) {
        if (progress > .5) {
            toAnimate.setValue(tileUpgrade.getTo());
        }
        return toAnimate;
    }

    public static ArrayList<DrawableTile> animateTiles(
            ArrayList<DrawableTile> tilesToAnimate,
            ArrayList<? extends TileMod> mods,
            double progress,
            float tileWidth,
            float tileHeight) {

        ArrayList<DrawableTile> newTiles = new ArrayList<>(tilesToAnimate);

        for (TileMod mod : mods) {

            Class<? extends TileMod> modClazz = mod.getClass();

            if (modClazz.equals(TileMove.class)) {

                TileMove tileMove = (TileMove) mod;

                DrawableTile toAnimate = findByBasePosition(
                        newTiles,
                        tileMove.getFromX(),
                        tileMove.getFromY(),
                        tileWidth,
                        tileHeight
                );

                if (toAnimate != null) {
                    newTiles.remove(toAnimate);
                    DrawableTile animated = animateMove(toAnimate, tileMove, progress, tileWidth, tileHeight);
                    newTiles.add(animated);
                }
            }

            else if (modClazz.equals(TilePop.class)) {

                TilePop tilePop = (TilePop) mod;

                DrawableTile toAnimate = findByBasePosition(
                        newTiles,
                        tilePop.getX(),
                        tilePop.getY(),
                        tileWidth,
                        tileHeight
                );

                if (toAnimate != null) {
                    newTiles.remove(toAnimate);
                }

                DrawableTile animated = animatePop(toAnimate, tilePop, progress, tileWidth, tileHeight);

                if (animated != null) {
                    newTiles.add(animated);
                }
            }

            else if (modClazz.equals(TileSpawn.class)) {

                TileSpawn tileSpawn = (TileSpawn) mod;

                DrawableTile toAnimate = findByBasePosition(
                        newTiles,
                        tileSpawn.getX(),
                        tileSpawn.getY(),
                        tileWidth,
                        tileHeight
                );

                if (toAnimate != null) {
                    newTiles.remove(toAnimate);
                }

                DrawableTile animated = animateSpawn(toAnimate, tileSpawn, progress, tileWidth, tileHeight);

                if (animated != null) {
                    newTiles.add(animated);
                }
            }

            else if (modClazz.equals(TileUpgrade.class)) {

                TileUpgrade tileUpgrade = (TileUpgrade) mod;

                DrawableTile toAnimate = findByBasePosition(
                        newTiles,
                        tileUpgrade.getX(),
                        tileUpgrade.getY(),
                        tileWidth,
                        tileHeight
                );

                if (toAnimate != null) {
                    newTiles.remove(toAnimate);
                    DrawableTile animated = animateUpgrade(toAnimate, tileUpgrade, progress, tileWidth, tileHeight);
                    newTiles.add(animated);
                }
            }
        }

        return newTiles;
    }
    private static DrawableTile findByBasePosition(ArrayList<DrawableTile> tiles, float x, float y, float tileWidth, float tileHeight) {
        for (DrawableTile tile : tiles) {
            if (tile.getX() == x * tileWidth && tile.getY() == y * tileHeight) {
                return tile;
            }
        }
        return null;
    }
}
