package com.example.jeu2048.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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

    private boolean isSaving = false;

    private CountDownTimer countDownTimer;
    private CountUpTimer countUpTimer;

    private long elapsedTimeMillis = 0;

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

        dba = new Dbhelper(this);

        binding.btnRestart.setOnClickListener(v -> {
            clearSavedGame();
            numMoves = 0;
            elapsedTimeMillis = 0;

            gameView.initGame();
            startTimer();
        });

        binding.btnClassement.setOnClickListener(v -> {
            startActivity(new Intent(this, StatistiqueActivity.class));
        });

        binding.btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingActivity.class));
        });

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimers();
        saveGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    // ================= SCORE =================
    @Override
    public void OnScoreChange(long from, long to) {
        if (to > meilleur) {
            meilleur = to;
            binding.bestScoreText.setText(String.valueOf(meilleur));
        }

        binding.scoreText.setText(String.valueOf(to));

        numMoves = binding.gameView.getNumMoves();
        binding.movesText.setText(numMoves + " " + getString(R.string.coups));
    }

    @Override
    public void OnStart() {
        binding.scoreText.setText("0");
    }

    // ================= GAME OVER =================
    public void OnGameOver(long dureeMillis) {

        if (isSaving) return;
        isSaving = true;

        playSound();

        SauvegardePartie("Perdu", dureeMillis);

        clearSavedGame();
        pauseTimers();

        displayEndMenu("GAME OVER", "Perdu", dureeMillis);
    }

    @Override
    public void OnGameWon(long dureeMillis) {

        playSound();

        SauvegardePartie("Gagné", dureeMillis);

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
                settingsHelper.getUsername(),
                scoreFinal,
                numMoves,
                resultat,
                maxTile,
                dureeMillis
        );
    }
    // ================= SON =================
    private void playSound() {
        try {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.lose);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= POPUP =================
    private void displayEndMenu(String titre, String resultat, long dureeMillis) {

        binding.endGameMenuSolo.setVisibility(android.view.View.VISIBLE);
        binding.gameView.setPaused(true);

        binding.tvEndStatus.setText(titre);

        String scoreStr = binding.scoreText.getText().toString();

        String message = getText(R.string.partage_score) + " : " + scoreStr + "\n" +
                getText(R.string.partage_coups) + " : " + numMoves + "\n" +
                getText(R.string.partage_duree) + " : " + (dureeMillis / 1000) + " sec";

        binding.endMessageSolo.setText(message);

        binding.replayButtonSolo.setOnClickListener(v -> {
            isSaving = false;
            numMoves = 0;
            elapsedTimeMillis = 0;

            binding.endGameMenuSolo.setVisibility(android.view.View.GONE);

            binding.gameView.setPaused(false);
            binding.gameView.initGame();

            startTimer();
        });

        binding.btnStatsSolo.setOnClickListener(v -> {
            startActivity(new Intent(this, StatistiqueActivity.class));
        });

        binding.btnShareSolo.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(intent, "Partager via"));
        });
    }

    // ================= TIMER =================
    private void startTimer() {

        pauseTimers();

        if (mode == GameMode.TimeLimit) {

            countDownTimer = new CountDownTimer(settingsHelper.getSingleTimeLimit(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    elapsedTimeMillis =
                            settingsHelper.getSingleTimeLimit() - millisUntilFinished;

                    binding.timerText.setText(String.valueOf(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    OnGameOver(elapsedTimeMillis);
                }
            }.start();

        } else {

            long startOffset = elapsedTimeMillis;

            countUpTimer = new CountUpTimer() {
                @Override
                public void onTick(long millis) {
                    elapsedTimeMillis = startOffset + millis;
                    binding.timerText.setText(String.valueOf(elapsedTimeMillis / 1000));
                }
            };

            countUpTimer.start();
        }
    }

    private void pauseTimers() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (countUpTimer != null) countUpTimer.stop();
    }

    // ================= SAVE =================
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

        } catch (Exception e) {
            binding.gameView.initGame();
        }
    }

    private void clearSavedGame() {
        deleteFile("save.dat");
        elapsedTimeMillis = 0;
    }
}