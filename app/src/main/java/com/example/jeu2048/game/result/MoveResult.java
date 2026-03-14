package com.example.jeu2048.game.result;

import java.util.ArrayList;

public class MoveResult {
    ArrayList<TileMod> mods;

    public MoveResult() {
        mods = new ArrayList<>();
    }

    public void addMod(TileMod mod) {
        mods.add(mod);
    }

    public void addMods(ArrayList<TileMod> newMods) {
        mods.addAll(newMods);
    }

    public ArrayList<TileMod> getMods() {
        return mods;
    }

    @Override
    public String toString() {
        return "MoveResult{" +
                "mods=" + mods +
                '}';
    }
}
