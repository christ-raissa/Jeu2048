package com.example.jeu2048.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.jeu2048.R;
import com.example.jeu2048.storage.ThemeManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this)
                .setKeepOnScreenCondition(() -> false);

        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );


        // Charge le layout XML de cette activité
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        int cornerRadius = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                15,
                getResources().getDisplayMetrics()
        );

        Glide.with(this)
                .asGif()
                .load(R.drawable.logo)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(cornerRadius)))
                .into(logo);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, DemoActivity.class));
            finish();
        }, SPLASH_DELAY);
    }
}