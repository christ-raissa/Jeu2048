package com.example.jeu2048.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.jeu2048.R;
import com.example.jeu2048.settings.FontActivity;
import java.util.ArrayList;

public class PartageActivity extends FontActivity {

    private ListView listParties;
    private Button btnPartager;
    private Dbhelper dba;
    private ArrayList<String> scoresDisplay;
    private ArrayList<Integer> idsList;
    private int selectedGameId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partage);

        dba = new Dbhelper(this);
        listParties = findViewById(R.id.listParties);
        btnPartager = findViewById(R.id.btnPartager);
        scoresDisplay = new ArrayList<>();
        idsList = new ArrayList<>();

        loadParties();

        // Sélection d'un item dans la liste
        listParties.setOnItemClickListener((parent, view, position, id) -> {
            selectedGameId = idsList.get(position);
        });

        // Clic sur le bouton Partager en bas
        btnPartager.setOnClickListener(v -> {
            if (selectedGameId != -1) {
                openConfirmationPopup(selectedGameId);
            } else {
                Toast.makeText(this, "Sélectionnez une partie dans la liste", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadParties() {
        SQLiteDatabase db = dba.getReadableDatabase();
        // On récupère Score et Date pour l'affichage
        Cursor cursor = db.rawQuery("SELECT * FROM " + Dbhelper.TABLE_SCORES +
                " WHERE " + Dbhelper.COLUMN_STATUT + " NOT LIKE '%Multi%'" +
                " ORDER BY " + Dbhelper.COLUMN_DATE + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                long score = cursor.getLong(cursor.getColumnIndexOrThrow(Dbhelper.COLUMN_SCORE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Dbhelper.COLUMN_DATE));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Dbhelper.COLUMN_ID));

                scoresDisplay.add("Score : " + score + "\nDate : " + date);
                idsList.add(id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Utilisation d'un layout standard qui supporte le Dark/Light mode
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, scoresDisplay);
        listParties.setAdapter(adapter);
    }

    private void openConfirmationPopup(int gameId) {
        SQLiteDatabase db = dba.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Dbhelper.TABLE_SCORES + " WHERE " + Dbhelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(gameId)});

        if (c.moveToFirst()) {
            long score = c.getLong(c.getColumnIndexOrThrow(Dbhelper.COLUMN_SCORE));
            long moves = c.getLong(c.getColumnIndexOrThrow(Dbhelper.COLUMN_COUT));
            long duree = c.getLong(c.getColumnIndexOrThrow(Dbhelper.COLUMN_DUREE));

            // Création du Popup de confirmation
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View v = getLayoutInflater().inflate(R.layout.dialog_confirm_share, null);
            builder.setView(v);
            AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            TextView tvDetails = v.findViewById(R.id.tvDetailsGame);

            // Formatage durée
            long sec = duree / 1000;
            String dureeStr = (sec < 60) ? sec + "s" : (sec/60) + "m " + (sec%60) + "s";

            String infos = "Score : " + score + "\nCoups : " + moves + "\nDurée : " + dureeStr;
            tvDetails.setText(infos);

            v.findViewById(R.id.btnCancelShare).setOnClickListener(view -> dialog.dismiss());

            v.findViewById(R.id.btnConfirmActionShare).setOnClickListener(view -> {
                dialog.dismiss();
                shareContent(score, moves);
            });

            dialog.show();
        }
        c.close();
    }

    private void shareContent(long score, long moves) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String message = "Regarde mon score sur TO 2048 !\n" +
                "Score : " + score + "\n" +
                "Mouvements : " + moves + "\n" +
                "Peux-tu faire mieux ?";
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Partager via"));
    }
}