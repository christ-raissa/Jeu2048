package com.example.jeu2048.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jeu2048.databinding.ManyUserGameActivityBinding;

public class MultiplayerGameActivity extends AppCompatActivity {

    ManyUserGameActivityBinding binding;

    private CountDownTimer timer1;
    private CountDownTimer timer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManyUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView timerP1 = binding.timerP1;
        TextView timerP2 = binding.timerP2;

        // Example: 10 seconds timers
        startTimer(timerP1, 10000, () -> onPlayer1TimeUp());
        startTimer(timerP2, 10000, () -> onPlayer2TimeUp());
    }

    private void startTimer(TextView textView, long durationMillis, Runnable onFinish) {
        CountDownTimer timer = new CountDownTimer(durationMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                textView.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                textView.setText("0");
                onFinish.run();
            }
        }.start();

        // Optional: store references if you want to cancel later
        if (textView == binding.timerP1) {
            timer1 = timer;
        } else if (textView == binding.timerP2) {
            timer2 = timer;
        }
    }

    private void onPlayer1TimeUp() {
        // TODO: your logic here
        System.out.println("Player 1 time up!");
    }

    private void onPlayer2TimeUp() {
        // TODO: your logic here
        System.out.println("Player 2 time up!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Prevent memory leaks
        if (timer1 != null) timer1.cancel();
        if (timer2 != null) timer2.cancel();
    }
}