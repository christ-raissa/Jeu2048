package com.example.jeu2048.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.example.jeu2048.gameRender.GameMode;
import com.example.jeu2048.theme.Theme;
import com.example.jeu2048.theme.ThemeName;

public class SettingsHelper {
    private static final String PREF_NAME = "game_settings";
    private final SharedPreferences prefs;

    /**
     * Initialize the helper.
     * @param context any valid context (Activity, Application, etc.)
     */
    public SettingsHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // =========================================================
    // SOUND SETTINGS
    // =========================================================

    /**
     * Music volume (range 0.0 - 1.0).
     */
    public void setMusicVolume(float value) {
        prefs.edit().putFloat("music_volume", value).apply();
    }

    public float getMusicVolume() {
        return prefs.getFloat("music_volume", 1.0f);
    }

    public void setMusicEnabled(boolean enabled) {
        prefs.edit().putBoolean("music_enabled", enabled).apply();
    }

    public boolean isMusicEnabled() {
        return prefs.getBoolean("music_enabled", false);
    }
    public float getSystemMusicVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            // On calcule le ratio : volume actuel / volume max
            return (float) currentVolume / maxVolume;
        }
        return getMusicVolume(); // Valeur par défaut si erreur
    }

    /**
     * Sound effects volume (range 0.0 - 1.0).
     */
    public void setSfxVolume(float value) {
        prefs.edit().putFloat("sfx_volume", value).apply();
    }

    public float getSfxVolume() {
        return prefs.getFloat("sfx_volume", 1.0f);
    }

    // =========================================================
    // STYLE SETTINGS
    // =========================================================

    /**
     * Theme identifier.
     * Interpretation depends on UI layer.
     */
    public void setTheme(ThemeName theme) {
        prefs.edit().putInt("theme", theme.ordinal()).apply();
    }

    public Theme getTheme(Context context) {
        int ordinal = prefs.getInt("theme", 0);
        return new Theme(context, ThemeName.values()[ordinal]);
    }

    /**
     * Whether animations are enabled.
     */
    public void setAnimationsEnabled(boolean enabled) {
        prefs.edit().putBoolean("animations_enabled", enabled).apply();
    }

    public boolean areAnimationsEnabled() {
        return prefs.getBoolean("animations_enabled", true);
    }

    /**
     * Animation speed multiplier.
     * Expected range is 0 - 1.
     */
    public void setAnimationSpeed(float speed) {
        prefs.edit().putFloat("animation_speed", speed).apply();
    }

    public float getAnimationSpeed() {
        return prefs.getFloat("animation_speed", 0.1f);
    }

    // =========================================================
    // SINGLEPLAYER SETTINGS
    // =========================================================

    /**
     * Username for singleplayer mode.
     */
    public void setSingleUsername(String username) {
        prefs.edit().putString("sp_username", username).apply();
    }

    public String getSingleUsername() {
        return prefs.getString("sp_username", "Player");
    }

    /**
     * Grid size.
     * Rows and columns are stored separately.
     * (Assumes rectangular grid.)
     */
    public void setSingleGridSize(int rows, int cols) {
        prefs.edit()
                .putInt("sp_rows", rows)
                .putInt("sp_cols", cols)
                .apply();
    }

    public int getSingleRows() {
        return prefs.getInt("sp_rows", 4);
    }

    public int getSingleCols() {
        return prefs.getInt("sp_cols", 4);
    }

    /**
     * Game mode identifier.
     * 0 = score-based
     * 1 = time-based
     * (This mapping may not match your actual implementation.)
     */
    public void setSingleMode(GameMode mode) {
        prefs.edit().putInt("sp_mode", mode.ordinal()).apply();
    }

    public GameMode getSingleMode() {
        int ordinal = prefs.getInt("sp_mode", 0);
        return GameMode.values()[ordinal];
    }

    /**
     * Target score for score-based mode.
     * Used only when mode matches expected value.
     */
    public void setSingleTargetScore(int score) {
        prefs.edit().putInt("sp_target_score", score).apply();
    }

    public int getSingleTargetScore() {
        return prefs.getInt("sp_target_score", 2048);
    }

    /**
     * Time limit in seconds (assumed).
     * Used only for time-based mode.
     */
    public void setSingleTimeLimit(long time) {
        prefs.edit().putLong("sp_time_limit", time).apply();
    }

    public long getSingleTimeLimit() {
        return prefs.getLong("sp_time_limit", 60_000);
    }

    // =========================================================
    // MULTIPLAYER SETTINGS
    // =========================================================

    /**
     * Username for player 1.
     */
    public void setMultiUsername1(String username) {
        prefs.edit().putString("mp_username1", username).apply();
    }

    public String getMultiUsername1() {
        return prefs.getString("mp_username1", "Player 1");
    }

    /**
     * Username for player 2.
     */
    public void setMultiUsername2(String username) {
        prefs.edit().putString("mp_username2", username).apply();
    }

    public String getMultiUsername2() {
        return prefs.getString("mp_username2", "Player 2");
    }

    /**
     * Grid size for multiplayer.
     * Same assumptions as singleplayer.
     */
    public void setMultiGridSize(int rows, int cols) {
        prefs.edit()
                .putInt("mp_rows", rows)
                .putInt("mp_cols", cols)
                .apply();
    }

    public int getMultiRows() {
        return prefs.getInt("mp_rows", 4);
    }

    public int getMultiCols() {
        return prefs.getInt("mp_cols", 4);
    }

    /**
     * Multiplayer mode identifier.
     * Meaning is shared with singleplayer (assumed).
     */
    public void setMultiMode(GameMode mode) {
        prefs.edit().putInt("mp_mode", mode.ordinal()).apply();
    }

    public GameMode getMultiMode() {
        int ordinal = prefs.getInt("mp_mode", 0);
        return GameMode.values()[ordinal];
    }

    /**
     * Target score for multiplayer.
     * May be applied per player or globally (unclear).
     */
    public void setMultiTargetScore(int score) {
        prefs.edit().putInt("mp_target_score", score).apply();
    }

    public int getMultiTargetScore() {
        return prefs.getInt("mp_target_score", 2048);
    }

    /**
     * Time limit for multiplayer mode.
     * Unit and synchronization between players not defined.
     */
    public void setMultiTimeLimit(long time) {
        prefs.edit().putLong("mp_time_limit", time).apply();
    }

    public long getMultiTimeLimit() {
        return prefs.getLong("mp_time_limit", 60_000);
    }
}