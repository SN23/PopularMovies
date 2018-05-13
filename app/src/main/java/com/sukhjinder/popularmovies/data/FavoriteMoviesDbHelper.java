package com.sukhjinder.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAV_MOVIES_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
