package com.example.jeu2048.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jeu2048.databinding.ManyUserGameActivityBinding;
import com.example.jeu2048.databinding.OneUserGameActivityBinding;
import com.example.jeu2048.gameRender.GameView;

public class MultiplayerGameActivity extends AppCompatActivity {
    ManyUserGameActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ManyUserGameActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
