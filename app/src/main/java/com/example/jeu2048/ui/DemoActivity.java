package com.example.jeu2048.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jeu2048.R;
import com.google.android.material.button.MaterialButton;

public class DemoActivity extends AppCompatActivity {

    private VideoView videoView;
    private MaterialButton btnNext;
    private TextView messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        videoView   = findViewById(R.id.tutorialVideo);
        messageText = findViewById(R.id.messageText);
        btnNext = findViewById(R.id.btnNext);

        // Texte explicatif
        messageText.setText(
                "Fusionnez les tuiles pour atteindre 2048.\n" +
                        "La partie se termine lorsque la grille est pleine."
        );

        // Boutons
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToGame());
        btnNext.setOnClickListener(v -> goToGame());

        playVideo();
    }

    private void playVideo() {

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.demo);

        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));
        videoView.start();
    }

    private void goToGame() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}