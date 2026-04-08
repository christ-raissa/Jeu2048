package com.example.jeu2048.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.ActivityStatistiqueBinding;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;
import com.example.jeu2048.settings.FontActivity;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class StatistiqueActivity extends FontActivity {

    AnimatedBottomBar bottomNavigation;
    Dbhelper dba = new Dbhelper(this);
    ActivityStatistiqueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);

        // Mode plein écran
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        binding = ActivityStatistiqueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView btnRenitialise = findViewById(R.id.btnRestartDba);

        btnRenitialise.setOnClickListener(v ->{
           showResetConfirmation();
        });


        // Barre de navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_statistique);

        updateUI();

    }

    private String formatDuration(long millis) {
        if (millis <= 0) return "0s";
        long seconde = (millis / 1000) % 60;
        long minuite = (millis / (1000 * 60)) % 60;
        long heure = (millis / (1000 * 60 *60));
        StringBuffer sb = new StringBuffer();
        if (heure > 0){
            sb.append(heure).append("h");
        }
        if (minuite > 0){
            sb.append(String.format("%02dm" , minuite));
        }
        if (heure == 0){
            sb.append(String.format("%02ds", seconde));
        }
        return  sb.toString().trim();
    }

    private void updateUI() {
        String modeChoice = getIntent().getStringExtra("MODE_JEU");
        if (modeChoice == null) modeChoice = "SOLO";

        Cursor top3 = null;

        if (modeChoice.equals("MULTI")) {
            // --- States multi joueur ---
            binding.layoutStatsGeneral.tvBestScore.setText(String.valueOf(dba.getBestScoreMulti()));
            binding.layoutStatsGeneral.tvTotalScore.setText(String.valueOf(dba.getTotalScoreMulti()));
            binding.layoutStatsGeneral.tvGamesPlayed.setText(String.valueOf(dba.getGamesPlayedMulti()));


            long totalTimeMulti = dba.getTotalTimeMilti();
            binding.layoutStatsGeneral.tvTotalTime.setText(formatDuration(totalTimeMulti));

            // --- Record Multi joueur ---
            binding.layoutStatsGeneral.tv128Games.setText(String.valueOf(dba.getMaxTileMulti()));

            long bestMovesMulti = dba.getBestMovesMulti();
            binding.layoutStatsGeneral.tv128Mvt.setText(bestMovesMulti > 0 ? String.valueOf(bestMovesMulti) : "0");

            binding.layoutStatsGeneral.tv128Time.setText(String.valueOf(dba.getBestTimeMulti()));

            top3 = dba.getTop3Multiplayer();

        } else {
            //--- State Solo----
            binding.layoutStatsGeneral.tvBestScore.setText(String.valueOf(dba.getBestScore()));
            binding.layoutStatsGeneral.tvTotalScore.setText(String.valueOf(dba.getTotalScoreSolo()));
            binding.layoutStatsGeneral.tvGamesPlayed.setText(String.valueOf(dba.getGamesPlayedSolo()));

            long totalTimeSolo = dba.getTotalTimeSolo();
            binding.layoutStatsGeneral.tvTotalTime.setText(formatDuration(totalTimeSolo));

            // --- Record Solo ---
            long maxTile = dba.getMaxTileReachedSolo();
            Log.d("DEBUG_STATS", "Max Tile: " + maxTile);
            binding.layoutStatsGeneral.tv128Games.setText(String.valueOf(maxTile));
            long bestTime = dba.getBestTimeSolo();
            if (bestTime > 0) {
                binding.layoutStatsGeneral.tv128Time.setText(formatDuration(bestTime));
            }

            long bestMoves = dba.getBestMovesSolo();
            binding.layoutStatsGeneral.tv128Mvt.setText(bestMoves > 0 ? String.valueOf(bestMoves) : "0");

            top3 = dba.getTop3Scores();
        }

        if (top3 != null) {
            fillTop3(top3, modeChoice.equals("MULTI"));
        }
    }
    // Renitialiser la base de donnée
    private void showResetConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getText(R.string.text_renitialisé))
                .setMessage(R.string.message)
                .setPositiveButton(getText(R.string.suppimer), (dialog, which) -> {
                    dba.clearAllScores();

                    if (getIntent() != null) {
                        getIntent().removeExtra("CURRENT_TIME_128");
                        getIntent().removeExtra("CURRENT_MOVES_128");
                    }

                    updateUI();
                })
                .setNegativeButton(getText(R.string.dialog_annuler), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void fillTop3(Cursor cursor, boolean isMulti) {
        // Reset des vues
        binding.layoutStatsGeneral.tvScore1.setText("-");
        binding.layoutStatsGeneral.tvScore2.setText("-");
        binding.layoutStatsGeneral.tvScore3.setText("-");
        binding.layoutStatsGeneral.tvDate1.setText("");
        binding.layoutStatsGeneral.tvDate2.setText("");
        binding.layoutStatsGeneral.tvDate3.setText("");

        if (cursor != null) {
            int i = 1;
            while (cursor.moveToNext() && i <= 3) {
                String score = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.COLUMN_SCORE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.COLUMN_DATE));

                String displayScore = score;
                if (isMulti) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.COLUMN_PLAYER_NAME));
                    displayScore = name + " : " + score;
                }

                if (i == 1) {
                    binding.layoutStatsGeneral.tvScore1.setText(displayScore);
                    binding.layoutStatsGeneral.tvDate1.setText(date);
                } else if (i == 2) {
                    binding.layoutStatsGeneral.tvScore2.setText(displayScore);
                    binding.layoutStatsGeneral.tvDate2.setText(date);
                } else if (i == 3) {
                    binding.layoutStatsGeneral.tvScore3.setText(displayScore);
                    binding.layoutStatsGeneral.tvDate3.setText(date);
                }
                i++;
            }
            cursor.close();
        }
    }
}