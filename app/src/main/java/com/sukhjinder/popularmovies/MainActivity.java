package com.sukhjinder.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movies;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movies = new ArrayList<>();
        final Context context = getApplicationContext();

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

        apiCall(POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.top_rated:
                item.setChecked(true);
                apiCall(TOP_RATED);
                return true;

            case R.id.popular:
                item.setChecked(true);
                apiCall(POPULAR);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apiCall(String movieSortType) {
        boolean onlineStatus = isOnline();
        if (onlineStatus) {
            movieAdapter.clearAll();
            new FetchMovies(movieAdapter).execute(movieSortType);
        } else if (!onlineStatus) {
            Toast.makeText(MainActivity.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    // Network Connectivity Code from Stackoverflow
// https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}