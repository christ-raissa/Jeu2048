package com.example.jeu2048.settings;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.jeu2048.R;

public class Music_Manager {

    private static MediaPlayer mediaPlayer;
    public static void play(Context context, int resId) {
        SettingsHelper settingsHelper = new SettingsHelper(context);

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.setLooping(true);
        }
        float volume =  settingsHelper.getSystemMusicVolume(context);
        mediaPlayer.setVolume(volume, volume);

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }


}
