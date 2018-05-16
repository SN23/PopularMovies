package com.sukhjinder.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sukhjinder.popularmovies.adapter.MovieAdapter;
import com.sukhjinder.popularmovies.api.FetchMovies;
import com.sukhjinder.popularmovies.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    @BindView(R.id.movies_recycler)
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    private MovieAdapter movieAdapter;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private boolean onlineStatus;
    private final static String MOVIES = "movies";
    private ArrayList<Movie> movieList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Context context = this;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MENU_SELECTED)) {
                selected = savedInstanceState.getInt(MENU_SELECTED);
            }
            if (savedInstanceState.containsKey(MOVIES)) {
                movieList = savedInstanceState.getParcelableArrayList(MOVIES);
            }
        }

        onlineStatus = Utils.isOnline(context);
        movieAdapter = new MovieAdapter(movieList, new MovieAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Movie movie) {
                Intent movieDetailsIntent = new Intent(context, MovieDetails.class);
                movieDetailsIntent.putExtra("movieDetails", movie);
                context.startActivity(movieDetailsIntent);
            }
        });

        gridLayoutManager = new GridLayoutManager(this, numberOfColumns());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(movieAdapter);
        recyclerView.getLayoutManager().onSaveInstanceState();
        recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState);


        if (selected == -1) {
            if (movieList.size() > 0) {
                movieList = movieAdapter.getMovies();
            } else {
                apiCall(POPULAR);
            }

        } else if (selected == R.id.popular) {
            if (movieList.size() > 0) {
                movieList = movieAdapter.getMovies();
            } else {
                apiCall(POPULAR);
            }
        } else if (selected == R.id.top_rated) {
            if (movieList.size() > 0) {
                movieList = movieAdapter.getMovies();
            } else {
                apiCall(TOP_RATED);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(MENU_SELECTED, selected);
        savedInstanceState.putParcelableArrayList(MOVIES, movieList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected = savedInstanceState.getInt(MENU_SELECTED);
        movieList = savedInstanceState.getParcelableArrayList(MOVIES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);

        if (selected != -1) {
            MenuItem menuItem = menu.findItem(selected);
            menuItem.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.top_rated:
                selected = id;
                item.setChecked(true);
                apiCall(TOP_RATED);
                return true;

            case R.id.popular:
                selected = id;
                item.setChecked(true);
                apiCall(POPULAR);
                return true;

            case R.id.favorites:
                Intent intent = new Intent(this, Favorites.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apiCall(String movieSortType) {
        onlineStatus = Utils.isOnline(this);
        if (onlineStatus) {
            movieAdapter.clearAll();
            new FetchMovies(movieAdapter).execute(movieSortType);
        } else if (!onlineStatus) {
            Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

}