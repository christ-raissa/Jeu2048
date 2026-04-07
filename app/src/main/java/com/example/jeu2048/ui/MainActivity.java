package com.example.jeu2048.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.ActivityMainBinding;
import com.example.jeu2048.settings.FontActivity;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends FontActivity {

    ActivityMainBinding binding;
    AnimatedBottomBar bottomNavigation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gestion des Insets (Barres système)
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

        // Navigation
        bottomNavigation = binding.bottomNavigation;
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_home);
        refreshAvatar();

        // Boutons de jeu
        binding.btnOnePlayer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OneUserGameActivity.class);
            startActivity(intent);
        });

        binding.btnTwoPlayers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MultiplayerGameActivity.class);
            startActivity(intent);
        });
    }


    private void refreshAvatar() {
        String avatarKey = settingsHelper.getAvatar();

        if (avatarKey != null && !avatarKey.isEmpty()) {
            try {
                int index = Integer.parseInt(avatarKey.replace("avatar_", ""));
                int[] avatars = getAvatarsArray();

                if (index >= 0 && index < avatars.length) {
                    binding.avatarImage.setImageResource(avatars[index]);
                }
            } catch (Exception e) {
                // En cas d'erreur, image par défaut
                binding.avatarImage.setImageResource(R.drawable.user_regular);
            }
        } else {
            binding.avatarImage.setImageResource(R.drawable.user_regular);
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