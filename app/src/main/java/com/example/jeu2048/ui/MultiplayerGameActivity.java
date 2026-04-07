package com.example.jeu2048.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.jeu2048.databinding.ManyUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameMode;
import com.example.jeu2048.gameRender.GameView;
import com.example.jeu2048.gameRender.GameViewListener;
import com.example.jeu2048.gameRender.SavedGameView;
import com.example.jeu2048.settings.SettingsHelper;
import com.example.jeu2048.utils.CountUpTimer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MultiplayerGameActivity extends AppCompatActivity {

    private ManyUserGameActivityBinding binding;

    private CountUpTimer countUpTimer;
    private android.os.CountDownTimer countDownTimer;

    private long elapsedTime = 0;      // for count-up
    private long remainingTimeMillis;  // for count-down

    private TextView timerP1, timerP2;
    private GameView gameP1, gameP2;
    private ImageButton replayButton;

    private long gameDurationMillis;
    private GameMode mode;
    private SettingsHelper settingsHelper;
    private Dbhelper dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManyUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsHelper = new SettingsHelper(this);
        gameDurationMillis = settingsHelper.getMultiTimeLimit();
        mode = settingsHelper.getMultiMode();
        dba = new Dbhelper(this);

        timerP1 = binding.timerP1;
        timerP2 = binding.timerP2;
        gameP1 = binding.gamePlayer1;
        gameP2 = binding.gamePlayer2;
        replayButton = binding.replayButton;

        setupListeners();
        replayButton.setOnClickListener(v -> {
            startGames();
            resumeGame();
        });

        loadGames(); // restore previous state if exists
    }

    private void setupListeners() {
        GameViewListener listenerP1 = new GameViewListener() {
            @Override
            public void OnScoreChange(long from, long to) {
                binding.p1Score.setText(String.valueOf(to));
                if (to > Long.parseLong(binding.p1Best.getText().toString())) {
                    binding.p1Best.setText(String.valueOf(to));
                }
            }

            @Override
            public void OnStart() { }

            @Override
            public void OnGameOver(long dureeMillis) { triggerWin(2); }

            @Override
            public void OnGameWon(long dureeMillis) { triggerWin(1); }
        };
        GameViewListener listenerP2 = new GameViewListener() {
            @Override
            public void OnScoreChange(long from, long to) {
                binding.p2Score.setText(String.valueOf(to));
                if (to > Long.parseLong(binding.p2Best.getText().toString())) {
                    binding.p2Best.setText(String.valueOf(to));
                }
            }

            @Override
            public void OnStart() { }

            @Override
            public void OnGameOver(long dureeMillis) { triggerWin(1); }

            @Override
            public void OnGameWon(long dureeMillis) { triggerWin(2); }
        };
        gameP1.sub(listenerP1);
        gameP2.sub(listenerP2);
    }

    private void startGames() {
        clearSavedGames();

        gameP1.initGame();
        gameP2.initGame();
        gameP1.setPaused(false);
        gameP2.setPaused(false);

        if (mode == GameMode.TimeLimit) {
            startCountdown(gameDurationMillis);
        } else {
            startCountUp(elapsedTime);
        }

        binding.endGameMenu.setVisibility(ConstraintLayout.GONE);
    }


    private void startCountdown(long durationMillis) {
        remainingTimeMillis = durationMillis;

        countDownTimer = new android.os.CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                updateTimerUI(remainingTimeMillis);
            }

            @Override
            public void onFinish() {
                remainingTimeMillis = 0;
                updateTimerUI(0);
                onTimeUp();
            }
        }.start();
    }

    private void startCountUp(long startMillis) {
        countUpTimer = new CountUpTimer() {
            @Override
            public void onTick(long millis) {
                elapsedTime = millis;
                updateTimerUI(elapsedTime);
            }
        };
        countUpTimer.setStartTime(startMillis);
        countUpTimer.start();
    }

    private void updateTimerUI(long millis) {
        long seconds = millis / 1000;
        timerP1.setText(String.valueOf(seconds));
        timerP2.setText(String.valueOf(seconds));
    }


    private void pauseGame() {
        if (countDownTimer != null) { countDownTimer.cancel(); countDownTimer = null; }
        if (countUpTimer != null) { countUpTimer.stop(); }

        gameP1.setPaused(true);
        gameP2.setPaused(true);
    }

    private void resumeGame() {
        gameP1.setPaused(false);
        gameP2.setPaused(false);

        if (mode == GameMode.TimeLimit) {
            startCountdown(remainingTimeMillis);
        } else {
            startCountUp(elapsedTime);
        }
    }


    private void pauseAndSaveGames() {
        pauseGame();
        try {
            // Player 1
            SavedGameView saveP1 = SavedGameView.fromGameView(gameP1);
            saveP1.setElapsedTime(elapsedTime);
            ObjectOutputStream oos1 = new ObjectOutputStream(openFileOutput("saveP1.dat", MODE_PRIVATE));
            oos1.writeObject(saveP1);
            oos1.close();

            // Player 2
            SavedGameView saveP2 = SavedGameView.fromGameView(gameP2);
            saveP2.setElapsedTime(elapsedTime);
            ObjectOutputStream oos2 = new ObjectOutputStream(openFileOutput("saveP2.dat", MODE_PRIVATE));
            oos2.writeObject(saveP2);
            oos2.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadGames() {
        try {
            // Player 1
            ObjectInputStream ois1 = new ObjectInputStream(openFileInput("saveP1.dat"));
            SavedGameView saveP1 = (SavedGameView) ois1.readObject();
            saveP1.applyTo(gameP1);
            elapsedTime = saveP1.getElapsedTime();
            ois1.close();

            // Player 2
            ObjectInputStream ois2 = new ObjectInputStream(openFileInput("saveP2.dat"));
            SavedGameView saveP2 = (SavedGameView) ois2.readObject();
            saveP2.applyTo(gameP2);
            ois2.close();

            gameP1.setPaused(true);
            gameP2.setPaused(true);

        } catch (Exception e) {
            e.printStackTrace();
            startGames(); // fallback fresh game
        }
    }

    private void clearSavedGames() {
        deleteFile("saveP1.dat");
        deleteFile("saveP2.dat");
    }


    private void triggerWin(int numPlayer) {
        pauseAndSaveGames();
        clearSavedGames();

        gameP1.setPaused(true);
        gameP2.setPaused(true);

        String message = (numPlayer == 1 ? "Player 1 WON !!!" : "Player 2 WON !!!");
        binding.endMessageP1.setText(message);
        binding.endMessageP2.setText(message);
        binding.endGameMenu.setVisibility(ConstraintLayout.VISIBLE);
    }

    private void onTimeUp() {
        triggerWin(getWinningPlayer());
    }

    private int getWinningPlayer() {
        long s1 = gameP1.getScore();
        long s2 = gameP2.getScore();

        if (s1 > s2) return 1;
        if (s2 > s1) return 2;

        // tie breaker: fewer moves
        int nm1 = gameP1.getNumMoves();
        int nm2 = gameP2.getNumMoves();
        if (nm1 < nm2) return 1;
        if (nm2 < nm1) return 2;
        return 1;
    }


    @Override
    protected void onPause() { super.onPause(); pauseAndSaveGames(); }

    @Override
    protected void onStop() { super.onStop(); pauseAndSaveGames(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        if (countUpTimer != null) countUpTimer.stop();
    }
}