package com.sukhjinder.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDbHelper dbHelper;

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_ID = 200;


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.MovieEntry.MOVIES, MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.MOVIES + "/#", MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID: {
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                return retCursor;
            }
            case MOVIE_WITH_ID: {
                retCursor = dbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }


            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);
                } else {
                    return null;
                }
                db.close();
                break;

            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsDeleted = 0;

        switch (match) {
            case MOVIE:
                numRowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;


            case MOVIE_WITH_ID:
                numRowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        db.close();
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
