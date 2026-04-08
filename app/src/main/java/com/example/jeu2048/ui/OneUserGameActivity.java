package com.example.jeu2048.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.FontAssetDelegate;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameMode;
import com.example.jeu2048.gameRender.GameView;
import com.example.jeu2048.gameRender.GameViewListener;
import com.example.jeu2048.gameRender.SavedGameView;
import com.example.jeu2048.settings.FontActivity;
import com.example.jeu2048.settings.SettingsHelper;
import com.example.jeu2048.utils.CountUpTimer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OneUserGameActivity extends FontActivity implements GameViewListener {

    OneUserGameActivityBinding binding;
    private long meilleur = 0;
    private long numMoves = 0;
    Dbhelper dba;
    SettingsHelper settingsHelper;
    private boolean isSaving = false; // Pour éviter les doubles sauvegardes

    private CountDownTimer countDownTimer;
    private CountUpTimer countUpTimer;
    private long elapsedTimeMillis = 0; // track elapsed time

    private GameMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OneUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsHelper = new SettingsHelper(this);

        loadGame();

        mode = settingsHelper.getSingleMode();

        GameView gameView = binding.gameView;
        gameView.sub(this);

        binding.btnRestart.setOnClickListener(v -> {
            clearSavedGame();
            numMoves = 0;
            gameView.initGame();

        });
        dba = new Dbhelper(this);

        binding.btnClassement.setOnClickListener(v -> {
            Intent intent = new Intent(OneUserGameActivity.this, StatistiqueActivity.class);
            startActivity(intent);
        });

        binding.btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(OneUserGameActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        startTimer();
    }


    @Override
    public void OnScoreChange(long from, long to) {
        if (to > meilleur) {
            meilleur = to;
            binding.bestScoreText.setText("" + meilleur);
        }
        binding.scoreText.setText("" + to);

        numMoves = binding.gameView.getNumMoves();
        binding.movesText.setText("" + numMoves);
    }

    @Override
    public void OnStart() {
        binding.scoreText.setText("0");
    }

    public void OnGameOver(long dureeMillis) {
        if (isSaving) return;
        isSaving = true;
        SauvegardePartie("Perdu", dureeMillis);
        clearSavedGame();
        pauseTimers();
        displayEndMenu("GAME OVER", "Perdu", dureeMillis);
    }

    @Override
    public void OnGameWon(long dureeMillis) {
       SauvegardePartie("Partie gagné", dureeMillis);

        clearSavedGame();
        pauseTimers();
        displayEndMenu("VICTOIRE !", "Gagné", dureeMillis);
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

        GameView gameView = binding.gameView;


        long maxTile = gameView.getMaxTile();

        dba.insertScore(
                settingsHelper.getSingleUsername(),
                scoreFinal,
                numMoves,
                resultat,
                maxTile,
                dureeMillis
        );
    }

    private void ouvrirStatistiques() {
        Intent intent = new Intent(OneUserGameActivity.this, StatistiqueActivity.class);
        startActivity(intent);
        finish();
    }
    private void displayEndMenu(String titre, String resultat, long dureeMillis) {
        binding.endGameMenu.setVisibility(android.view.View.VISIBLE);
        binding.gameView.setPaused(true);

        binding.tvEndStatus.setText(titre);

        String scoreStr = binding.scoreText.getText().toString();
        String message = "Score : " + scoreStr + "\n" +
                "Mouvements : " + numMoves + "\n" +
                "Durée : " + (dureeMillis / 1000) + " sec";

        binding.endMessageSolo.setText(message);

        // Bouton Rejouer
        binding.replayButton.setOnClickListener(v -> {
            isSaving = false;
            numMoves = 0;
            binding.endGameMenu.setVisibility(android.view.View.GONE);
            binding.gameView.setPaused(false);
            binding.gameView.initGame();
            startTimer(); // Relancer le chrono
        });

        // Bouton Statistiques
        binding.btnStats.setOnClickListener(v -> {
            ouvrirStatistiques();
        });


        binding.btnShare.setOnClickListener(v -> {
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Mon score au 2048 : " + scoreStr);
            startActivity(android.content.Intent.createChooser(shareIntent, "Partager mon score"));
        });
    }

    private void startTimer() {
        pauseTimers(); // cancel existing timers

        if (mode == GameMode.TimeLimit) {
            countDownTimer = new CountDownTimer(settingsHelper.getSingleTimeLimit(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    elapsedTimeMillis = settingsHelper.getSingleTimeLimit() - millisUntilFinished;
                    updateTimerText(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    elapsedTimeMillis = settingsHelper.getSingleTimeLimit();
                    binding.timerText.setText("0");
                    binding.gameView.setPaused(true);
                    OnGameOver(elapsedTimeMillis);
                }
            }.start();
        } else {
            // Target score mode: count up
            countUpTimer = new CountUpTimer() {
                @Override
                public void onTick(long millis) {
                    elapsedTimeMillis = millis;
                    updateTimerText();
                }
            };
            countUpTimer.start();
        }
    }

    private void pauseTimers() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (countUpTimer != null) countUpTimer.stop();
    }

    private void updateTimerText() {
        Log.d("GAME2048", "updateTimerText: updating timer...");
        binding.timerText.setText(String.valueOf(elapsedTimeMillis / 1000));
    }

    private void updateTimerText(long millisRemaining) {
        binding.timerText.setText(String.valueOf(millisRemaining / 1000));
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimers();
        saveGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseTimers();
        saveGame();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        pauseTimers();
        saveGame();
    }

    private void saveGame() {
        try (FileOutputStream fos = openFileOutput("save.dat", MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            SavedGameView save = SavedGameView.fromGameView(binding.gameView);
            save.setElapsedTime(elapsedTimeMillis);
            oos.writeObject(save);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGame() {
        try (FileInputStream fis = openFileInput("save.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            SavedGameView save = (SavedGameView) ois.readObject();
            save.applyTo(binding.gameView);
            elapsedTimeMillis = save.getElapsedTime();
            updateTimerText();

        } catch (Exception e) {
            binding.gameView.initGame(); // fallback
        }
    }

    private void clearSavedGame() {
        deleteFile("save.dat");
        elapsedTimeMillis = 0;
        updateTimerText();
    }
}