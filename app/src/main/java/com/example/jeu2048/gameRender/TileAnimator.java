package com.example.jeu2048.gameRender;

import android.util.Log;

import com.example.jeu2048.game.result.TileMod;
import com.example.jeu2048.game.result.TileMove;
import com.example.jeu2048.game.result.TilePop;
import com.example.jeu2048.game.result.TileSpawn;
import com.example.jeu2048.game.result.TileUpgrade;

import java.util.ArrayList;

public class TileAnimator {
    public static DrawableTile animateMove(DrawableTile toAnimate, TileMove tileMove, double progress, float tileWidth, float tileHeight) {
        float targetX = tileMove.getToX() * tileWidth;
        float targetY = tileMove.getToY() * tileHeight;
        toAnimate.setAnimateX(targetX);
        toAnimate.setAnimateY(targetY);
        return toAnimate;
    }

    public static DrawableTile animatePop(DrawableTile toAnimate, TilePop tilePop, double progress, float tileWidth, float tileHeight) {
        if (progress > .9) {
            return null;
        }
        return toAnimate;
    }


    public static DrawableTile animateSpawn(DrawableTile toAnimate, TileSpawn tileSpawn, double progress, float tileWidth, float tileHeight) {
        return toAnimate;
    }


    public static DrawableTile animateUpgrade(DrawableTile toAnimate, TileUpgrade tileUpgrade, double progress, float tileWidth, float tileHeight) {
        return toAnimate;
    }

    public static ArrayList<DrawableTile> animateTiles(ArrayList<DrawableTile> tilesToAnimate, ArrayList<? extends TileMod> mods, double progress, float tileWidth, float tileHeight) {
        for (TileMod mod : mods) {
            Class<? extends TileMod> modClazz = mod.getClass();

            Log.d("GAME2048", "animateTiles: progress is " + progress + " for move " + mod.toString());

            if (modClazz.equals(TileMove.class)) {
                TileMove tileMove = (TileMove) mod;
                DrawableTile toAnimate = findByBasePosition(tilesToAnimate, tileMove.getFromX(), tileMove.getFromY(), tileWidth, tileHeight);
                tilesToAnimate.remove(toAnimate);
                DrawableTile animated = animateMove(toAnimate, tileMove, progress, tileWidth, tileHeight);
                tilesToAnimate.add(animated);
                Log.d("GAME2048", "animateTiles: for " + toAnimate + " after move readding " + animated);
            }
            else if (modClazz.equals(TilePop.class)) {
                TilePop tilePop = (TilePop) mod;
                DrawableTile toAnimate = findByBasePosition(tilesToAnimate, tilePop.getX(), tilePop.getY(), tileWidth, tileHeight);

                if (toAnimate != null) {
                    tilesToAnimate.remove(toAnimate);
                }

                DrawableTile animated = animatePop(toAnimate, tilePop, progress, tileWidth, tileHeight);
                
                if (animated != null) {
                    tilesToAnimate.add(animated);
                    Log.d("GAME2048", "animateTiles: for " + toAnimate + " after pop readding " + animated);
                }
            }
            else if (modClazz.equals(TileSpawn.class)) {
                TileSpawn tileSpawn = (TileSpawn) mod;
                DrawableTile toAnimate = findByBasePosition(tilesToAnimate, tileSpawn.getX(), tileSpawn.getY(), tileWidth, tileHeight);

                if (toAnimate != null) {
                    tilesToAnimate.remove(toAnimate);
                }

                DrawableTile animated = animateSpawn(toAnimate, tileSpawn, progress, tileWidth, tileHeight);
                if (animated != null) {
                    tilesToAnimate.add(animated);
                    Log.d("GAME2048", "animateTiles: for " + toAnimate + " after spawn readding " + animated);
                }
            }
            else if (modClazz.equals(TileUpgrade.class)) {
                TileUpgrade tileUpgrade = (TileUpgrade) mod;
                DrawableTile toAnimate = findByBasePosition(tilesToAnimate, tileUpgrade.getX(), tileUpgrade.getY(), tileWidth, tileHeight);
                tilesToAnimate.remove(toAnimate);
                DrawableTile animated = animateUpgrade(toAnimate, tileUpgrade, progress, tileWidth, tileHeight);
                tilesToAnimate.add(animated);
                Log.d("GAME2048", "animateTiles: for " + toAnimate + " after upgrade readding " + animated);
            }
        }

        return tilesToAnimate;
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
