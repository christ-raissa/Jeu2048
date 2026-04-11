package com.example.jeu2048.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jeu2048.R;

import java.util.Locale;

public class SoundActivity extends AppCompatActivity {

    protected SettingsHelper settingsHelper;

    // Statique pour la persistance entre les écrans
    private static MediaPlayer mediaPlayer;
    private static int lastMusicIndex = -1;
    private static int activityCount = 0; // Compteur pour savoir si l'app est au premier plan

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsHelper = new SettingsHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityCount++; // Une activité démarre
        applyMusicSettings();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityCount--;
        if (activityCount == 0 && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // --- SECTION LANGUE ---
    @Override
    protected void attachBaseContext(Context newBase) {
        SettingsHelper sh = new SettingsHelper(newBase);
        String lang = sh.getLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = newBase.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    // section son
    public void applyMusicSettings() {
        boolean isSoundEnabled = settingsHelper.isSoundEnabled();
        int selectedIndex = settingsHelper.getSelectedMusicIndex();
        int[] musicFiles = {R.raw.son1, R.raw.son2, R.raw.son3, R.raw.son4, R.raw.son5};

        if (!isSoundEnabled) {
            stopMusic();
            return;
        }

        if (mediaPlayer == null || lastMusicIndex != selectedIndex) {
            stopMusic();
            try {
                lastMusicIndex = selectedIndex;
                mediaPlayer = MediaPlayer.create(getApplicationContext(), musicFiles[selectedIndex]);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Cas 2 : On revient de l'arrière-plan (Home -> Appli)
        else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.setOnErrorListener(null);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("SOUND_ERROR", "Erreur arrêt : " + e.getMessage());
            } finally {
                mediaPlayer = null;
                lastMusicIndex = -1;
            }
        }
    }

}