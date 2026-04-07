package com.example.jeu2048.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.ActivityStatistiqueBinding;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class StatistiqueActivity extends AppCompatActivity {

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

        // Barre de navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_inventory);

        updateUI();

    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        return hours + "h " + String.format("%02dm", minutes);
    }

    private void updateUI() {
        String modeChoice = getIntent().getStringExtra("MODE_JEU");
        if (modeChoice == null) modeChoice = "SOLO";

        Cursor top3;

        if (modeChoice.equals("MULTI")) {
            // --- STATS MULTI ---
            binding.layoutStatsGeneral.tvBestScore.setText(String.valueOf(dba.getBestScoreMulti()));
            binding.layoutStatsGeneral.tvTotalScore.setText(String.valueOf(dba.getTotalScoreMulti()));
            binding.layoutStatsGeneral.tvGamesPlayed.setText(String.valueOf(dba.getGamesPlayedMulti()));

            binding.layoutStatsGeneral.tvTotalTime.setText(String.valueOf(dba.getTotalTimeMilti()));

            top3 = dba.getTop3Multiplayer();
        } else {
            // --- STATS SOLO ---
            binding.layoutStatsGeneral.tvBestScore.setText(String.valueOf(dba.getBestScore()));
            binding.layoutStatsGeneral.tvTotalScore.setText(String.valueOf(dba.getTotalScoreSolo()));

            binding.layoutStatsGeneral.tvGamesPlayed.setText(String.valueOf(dba.getGamesPlayedSolo()));

            long totalMillis = dba.getTotalTimeSolo();
            binding.layoutStatsGeneral.tvTotalTime.setText(formatDuration(totalMillis));

            top3 = dba.getTop3Scores();
        }

        fillTop3(top3, modeChoice.equals("MULTI"));
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