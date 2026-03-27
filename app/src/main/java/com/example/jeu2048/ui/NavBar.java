package com.example.jeu2048.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import androidx.core.content.ContextCompat;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import com.example.jeu2048.R;

public class NavBar {

    public static void setupBottomNavigation(
            Activity activity,
            AnimatedBottomBar bottomBar,
            int selectedItemId) {

        // Détecter le thème actuel
        boolean isDark = (activity.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // Appliquer les couleurs selon le thème
        if (isDark) {
            bottomBar.setTabColor(
                    ContextCompat.getColor(activity, R.color.bottom_nav_item_dark)
            );
            bottomBar.setTabColorSelected(
                    ContextCompat.getColor(activity, R.color.bottom_nav_item_selected_dark)
            );
            bottomBar.setIndicatorColor(
                    ContextCompat.getColor(activity, R.color.bottom_nav_item_selected_dark)
            );
        } else {
            bottomBar.setTabColor(
                    ContextCompat.getColor(activity, R.color.bottom_nav_item_light)
            );
            bottomBar.setTabColorSelected(
                    ContextCompat.getColor(activity, R.color.bottom_nav_item_selected_light)
            );
            bottomBar.setIndicatorColor(
                    ContextCompat.getColor(activity, R.color.bottom_nav_item_selected_light)
            );
        }

        // Sélectionner l'onglet actif
        if (selectedItemId == R.id.nav_home) {
            bottomBar.selectTabAt(0, false);
        } else if (selectedItemId == R.id.nav_inventory) {
            bottomBar.selectTabAt(1, false);
        } else if (selectedItemId == R.id.nav_users) {
            bottomBar.selectTabAt(2, false);
        } else if (selectedItemId == R.id.nav_settings) {
            bottomBar.selectTabAt(3, false);
        }

        // Gérer les clics
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(
                    int lastIndex,
                    AnimatedBottomBar.Tab lastTab,
                    int newIndex,
                    AnimatedBottomBar.Tab newTab) {

                int id = newTab.getId();

                if (id == R.id.nav_home && !(activity instanceof MainActivity)) {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                } else if (id == R.id.nav_inventory && !(activity instanceof InventoryActivity)) {
                    activity.startActivity(new Intent(activity, InventoryActivity.class));
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                } else if (id == R.id.nav_users && !(activity instanceof UsersActivity)) {
                    activity.startActivity(new Intent(activity, UsersActivity.class));
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                } else if (id == R.id.nav_settings && !(activity instanceof SettingActivity)) {
                    activity.startActivity(new Intent(activity, SettingActivity.class));
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                }
            }

            @Override
            public void onTabReselected(int index, AnimatedBottomBar.Tab tab) {}
        });
    }
}