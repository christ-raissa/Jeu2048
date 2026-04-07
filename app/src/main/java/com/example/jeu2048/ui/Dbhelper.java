package com.example.jeu2048.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dbhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jeu2048.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_SCORES = "scores";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAYER_NAME = "player_name";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_COUT = "cout";
    public static final String COLUMN_STATUT = "statut";
    public static final String COLUMN_DUREE = "duree";
    public static final String COLUMN_MAX_TILE = "max_tile";
    public static final String COLUMN_DATE = "date_score";

    private static final String CREATE_TABLE_SCORES =
            "CREATE TABLE " + TABLE_SCORES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLAYER_NAME + " TEXT, " +
                    COLUMN_SCORE + " INTEGER, " +
                    COLUMN_STATUT + " TEXT, " +
                    COLUMN_COUT + " INTEGER, " +
                    COLUMN_DUREE + " INTEGER, " +
                    COLUMN_MAX_TILE + " INTEGER, " +
                    COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    public Dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public void insertScore(String name, long score, long cout, String statut, long maxTile, long duree) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLAYER_NAME, name);
        cv.put(COLUMN_SCORE, score);
        cv.put(COLUMN_COUT, cout);
        cv.put(COLUMN_STATUT, statut);
        cv.put(COLUMN_MAX_TILE, maxTile);
        cv.put(COLUMN_DUREE, duree);

        db.insert(TABLE_SCORES, null, cv);
        db.close();
    }

    // --- SECTION SOLO ---

    public Cursor getTop3Scores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COLUMN_SCORE + ", " + COLUMN_DATE +
                        " FROM " + TABLE_SCORES +
                        " WHERE " + COLUMN_STATUT + " NOT LIKE '%Multi%'" +
                        " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 3",
                null
        );
    }

    public long getBestScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " NOT LIKE '%Multi%'", null);
        if (c.moveToFirst()) {
            long val = c.getLong(0);
            c.close();
            return val;
        }
        return 0;
    }

    public long getTotalScoreSolo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COLUMN_SCORE + ") FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " NOT LIKE '%Multi%'", null);
        if (c.moveToFirst()) {
            long val = c.getLong(0);
            c.close();
            return val;
        }
        return 0;
    }

    // --- SECTION MULTIJOUEUR ---

    public Cursor getTop3Multiplayer() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COLUMN_SCORE + ", " + COLUMN_DATE + ", " + COLUMN_PLAYER_NAME +
                        " FROM " + TABLE_SCORES +
                        " WHERE " + COLUMN_STATUT + " LIKE '%Multi%'" +
                        " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 3",
                null
        );
    }

    public long getBestScoreMulti() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " LIKE '%Multi%'", null);
        long val = 0;
        if (c.moveToFirst()) val = c.getLong(0);
        c.close();
        return val;
    }

    public long getTotalScoreMulti() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COLUMN_SCORE + ") FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " LIKE '%Multi%'", null);
        if (c.moveToFirst()) {
            long val = c.getLong(0);
            c.close();
            return val;
        }
        return 0;
    }
    // NOMBRE DE PARTIES JOUÉES EN MULTIJOUEUR
    public int getGamesPlayedMulti() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " LIKE '%Multi%'", null);
        if (c.moveToFirst()) {
            int val = c.getInt(0);
            c.close();
            return val;
        }
        return 0;
    }

    // --- STATISTIQUES ---

    public long getTotalTimeMilti() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COLUMN_DUREE + ") FROM " + TABLE_SCORES, null);
        if (c.moveToFirst()) {
            long val = c.getLong(0);
            c.close();
            return val;
        }
        return 0;
    }

    public int getGamesPlayedSolo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " NOT LIKE '%Multi%'", null);
        if (c.moveToFirst()) {
            int val = c.getInt(0);
            c.close();
            return val;
        }
        return 0;
    }

    // LE TEMPS TOTAL EN MODE SOLO
    public long getTotalTimeSolo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COLUMN_DUREE + ") FROM " + TABLE_SCORES +
                " WHERE " + COLUMN_STATUT + " NOT LIKE '%Multi%'", null);
        if (c.moveToFirst()) {
            long val = c.getLong(0);
            c.close();
            return val;
        }
        return 0;
    }


}