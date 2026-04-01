package com.example.jeu2048.game.result;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MoveResult {
    ArrayList<TileMove> firstMoves;

    ArrayList<TileUpgrade> upgrades;
    ArrayList<TilePop> pops;

    ArrayList<TileMove> lastMoves;
    ArrayList<TileSpawn> spawns;

    public MoveResult() {
        firstMoves = new ArrayList<>();
        upgrades = new ArrayList<>();
        pops = new ArrayList<>();
        lastMoves = new ArrayList<>();
        spawns = new ArrayList<>();
    }

    public void addFirstPartMove(TileMove tileMove) {
        firstMoves.add(tileMove);
    }

    public void addLastPartMove(TileMove tileMove) {
        lastMoves.add(tileMove);
    }

    public void addUpgrade(TileUpgrade upgrade) {
        upgrades.add(upgrade);
    }

    public void addPop(TilePop pop) {
        pops.add(pop);
    }

    public void addSpawn(TileSpawn spawn) {
        spawns.add(spawn);
    }

    public ArrayList<TileMove> getFirstMoves() {
        return firstMoves;
    }

    public ArrayList<TileUpgrade> getUpgrades() {
        return upgrades;
    }

    public ArrayList<TilePop> getPops() {
        return pops;
    }

    public ArrayList<TileMove> getLastMoves() {
        return lastMoves;
    }

    public ArrayList<TileSpawn> getSpawns() {
        return spawns;
    }

    public void concat(MoveResult other) {
        firstMoves.addAll(other.getFirstMoves());
        upgrades.addAll(other.getUpgrades());
        pops.addAll(other.getPops());
        lastMoves.addAll(other.getLastMoves());
        spawns.addAll(other.getSpawns());
    }

    @Override
    public String toString() {
        return "MoveResult{" +
                "firstMoves=" + firstMoves +
                ", upgrades=" + upgrades +
                ", pops=" + pops +
                ", lastMoves=" + lastMoves +
                ", spawns=" + spawns +
                '}';
    }
}
