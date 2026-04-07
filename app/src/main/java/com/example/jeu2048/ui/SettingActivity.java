package com.example.jeu2048.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.jeu2048.R;
import com.example.jeu2048.settings.Music_Manager;
import com.example.jeu2048.settings.SettingsHelper;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class SettingActivity extends AppCompatActivity {

    AnimatedBottomBar bottomNavigation;
    SwitchCompat swithtmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        //swithtmusic = findViewById(R.id.music_select);

        SettingsHelper settingsHelper = new SettingsHelper(this);

       /* swithtmusic.setChecked(settingsHelper.isMusicEnabled());

        swithtmusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsHelper.setMusicEnabled(isChecked);
            if (isChecked) {
                Music_Manager.play(this, R.raw.music);
            } else {
                Music_Manager.stop();
            }
        }); */

        bottomNavigation = findViewById(R.id.bottomNavigation);
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_settings);
    }
}