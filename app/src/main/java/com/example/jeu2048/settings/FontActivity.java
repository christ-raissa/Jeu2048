package com.example.jeu2048.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.jeu2048.R;

import java.util.Locale;

public class FontActivity extends AppCompatActivity {

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

    /*
    // --- SECTION POLICE ---
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        final View root = getWindow().getDecorView().getRootView();
        root.post(this::refreshFont);
    }

    protected void refreshFont() {
        View root = getWindow().getDecorView().getRootView();
        applyFontToView(root);
    }

    private void applyFontToView(View view) {
        int[] fonts = {R.font.robotomono_regular, R.font.montserrat_regular, R.font.fredoka_regular, R.font.comicneue_regular};
        int index = settingsHelper.getPoliceIndex();
        if (index < 0 || index >= fonts.length) index = 0;
        try {
            Typeface typeface = ResourcesCompat.getFont(this, fonts[index]);
            applyRecursively(view, typeface);
            view.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyRecursively(View view, Typeface typeface) {
        if (view instanceof TextView) { ((TextView) view).setTypeface(typeface); }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) { applyRecursively(group.getChildAt(i), typeface); }
        }
    }
    */
}