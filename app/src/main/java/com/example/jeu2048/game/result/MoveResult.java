package com.example.jeu2048.game.result;

import androidx.annotation.NonNull;

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

    @NonNull
    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        repr.append("MoveResult{");
        for (TileMod mod : mods) {
            repr.append(mod.toString());
            repr.append("; ");
        }
        repr.delete(repr.length() - 2, repr.length() - 1);
        repr.append("}");
        return repr.toString();
    }
}
