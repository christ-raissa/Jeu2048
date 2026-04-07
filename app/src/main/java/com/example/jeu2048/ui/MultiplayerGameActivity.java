package com.example.jeu2048.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.jeu2048.databinding.ManyUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameView;
import com.example.jeu2048.gameRender.GameViewListener;
import com.example.jeu2048.gameRender.SavedGameView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MultiplayerGameActivity extends AppCompatActivity {

    ManyUserGameActivityBinding binding;

    private CountDownTimer gameTimer;

    TextView timerP1;
    TextView timerP2;

    GameView gameP1;
    GameView gameP2;

    ImageButton replayButton;

    long gameDurationMillis = 60_000;
    long remainingTimeMillis;

    Dbhelper dba;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManyUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timerP1 = binding.timerP1;
        timerP2 = binding.timerP2;

        gameP1 = binding.gamePlayer1;
        gameP2 = binding.gamePlayer2;

        replayButton = binding.replayButton;
        dba = new Dbhelper(this);

        gameP1.sub(new GameViewListener() {
            @Override
            public void OnScoreChange(long from, long to) {
                if (to > Long.parseLong(binding.p1Best.getText().toString())) {
                    binding.p1Best.setText("" + to);
                }
                binding.p1Score.setText("" + to);
            }

            @Override
            public void OnStart() {

            }

            @Override
            public void OnGameOver(long dureeMillis) {
                triggerWin(2);
            }

            @Override
            public void OnGameWon(long dureeMillis) {
                triggerWin(1);
            }
        });

        gameP2.sub(new GameViewListener() {
            @Override
            public void OnScoreChange(long from, long to) {
                if (to > Long.parseLong(binding.p2Best.getText().toString())) {
                    binding.p2Best.setText("" + to);
                }
                binding.p2Score.setText("" + to);
            }

            @Override
            public void OnStart() {}

            @Override
            public void OnGameOver(long dureeMillis) {
                triggerWin(1);
            }

            @Override
            public void OnGameWon(long dureeMillis) {
                triggerWin(2);
            }
        });

        replayButton.setOnClickListener(v -> {
            startGames();
        });

        loadGames();
    }

    private void startTimer(long durationMillis, Runnable onFinish) {
        remainingTimeMillis = durationMillis;

        gameTimer = new CountDownTimer(durationMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;

                updateTimerUI();
            }

            @Override
            public void onFinish() {
                remainingTimeMillis = 0;

                timerP1.setText("0");
                timerP2.setText("0");
                onFinish.run();
            }
        }.start();
    }

    private void startGames() {
        gameP1.initGame();
        gameP2.initGame();
        gameP1.setPaused(false);
        gameP2.setPaused(false);

        startTimer(gameDurationMillis, this::onTimeUp);

        binding.endGameMenu.setVisibility(ConstraintLayout.GONE);
    }

    private void setEndMessagesText(String text) {
        binding.endMessageP1.setText(text);
        binding.endMessageP2.setText(text);
    }

    private void triggerWin(int numPlayer) {
        clearSavedGames();

        gameP1.setPaused(true);
        gameP2.setPaused(true);

        if (numPlayer == 1) {
            setEndMessagesText("Player 1 WON !!!");
        }

        if (numPlayer == 2) {
            setEndMessagesText("Player 2 WON !!!");
        }

        binding.endGameMenu.setVisibility(ConstraintLayout.VISIBLE);
    }

    private void onTimeUp() {
        System.out.println("Time is up!");
        triggerWin(getWinningPlayer());
    }

    private int getWinningPlayer() {
        long s1 = gameP1.getScore();
        long s2 = gameP2.getScore();

        if (s1 > s2) {
            return 1;
        } else if (s2 > s1) {
            return 2;
        } else {
            int nm1 = gameP1.getNumMoves();
            int nm2 = gameP2.getNumMoves();

            if (nm1 < nm2) {
                return 1;
            } else if (nm2 < nm1) {
                return 2;
            } else {
                return 1;
            }
        }
    }

    private void updateTimerUI() {
        long seconds = remainingTimeMillis / 1000;
        timerP1.setText(String.valueOf(seconds));
        timerP2.setText(String.valueOf(seconds));
    }

    private void pauseGame() {
        if (gameTimer != null) gameTimer.cancel();

        gameP1.setPaused(true);
        gameP2.setPaused(true);
    }

    private void resumeGame() {
        gameP1.setPaused(false);
        gameP2.setPaused(false);

        startTimer(remainingTimeMillis, this::onTimeUp);
    }

    private void pauseAndSaveGames() {
        pauseGame();

        try {
            // Save Player 1
            SavedGameView saveP1 = SavedGameView.fromGameView(gameP1);
            FileOutputStream fos1 = openFileOutput("saveP1.dat", MODE_PRIVATE);
            ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
            oos1.writeObject(saveP1);
            oos1.close();
            fos1.close();

            // Save Player 2
            SavedGameView saveP2 = SavedGameView.fromGameView(gameP2);
            FileOutputStream fos2 = openFileOutput("saveP2.dat", MODE_PRIVATE);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            oos2.writeObject(saveP2);
            oos2.close();
            fos2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGames() {
        try {
            // Load Player 1
            FileInputStream fis1 = openFileInput("saveP1.dat");
            ObjectInputStream ois1 = new ObjectInputStream(fis1);
            SavedGameView saveP1 = (SavedGameView) ois1.readObject();
            saveP1.applyTo(gameP1);
            ois1.close();
            fis1.close();

            // Load Player 2
            FileInputStream fis2 = openFileInput("saveP2.dat");
            ObjectInputStream ois2 = new ObjectInputStream(fis2);
            SavedGameView saveP2 = (SavedGameView) ois2.readObject();
            saveP2.applyTo(gameP2);
            ois2.close();
            fis2.close();

            // Pause until user resumes
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

    @Override
    protected void onPause() {
        super.onPause();
        pauseAndSaveGames();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseAndSaveGames();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameTimer != null) gameTimer.cancel();
    }
}