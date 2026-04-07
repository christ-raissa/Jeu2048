package com.example.jeu2048.theme;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.jeu2048.R;
import com.example.jeu2048.gameRender.GridRenderer;
import com.example.jeu2048.gameRender.TileRenderer;
import com.example.jeu2048.gameRender.gridRenderers.DefaultGridRenderer;
import com.example.jeu2048.gameRender.tileRenderers.DefaultTileRenderer;

public class Theme {
    private final GridRenderer gridRenderer;
    private final TileRenderer tileRenderer;

    public Theme(Context context, ThemeName name) {

        gridRenderer = new DefaultGridRenderer();
        tileRenderer = new DefaultTileRenderer();

        int gridColor = -1;
        int tileColor = -1;

        switch (name) {
            case Classic:
                gridColor = ContextCompat.getColor(context, R.color.grid_background_light);
                tileColor = ContextCompat.getColor(context, R.color.tuile_bacground_ligth);
                break;

            case System:
                gridColor = ContextCompat.getColor(context, R.color.grid_background_light);
                tileColor = ContextCompat.getColor(context, R.color.tuile_bacground_ligth);
                break;

            default:
                ContextCompat.getColor(context, R.color.grid_background_light);
                ContextCompat.getColor(context, R.color.tuile_bacground_ligth);
                break;
        }
        if (gridColor > -1 && tileColor > -1) {
            gridRenderer.updateColors(gridColor, tileColor);
        }
    }

    public GridRenderer getGridRenderer() {
        return gridRenderer;
    }

    public TileRenderer getTileRenderer() {
        return tileRenderer;
    }
}
