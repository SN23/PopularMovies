package com.sukhjinder.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.sukhjinder.popularmovies.adapter.MovieAdapter;
import com.sukhjinder.popularmovies.api.FetchMovies;
import com.sukhjinder.popularmovies.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String MENU_SELECTED = "selected";
    private int selected = -1;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movies;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private boolean onlineStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt(MENU_SELECTED);
        }

        final Context context = this;
        Utils utils = new Utils();

        onlineStatus = utils.isOnline(context);
        movies = new ArrayList<>();

        recyclerView = findViewById(R.id.movies_recycler);
        movieAdapter = new MovieAdapter(movies, new MovieAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Movie movie) {
                Intent movieDetailsIntent = new Intent(context, MovieDetails.class);
                movieDetailsIntent.putExtra("movieDetails", movie);
                context.startActivity(movieDetailsIntent);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(movieAdapter);

        if (selected == -1) {
            apiCall(POPULAR);
        }

        if (selected != -1 && selected == R.id.popular) {
            apiCall(POPULAR);
        } else if (selected != -1 && selected == R.id.top_rated) {
            apiCall(TOP_RATED);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(MENU_SELECTED, selected);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected = savedInstanceState.getInt(MENU_SELECTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);

        if (selected != -1) {
            MenuItem menuItem = (MenuItem) menu.findItem(selected);
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
                selected = id;
                item.setChecked(true);
                Intent intent = new Intent(this, Favorites.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apiCall(String movieSortType) {
        if (onlineStatus) {
            movieAdapter.clearAll();
            new FetchMovies(movieAdapter).execute(movieSortType);
        } else if (!onlineStatus) {
            Toast.makeText(MainActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

}