package com.sukhjinder.popularmovies;

import android.database.DatabaseUtils;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sukhjinder.popularmovies.data.MovieContract;
import com.sukhjinder.popularmovies.adapter.FavoritesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class Favorites extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.favorites_recycler)
    RecyclerView recyclerView;
    private FavoritesAdapter favAdapter;
    private Cursor mDetailCursor;

    private static final int ID_MOVIES_LOADER = 23;

    private Parcelable listState;
    private static final String LIST_STATE = "list_state";
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE);
        }

        favAdapter = new FavoritesAdapter(mDetailCursor);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(favAdapter);
        DividerItemDecoration favItemDecor = new DividerItemDecoration(this, VERTICAL);
        recyclerView.addItemDecoration(favItemDecor);
        getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, this);

        linearLayoutManager.onRestoreInstanceState(listState);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TITLE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mDetailCursor = data;
        mDetailCursor.moveToFirst();
        DatabaseUtils.dumpCursor(data);
        favAdapter.swapCursor(mDetailCursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mDetailCursor = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, linearLayoutManager.onSaveInstanceState());
    }
}