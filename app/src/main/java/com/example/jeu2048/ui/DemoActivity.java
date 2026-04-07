package com.example.jeu2048.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.jeu2048.R;
import com.example.jeu2048.settings.FontActivity;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class DemoActivity extends FontActivity {

    private VideoView videoView;
    private MaterialButton btnNext;
    private TextView messageText;
    private TextView langFR, langEN;

    // Variable pour mémoriser la position de la vidéo
    private int stopPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        videoView = findViewById(R.id.tutorialVideo);
        messageText = findViewById(R.id.messageText);
        btnNext = findViewById(R.id.btnNext);
        langFR = findViewById(R.id.langFR);
        langEN = findViewById(R.id.langEN);

        updateLanguageButtonsUI();

        // Clics langues
        langFR.setOnClickListener(v -> changeLanguage("fr"));
        langEN.setOnClickListener(v -> changeLanguage("en"));

        // Navigation
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToGame());
        btnNext.setOnClickListener(v -> goToGame());

        setupVideo();
    }

    // gestion du cycle de vie de la vidéo

    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On reprend là où on s'est arrêté
        videoView.seekTo(stopPosition);
        videoView.start();
    }

    // logique de la video
    private void setupVideo() {
        String currentLang = settingsHelper.getLanguage();
        int videoResId = "en".equals(currentLang) ? R.raw.demo_english : R.raw.demo;

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            // Si on revient d'un appel, on recalibre la position
            if (stopPosition > 0) {
                videoView.seekTo(stopPosition);
            }
            videoView.start();
        });

        // éviter un flash noir au tout début du chargement
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == mp.MEDIA_INFO_VIDEO_RENDERING_START) {
                videoView.setBackground(null); // Retire le fond si vous en aviez mis un
                return true;
            }
            return false;
        });
    }

    // --- LOGIQUE LANGUES ---

    private void updateLanguageButtonsUI() {
        String currentLang = settingsHelper.getLanguage();
        boolean isEn = "en".equals(currentLang);

        langEN.setTypeface(null, isEn ? Typeface.BOLD : Typeface.NORMAL);
        langEN.setAlpha(isEn ? 3.0f : 0.5f);

        langFR.setTypeface(null, isEn ? Typeface.NORMAL : Typeface.BOLD);
        langFR.setAlpha(isEn ? 0.5f : 3.0f);

        // Mise à jour dynamique du texte si nécessaire
        messageText.setText(getString(R.string.afficher));
    }

    private void changeLanguage(String langCode) {
        if (!langCode.equals(settingsHelper.getLanguage())) {
            settingsHelper.setLanguage(langCode);

            Locale locale = new Locale(langCode);
            Locale.setDefault(locale);
            Resources res = getResources();
            Configuration config = res.getConfiguration();
            config.setLocale(locale);
            res.updateConfiguration(config, res.getDisplayMetrics());

            // On redémarre l'activité proprement
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void goToGame() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}