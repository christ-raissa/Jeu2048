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

        long totalMillis = dba.getTotalTime();
        binding.layoutStatsGeneral.tvTotalTime.setText(formatDuration(totalMillis));

        binding.layoutStatsGeneral.tvBestScore.setText(String.valueOf(dba.getBestScore()));
        binding.layoutStatsGeneral.tvTotalScore.setText(String.valueOf(dba.getTotalScore()));
        binding.layoutStatsGeneral.tvGamesPlayed.setText(String.valueOf(dba.getGamesPlayed()));
        binding.layoutStatsGeneral.tvTopTile.setText(String.valueOf(dba.getTopTile()));

        Cursor top3 = dba.getTop3Scores();
        if (top3 != null) {
            if (top3.moveToFirst()) {
                binding.layoutStatsGeneral.tvScore1.setText(top3.getString(top3.getColumnIndexOrThrow("score")));
                binding.layoutStatsGeneral.tvDate1.setText(top3.getString(top3.getColumnIndexOrThrow("date_score")));
            }
            if (top3.moveToNext()) {
                binding.layoutStatsGeneral.tvScore2.setText(top3.getString(top3.getColumnIndexOrThrow("score")));
                binding.layoutStatsGeneral.tvDate2.setText(top3.getString(top3.getColumnIndexOrThrow("date_score")));
            }
            if (top3.moveToNext()) {
                binding.layoutStatsGeneral.tvScore3.setText(top3.getString(top3.getColumnIndexOrThrow("score")));
                binding.layoutStatsGeneral.tvDate3.setText(top3.getString(top3.getColumnIndexOrThrow("date_score")));
            }
            top3.close();
        }
    }
}