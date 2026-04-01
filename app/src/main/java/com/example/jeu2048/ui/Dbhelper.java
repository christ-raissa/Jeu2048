package com.example.jeu2048.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dbhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jeu2048.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAYER_NAME = "player_name";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_COUT = "Cout_grille";
    public static final String COLUMN_STATUT = "Statut";
    public static final String COLUMN_DATE = "date_score";

    private static final String CREATE_TABLE_SCORES = "CREATE TABLE " + TABLE_SCORES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PLAYER_NAME + " TEXT, " +
            COLUMN_SCORE + " INTEGER, " +
            COLUMN_STATUT + " INTEGER, " +
            COLUMN_COUT + " INTEGER, " +
            COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public Dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si on change de version, on supprime et on recrée
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public void insertScore(String name, long score, long cout, String statut){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PLAYER_NAME, name);
        cv.put(COLUMN_SCORE, score);
        cv.put(COLUMN_COUT,cout);
        cv.put(COLUMN_STATUT, statut);


        db.insert(TABLE_SCORES, null, cv);
        db.close();

    }
}