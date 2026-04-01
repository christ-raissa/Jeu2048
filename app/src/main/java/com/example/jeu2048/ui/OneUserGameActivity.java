package com.example.jeu2048.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameView;
import com.example.jeu2048.gameRender.GameViewListener;

public class OneUserGameActivity extends AppCompatActivity implements GameViewListener {

    OneUserGameActivityBinding binding;
    private long meilleur = 0;
    private long numMoves = 0;
    private String nom_Joueur = "Anonyme";
    Dbhelper dba;
    private Boolean isGameWon = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OneUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GameView gameView = binding.gameView;
        gameView.sub(this);

        binding.btnRestart.setOnClickListener(v -> {
            numMoves = 0;
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


    public void OnGameOver() {
        if (!isGameWon){
            SauvegardePartie("Perdu");
        }else {
            SauvegardePartie("Partie gagné fini");
        }
    }

    @Override
    public void OnGameWon() {
       isGameWon = true;
       SauvegardePartie("Partie gagné");
    }

    public void SauvegardePartie(String resultat){
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
        dba.insertScore(nom_Joueur, scoreFinal, numMoves, resultat);
    }
}