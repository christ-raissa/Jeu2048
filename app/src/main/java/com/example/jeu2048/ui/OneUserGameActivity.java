package com.example.jeu2048.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameView;
import com.example.jeu2048.gameRender.GameViewListener;
import com.example.jeu2048.settings.FontActivity;
import com.example.jeu2048.settings.SettingsHelper;

public class OneUserGameActivity extends FontActivity implements GameViewListener {

    OneUserGameActivityBinding binding;
    private long meilleur = 0;
    private long numMoves = 0;
    Dbhelper dba;
    SettingsHelper settingsHelper;
    private Boolean isGameWon = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OneUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsHelper = new SettingsHelper(this);

        GameView gameView = binding.gameView;
        gameView.sub(this);

        binding.btnRestart.setOnClickListener(v -> {
            numMoves = 0;
            gameView.initGame();

        });

        binding.btnClassement.setOnClickListener(v -> {
            Intent intent = new Intent(OneUserGameActivity.this, StatistiqueActivity.class);
            startActivity(intent);
        });

        binding.btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(OneUserGameActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public void OnScoreChange(long from, long to) {
        if (to > meilleur) {
            meilleur = to;
            binding.bestScoreText.setText("" + meilleur);
        }
        binding.scoreText.setText("" + to);
        binding.movesText.setText("" + numMoves++);
    }

    @Override
    public void OnStart() {
        binding.scoreText.setText("0");
    }

    public void OnGameOver(long dureeMillis) {
        isGameWon = false;
        SauvegardePartie("Perdu", dureeMillis);
    }

    @Override
    public void OnGameWon(long dureeMillis) {
       isGameWon = true;
       SauvegardePartie("Partie gagné", dureeMillis);
    }

    public void SauvegardePartie(String resultat, long dureeMillis){
        String scoreStr = binding.scoreText.getText().toString().trim();
        long scoreFinal = 0;

        try {
            if (!scoreStr.isEmpty()) {
                scoreFinal = Long.parseLong(scoreStr);
            }
        } catch (NumberFormatException e) {
            scoreFinal = 0;
        }

        if (dba == null) {
            dba = new Dbhelper(this);
        }
        dba.insertScore(settingsHelper.getSingleUsername(), scoreFinal, numMoves, resultat);
    }
}