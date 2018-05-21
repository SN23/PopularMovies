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
        StringBuilder stringBuilder = new StringBuilder();

        final String SQL_CREATE_FAV_MOVIES_TABLE =
                stringBuilder
                        .append("CREATE TABLE ")
                        .append(MovieContract.MovieEntry.TABLE_NAME)
                        .append(" (")
                        .append(MovieContract.MovieEntry.COLUMN_ID)
                        .append(" INTEGER PRIMARY KEY, ")
                        .append(MovieContract.MovieEntry.COLUMN_TITLE)
                        .append(" TEXT NOT NULL, ")
                        .append(MovieContract.MovieEntry.COLUMN_POSTER)
                        .append(" TEXT NOT NULL);").toString();

        sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
