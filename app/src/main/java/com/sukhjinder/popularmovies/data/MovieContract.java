package com.sukhjinder.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.sukhjinder.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final String MOVIES = "movies";

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(MOVIES)
                .build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIES;

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
