package com.example.jeu2048.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.jeu2048.R;
import com.example.jeu2048.databinding.ActivityMainBinding;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    AnimatedBottomBar bottomNavigation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        bottomNavigation = binding.bottomNavigation;
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_home);

        // Bouton Débuter
        binding.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OneUserGameActivity.class);
            startActivity(intent);
        });

        binding.btnTwoPlayers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MultiplayerGameActivity.class);
            startActivity(intent);
        });
    }
}