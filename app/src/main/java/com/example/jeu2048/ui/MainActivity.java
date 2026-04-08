package com.example.jeu2048.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.ActivityMainBinding;
import com.example.jeu2048.settings.FontActivity;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends FontActivity {

    private ActivityMainBinding binding;
    private AnimatedBottomBar bottomNavigation;
    private Dbhelper dba;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dba = new Dbhelper(this);

        // Gestion des Insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Plein écran
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Navigation basse
        bottomNavigation = binding.bottomNavigation;
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_home);
        refreshAvatar();

        // --- BOUTONS DE JEU ---
        binding.btnOnePlayer.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OneUserGameActivity.class));
        });

        binding.btnTwoPlayers.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MultiplayerGameActivity.class));
        });
    }

    /**
     * Affiche le popup de statistiques
     */
    private void showRankingPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_classement, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView imgAvatar = dialogView.findViewById(R.id.dialogAvatar);
        TextView tvBest = dialogView.findViewById(R.id.tvMeilleur);
        TextView tvTotal = dialogView.findViewById(R.id.tvScoreTotal);
        TextView tvTime = dialogView.findViewById(R.id.tvTempsTotal);
        Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);

        updateAvatarInImageView(imgAvatar);

        // Données DB
        tvBest.setText("Meilleur : " + dba.getBestScore());
        tvTotal.setText("Score total : " + dba.getTotalScoreSolo());

        long totalMillis = dba.getTotalTimeSolo();
        long hours = (totalMillis / 1000) / 3600;
        long minutes = ((totalMillis / 1000) % 3600) / 60;
        tvTime.setText(String.format("Temps total : %dh %02dm", hours, minutes));

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void refreshAvatar() {
        updateAvatarInImageView(binding.avatarImage);
    }

    private void updateAvatarInImageView(ImageView view) {
        String avatarKey = settingsHelper.getAvatar();
        if (avatarKey != null && !avatarKey.isEmpty()) {
            try {
                int index = Integer.parseInt(avatarKey.replace("avatar_", ""));
                int[] avatars = getAvatarsArray();
                if (index >= 0 && index < avatars.length) {
                    view.setImageResource(avatars[index]);
                }
            } catch (Exception e) {
                view.setImageResource(R.drawable.user_regular);
            }
        } else {
            view.setImageResource(R.drawable.user_regular);
        }
    }

    private int[] getAvatarsArray() {
        return new int[]{
                R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
                R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6,
                R.drawable.avatar7, R.drawable.avatar8
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAvatar();
    }
}