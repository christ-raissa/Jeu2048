package com.example.jeu2048.theme;

import com.example.jeu2048.gameRender.GridRenderer;
import com.example.jeu2048.gameRender.TileRenderer;
import com.example.jeu2048.gameRender.gridRenderers.DefaultGridRenderer;
import com.example.jeu2048.gameRender.tileRenderers.DefaultTileRenderer;

public class Theme {
    private final GridRenderer gridRenderer;
    private final TileRenderer tileRenderer;

    public Theme(ThemeName name) {
        switch (name) {
            case Classic:
                gridRenderer = new DefaultGridRenderer();
                tileRenderer = new DefaultTileRenderer();
                break;

            default:
                gridRenderer = new DefaultGridRenderer();
                tileRenderer = new DefaultTileRenderer();
                break;
        }
    }

    public GridRenderer getGridRenderer() {
        return gridRenderer;
    }

    public TileRenderer getTileRenderer() {
        return tileRenderer;
    }
}
