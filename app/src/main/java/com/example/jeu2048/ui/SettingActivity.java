package com.example.jeu2048.ui;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.example.jeu2048.R;
import com.example.jeu2048.databinding.ActivitySettingBinding;
import com.example.jeu2048.gameRender.GameMode;
import com.example.jeu2048.settings.FontActivity;

import java.util.Arrays;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class SettingActivity extends FontActivity {

    private RadioGroup radioGroupSoundStatus, radioGroupTheme;
    private RadioButton radioLight, radioDark, radioSystem, radioSoundOn, radioSoundOff;

    private TextView tvUsername, tvAvatarStatus;
    private ImageView imgAvatar, ivEditUsername, ivChangeAvatar;

    private Spinner spinnerLang, spinnerSound, spinnerPolice;
    private AnimatedBottomBar bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        bottomNavigation = findViewById(R.id.bottomNavigation);
        NavBar.setupBottomNavigation(this, bottomNavigation, R.id.nav_settings);

        setupThemeSettings();
        setupSoundSettings();
        setupUsernameSettings();
        setupAvatarSettings();
        setupLanguageSpinner();
        // setupPoliceSpinner();
        setupMusicSpinner();
        setupScoreSpinners();
        setupModeSpinners();
        setupInputTexts();
    }

    // methode gestion de la boite de dialogue
    private void colorizeDialog(AlertDialog dialog) {
        dialog.show();

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }

        int colorRose = ContextCompat.getColor(this, R.color.bottom_filled_dark);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorRose);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
    }


    // section utilisateur partie nom
    private void setupUsernameSettings() {
        tvUsername = findViewById(R.id.tvUsername);
        ivEditUsername = findViewById(R.id.ivEditUsername);

        refreshUsernameUI(settingsHelper.getUsername());

        View.OnClickListener listener = v -> showUsernameDialog();
        tvUsername.setOnClickListener(listener);
        ivEditUsername.setOnClickListener(listener);
    }

    private void refreshUsernameUI(String name) {
        if (name == null || name.isEmpty()) {
            tvUsername.setText(getString(R.string.username_hint));
            tvUsername.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            ivEditUsername.setVisibility(View.GONE);
        } else {
            updateUsernameText(name);
            ivEditUsername.setVisibility(View.VISIBLE);
        }
    }

    private void updateUsernameText(String name) {
        String prefix = (String) getText(R.string.parametre_pseudo_pre);
        String fullName = prefix + name;
        SpannableStringBuilder spannable = new SpannableStringBuilder(fullName);
        int colorRose = ContextCompat.getColor(this, R.color.bottom_filled_dark);

        spannable.setSpan(new ForegroundColorSpan(colorRose),
                prefix.length(), fullName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                prefix.length(), fullName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvUsername.setText(spannable);
    }

    private void showUsernameDialog() {
        EditText input = new EditText(this);
        input.setText(settingsHelper.getUsername());
        input.setPadding(50, 40, 50, 40);

        // Sécurité pour la visibilité du texte
        input.setTextColor(ContextCompat.getColor(this, android.R.color.primary_text_light));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getText(R.string.parametre_changer_nom))
                .setView(input)
                .setPositiveButton(getText(R.string.dialog_valider), (d, which) -> {
                    String name = input.getText().toString().trim();
                    settingsHelper.setUsername(name);
                    refreshUsernameUI(name);
                })
                .setNegativeButton(getText(R.string.dialog_annuler), null)
                .create();

        colorizeDialog(dialog);
    }

    // section utilisateur partie avatar
    private void setupAvatarSettings() {
        imgAvatar = findViewById(R.id.imgAvatar);
        tvAvatarStatus = findViewById(R.id.tvAvatarStatus);
        ivChangeAvatar = findViewById(R.id.ivChangeAvatar);

        refreshAvatarUI(settingsHelper.getAvatar());

        View.OnClickListener listener = v -> showAvatarDialog();
        tvAvatarStatus.setOnClickListener(listener);
        ivChangeAvatar.setOnClickListener(listener);
    }

    private void refreshAvatarUI(String avatarKey) {
        if (avatarKey == null || avatarKey.isEmpty()) {
            imgAvatar.setImageResource(R.drawable.user_regular);
            tvAvatarStatus.setText(getText(R.string.avatar_select));
            ivChangeAvatar.setVisibility(View.GONE);
        } else {
            try {
                int index = Integer.parseInt(avatarKey.replace("avatar_", ""));
                imgAvatar.setImageResource(getAvatarsArray()[index]);
                tvAvatarStatus.setText(getText(R.string.avatar_select));
                ivChangeAvatar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                imgAvatar.setImageResource(R.drawable.user_regular);
            }
        }
    }

    private int[] getAvatarsArray() {
        return new int[]{
                R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
                R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6,
                R.drawable.avatar7, R.drawable.avatar8
        };
    }

    private void showAvatarDialog() {
        int[] avatars = getAvatarsArray();
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_avatar_grid, null);
        GridLayout grid = dialogView.findViewById(R.id.gridAvatars);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getText(R.string.avatar_select))
                .setView(dialogView)
                .setNegativeButton(getText(R.string.dialog_annuler), null)
                .create();

        for (int i = 0; i < avatars.length; i++) {
            final int index = i;
            ImageView img = new ImageView(this);
            img.setImageResource(avatars[i]);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 180; // Taille réduite pour un dialogue plus compact
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);

            img.setLayoutParams(params);
            img.setPadding(10, 10, 10, 10);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setBackgroundResource(R.drawable.bg_avatar_item);
            img.setClickable(true);
            img.setOnClickListener(v -> {
                settingsHelper.setAvatar("avatar_" + index);
                refreshAvatarUI("avatar_" + index);
                dialog.dismiss();
            });
            grid.addView(img);
        }
        colorizeDialog(dialog);
    }


    // section pour la gestion du thème
    private void setupThemeSettings() {
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);
        radioSystem = findViewById(R.id.radioSystem);

        int colorRose = ContextCompat.getColor(this, R.color.bottom_filled_dark);

        // On garde votre syntaxe d'origine pour les couleurs
        CompoundButtonCompat.setButtonTintList(radioLight, android.content.res.ColorStateList.valueOf(colorRose));
        CompoundButtonCompat.setButtonTintList(radioDark, android.content.res.ColorStateList.valueOf(colorRose));
        CompoundButtonCompat.setButtonTintList(radioSystem, android.content.res.ColorStateList.valueOf(colorRose));

        //On vérifie la valeur sauvegardée
        int savedTheme = settingsHelper.getUIThemeMode();
        if (savedTheme == 1) {
            radioLight.setChecked(true);
        } else if (savedTheme == 2) {
            radioDark.setChecked(true);
        } else {
            radioSystem.setChecked(true);
        }

        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int mode, themeChoice;
            if (checkedId == R.id.radioLight) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                themeChoice = 1;
            }
            else if (checkedId == R.id.radioDark) {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                themeChoice = 2;
            }
            else {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                themeChoice = 0;
            }

            settingsHelper.setUIThemeMode(themeChoice);
            AppCompatDelegate.setDefaultNightMode(mode);
        });
    }


    // Section son
    private void setupSoundSettings() {
        radioGroupSoundStatus = findViewById(R.id.radioGroupSoundStatus);
        radioSoundOn = findViewById(R.id.radioSoundOn);
        radioSoundOff = findViewById(R.id.radioSoundOff);

        int colorRose = ContextCompat.getColor(this, R.color.bottom_filled_dark);
        CompoundButtonCompat.setButtonTintList(radioSoundOn, android.content.res.ColorStateList.valueOf(colorRose));
        CompoundButtonCompat.setButtonTintList(radioSoundOff, android.content.res.ColorStateList.valueOf(colorRose));

        boolean soundEnabled = settingsHelper.isSoundEnabled();
        radioSoundOn.setChecked(soundEnabled);
        radioSoundOff.setChecked(!soundEnabled);

        radioGroupSoundStatus.setOnCheckedChangeListener((group, checkedId) -> {
            settingsHelper.setSoundEnabled(checkedId == R.id.radioSoundOn);
            applyMusicSettings();
        });
    }


    // section langue
    private void setupLanguageSpinner() {
        spinnerLang = findViewById(R.id.spinnerLang);
        String[] langues = {"Français", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, langues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLang.setAdapter(adapter);
        spinnerLang.setSelection("en".equals(settingsHelper.getLanguage()) ? 1 : 0);
        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirst = true;
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) { isFirst = false; return; }
                String lang = (position == 0) ? "fr" : "en";
                if (!lang.equals(settingsHelper.getLanguage())) {
                    settingsHelper.setLanguage(lang);
                    recreate();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // methode pour la gestion du son
    private void setupMusicSpinner() {
        spinnerSound = findViewById(R.id.spinnerSound);
        String[] musiques = {"Son 1","Son 2","Son 3","Son 4","Son 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, musiques);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSound.setAdapter(adapter);
        spinnerSound.setSelection(settingsHelper.getSelectedMusicIndex());
        spinnerSound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirst = true;
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (isFirst) { isFirst = false; return; }
                settingsHelper.setSelectedMusicIndex(pos);
                applyMusicSettings();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupScoreSpinners() {
        Spinner spinner1 = findViewById(R.id.spinnerScoreSolo);
        Spinner spinner2 = findViewById(R.id.spinnerScoreMulti);
        List<String> options = Arrays.asList("2048", "1024", "512", "256", "128");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);

        int soloIndex = options.indexOf(String.valueOf(settingsHelper.getSingleTargetScore()));
        if (soloIndex < 0) soloIndex = 0;
        spinner1.setSelection(soloIndex, false);

        int multiIndex = options.indexOf(String.valueOf(settingsHelper.getMultiTargetScore()));
        if (multiIndex < 0) multiIndex = 0;
        spinner2.setSelection(multiIndex, false);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choix = options.get(position);
                int score = Integer.parseInt(choix);
                settingsHelper.setSingleTargetScore(score);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                settingsHelper.setSingleTargetScore(2048);
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choix = options.get(position);
                int score = Integer.parseInt(choix);
                settingsHelper.setMultiTargetScore(score);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                settingsHelper.setSingleTargetScore(2048);
            }
        });
    }

    private void setupModeSpinners() {
        Spinner spinner1 = findViewById(R.id.spinnerModeSolo);
        Spinner spinner2 = findViewById(R.id.spinnerModeMulti);
        List<String> options = Arrays.asList("Score", "Chrono");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner2.setAdapter(adapter2);

        switch (settingsHelper.getSingleMode()) {
            case TimeLimit:
                spinner1.setSelection(1, false);
                break;
            case ScoreObjective:
                spinner1.setSelection(0, false);
                break;
            default:
                spinner1.setSelection(0, false);
                break;
        }

        switch (settingsHelper.getMultiMode()) {
            case TimeLimit:
                spinner2.setSelection(1, false);
                break;
            case ScoreObjective:
                spinner2.setSelection(0, false);
                break;
            default:
                spinner2.setSelection(1, false);
                break;
        }

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choix = options.get(position);
                if (choix.equals("Chrono")) {
                    settingsHelper.setSingleMode(GameMode.TimeLimit);
                } else if (choix.equals("Chrono")) {
                    settingsHelper.setSingleMode(GameMode.ScoreObjective);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                settingsHelper.setSingleMode(GameMode.ScoreObjective);
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choix = options.get(position);
                if (choix.equals("Chrono")) {
                    settingsHelper.setSingleMode(GameMode.TimeLimit);
                } else if (choix.equals("Chrono")) {
                    settingsHelper.setMultiMode(GameMode.ScoreObjective);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                settingsHelper.setMultiMode(GameMode.TimeLimit);
            }
        });
    }

    public void setupInputTexts() {
        EditText timeSingle = findViewById(R.id.tvTempsMinSolo);
        EditText timeMulti = findViewById(R.id.tvTempsMinMulti);

        EditText widthSolo = findViewById(R.id.tvGrilleColonneSolo);
        EditText heightSolo = findViewById(R.id.tvGrilleLigneSolo);

        EditText widthMulti = findViewById(R.id.tvGrilleColonneMulti);
        EditText heightMulti = findViewById(R.id.tvGrilleLigneMulti);

        Log.d("GAME2048", "setupInputTexts: \n" + timeSingle + "\n" + timeMulti + "\n" + widthSolo + "\n" + heightSolo + "\n" + widthMulti + "\n" + heightMulti);

        timeSingle.setText(String.valueOf(settingsHelper.getSingleTimeLimit()));
        timeMulti.setText(String.valueOf(settingsHelper.getMultiTimeLimit()));

        widthSolo.setText(String.valueOf(settingsHelper.getSingleCols()));
        heightSolo.setText(String.valueOf(settingsHelper.getSingleRows()));

        widthMulti.setText(String.valueOf(settingsHelper.getMultiCols()));
        heightMulti.setText(String.valueOf(settingsHelper.getMultiRows()));

        timeSingle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int timeMin = Integer.parseInt(s.toString());
                    settingsHelper.setSingleTimeLimit(timeMin);
                } catch (Exception e) {
                }
            }
        });

        timeMulti.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int timeMin = Integer.parseInt(s.toString());
                    settingsHelper.setMultiTimeLimit(timeMin);
                } catch (Exception e) {
                }
            }
        });

        widthSolo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int width = Integer.parseInt(s.toString());
                    settingsHelper.setSingleGridSize(settingsHelper.getSingleRows(), width);
                } catch (Exception e) {
                }
            }
        });

        heightSolo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int height = Integer.parseInt(s.toString());
                    settingsHelper.setSingleGridSize(height, settingsHelper.getSingleCols());
                } catch (Exception e) {
                }
            }
        });

        widthMulti.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int width = Integer.parseInt(s.toString());
                    settingsHelper.setMultiGridSize(settingsHelper.getMultiRows(), width);
                } catch (Exception e) {
                }
            }
        });

        heightMulti.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int height = Integer.parseInt(s.toString());
                    settingsHelper.setSingleGridSize(height, settingsHelper.getMultiCols());
                } catch (Exception e) {
                }
            }
        });
    }
}

    /*
    // methode pour la gestion de la police
    private void setupPoliceSpinner() {
        spinnerPolice = findViewById(R.id.spinnerPolice);
        String[] polices = {"Roboto","Montserrat","Fredoka","Comic"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, polices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPolice.setAdapter(adapter);
        spinnerPolice.setSelection(settingsHelper.getPoliceIndex());
        spinnerPolice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(first){ first=false; return; }
                settingsHelper.setPoliceIndex(pos);
                refreshFont();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    */