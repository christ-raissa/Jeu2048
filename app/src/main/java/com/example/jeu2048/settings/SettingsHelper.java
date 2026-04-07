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

    public SettingsHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ===========================
    // MUSIQUE
    // ===========================
    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_MUSIC_SELECTED_INDEX = "music_selected_index";

    public void setMusicEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply();
    }

    public boolean isMusicEnabled() {
        return prefs.getBoolean(KEY_MUSIC_ENABLED, false);
    }

    public void setMusicVolume(float volume) {
        prefs.edit().putFloat(KEY_MUSIC_VOLUME, volume).apply();
    }

    public float getMusicVolume() {
        return prefs.getFloat(KEY_MUSIC_VOLUME, 1.0f);
    }

    public void setSelectedMusicIndex(int index) {
        prefs.edit().putInt(KEY_MUSIC_SELECTED_INDEX, index).apply();
    }

    public int getSelectedMusicIndex() {
        return prefs.getInt(KEY_MUSIC_SELECTED_INDEX, 0);
    }

    public float getSystemMusicVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            return (float) currentVolume / maxVolume;
        }
        return getMusicVolume();
    }

    // ===========================
    // EFFETS SONORES
    // ===========================
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_SOUND_SELECTED = "sound_selected";

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundSelected(String soundName) {
        prefs.edit().putString(KEY_SOUND_SELECTED, soundName).apply();
    }

    public String getSoundSelected() {
        return prefs.getString(KEY_SOUND_SELECTED, "Bip");
    }

    // ===========================
    // UTILISATEUR
    // ===========================
    public void setUsername(String username) {
        prefs.edit().putString("username", username).apply();
    }

    public String getUsername() {
        return prefs.getString("username", "");
    }

    // ===========================
    // AVATAR
    // ===========================
    public void setAvatar(String avatarName) {
        prefs.edit().putString("avatar", avatarName).apply();
    }

    public String getAvatar() {
        return prefs.getString("avatar", "");
    }

    // ===========================
    // LANGUE
    // ===========================
    public void setLanguage(String lang) {
        prefs.edit().putString("language", lang).apply();
    }

    public String getLanguage() {
        return prefs.getString("language", "fr");
    }

    // ===========================
    // POLICE
    // ===========================
    private static final String KEY_POLICE_INDEX = "police_index";

    public void setPoliceIndex(int index) {
        prefs.edit().putInt(KEY_POLICE_INDEX, index).apply();
    }

    public int getPoliceIndex() {
        return prefs.getInt(KEY_POLICE_INDEX, 0);
    }

    // ===========================
    // STYLE / THEME
    // ===========================
    public void setTheme(ThemeName theme) {
        prefs.edit().putInt("theme", theme.ordinal()).apply();
    }

    public Theme getTheme(Context context) {
        int ordinal = prefs.getInt("theme", 0);
        return new Theme(context,ThemeName.values()[ordinal]);
    }

    private static final String KEY_UI_THEME = "ui_theme_mode";

    public void setUIThemeMode(int mode) {
        prefs.edit().putInt(KEY_UI_THEME, mode).apply();
    }

    public int getUIThemeMode() {
        // Par défaut 0 (Système)
        return prefs.getInt(KEY_UI_THEME, 0);
    }
    // ===========================
    // ANIMATIONS
    // ===========================
    public void setAnimationsEnabled(boolean enabled) {
        prefs.edit().putBoolean("animations_enabled", enabled).apply();
    }

    public boolean areAnimationsEnabled() {
        return prefs.getBoolean("animations_enabled", true);
    }

    public void setAnimationSpeed(float speed) {
        prefs.edit().putFloat("animation_speed", speed).apply();
    }

    public float getAnimationSpeed() {
        return prefs.getFloat("animation_speed", 0.1f);
    }

    // ===========================
    // SINGLEPLAYER
    // ===========================
    public void setSingleUsername(String username) {
        prefs.edit().putString("sp_username", username).apply();
    }

    public String getSingleUsername() {
        return prefs.getString("sp_username", "Player");
    }

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

    public void setSingleMode(GameMode mode) {
        prefs.edit().putInt("sp_mode", mode.ordinal()).apply();
    }

    public GameMode getSingleMode() {
        int ordinal = prefs.getInt("sp_mode", 0);
        return GameMode.values()[ordinal];
    }

    public void setSingleTargetScore(int score) {
        prefs.edit().putInt("sp_target_score", score).apply();
    }

    public int getSingleTargetScore() {
        return prefs.getInt("sp_target_score", 2048);
    }

    public void setSingleTimeLimit(int time) {
        prefs.edit().putInt("sp_time_limit", time).apply();
    }

    public int getSingleTimeLimit() {
        return prefs.getInt("sp_time_limit", 60);
    }

    // ===========================
    // MULTIPLAYER
    // ===========================
    public void setMultiUsername1(String username) {
        prefs.edit().putString("mp_username1", username).apply();
    }

    public String getMultiUsername1() {
        return prefs.getString("mp_username1", "Player 1");
    }

    public void setMultiUsername2(String username) {
        prefs.edit().putString("mp_username2", username).apply();
    }

    public String getMultiUsername2() {
        return prefs.getString("mp_username2", "Player 2");
    }

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

    public void setMultiMode(GameMode mode) {
        prefs.edit().putInt("mp_mode", mode.ordinal()).apply();
    }

    public GameMode getMultiMode() {
        int ordinal = prefs.getInt("mp_mode", 0);
        return GameMode.values()[ordinal];
    }

    public void setMultiTargetScore(int score) {
        prefs.edit().putInt("mp_target_score", score).apply();
    }

    public int getMultiTargetScore() {
        return prefs.getInt("mp_target_score", 2048);
    }

    public void setMultiTimeLimit(int time) {
        prefs.edit().putInt("mp_time_limit", time).apply();
    }

    public int getMultiTimeLimit() {
        return prefs.getInt("mp_time_limit", 60);
    }
}