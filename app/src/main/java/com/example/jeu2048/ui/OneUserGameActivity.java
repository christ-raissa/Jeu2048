package com.example.jeu2048.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameView;
import com.example.jeu2048.gameRender.GameViewListener;

public class OneUserGameActivity extends AppCompatActivity implements GameViewListener {

    OneUserGameActivityBinding binding;
    private long meilleur = 0;
    private long numMoves = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OneUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GameView gameView = binding.gameView;
        gameView.sub(this);

        binding.btnRestart.setOnClickListener(v -> {
            gameView.initGame();
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

    @Override
    public void OnGameOver() {

    }

    @Override
    public void OnGameWon() {

    }
}