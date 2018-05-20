package com.sukhjinder.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    private final static String POPULAR_MOVIES = "movies";
    private ArrayList<Movie> popularMovieList = new ArrayList<>();

    private final static String TOP_RATED_MOVIES = "top_rated_movies";
    private ArrayList<Movie> topRatedMovieList = new ArrayList<>();

    private int popularScrollPosition = 0;
    private int topRatedScrollPosition = 0;
    private int popularOffset;
    private int topRatedOffset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Context context = this;
        ArrayList<Movie> movies = new ArrayList<>();

        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt(MENU_SELECTED);
            if (selected == -1 || selected == R.id.popular) {
                popularMovieList = savedInstanceState.getParcelableArrayList(POPULAR_MOVIES);
                movies = popularMovieList;
            } else if (selected == R.id.top_rated) {
                topRatedMovieList = savedInstanceState.getParcelableArrayList(TOP_RATED_MOVIES);
                movies = topRatedMovieList;
            }
        }

        onlineStatus = Utils.isOnline(context);
        movieAdapter = new MovieAdapter(movies, new MovieAdapter.OnItemClickListener() {
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


        if (selected == -1 || selected == R.id.popular) {
            if (popularMovieList.size() > 0) {
                popularMovieList = movieAdapter.getMovies();
            } else {
                apiCall(POPULAR);
            }

        } else if (selected == R.id.top_rated) {
            if (topRatedMovieList.size() > 0) {
                topRatedMovieList = movieAdapter.getMovies();
            } else {
                apiCall(TOP_RATED);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(MENU_SELECTED, selected);
        if (selected == -1 || selected == R.id.popular) {
            savedInstanceState.putParcelableArrayList(POPULAR_MOVIES, popularMovieList);
        } else if (selected == R.id.top_rated) {
            savedInstanceState.putParcelableArrayList(TOP_RATED_MOVIES, topRatedMovieList);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected = savedInstanceState.getInt(MENU_SELECTED);
        if (selected == -1 || selected == R.id.popular) {
            popularMovieList = savedInstanceState.getParcelableArrayList(POPULAR_MOVIES);
        } else if (selected == R.id.top_rated) {
            topRatedMovieList = savedInstanceState.getParcelableArrayList(TOP_RATED_MOVIES);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (selected == -1 || selected == R.id.popular) {
            popularScrollPosition = (gridLayoutManager.findFirstVisibleItemPosition());
            if (popularScrollPosition > -1) {
                View firstItemView = gridLayoutManager.findViewByPosition(popularScrollPosition);
                popularOffset = firstItemView.getTop();
            }
        } else if (selected == R.id.top_rated) {
            topRatedScrollPosition = (gridLayoutManager.findFirstVisibleItemPosition());
            if (topRatedScrollPosition > -1) {
                View firstItemView = gridLayoutManager.findViewByPosition(topRatedScrollPosition);
                topRatedOffset = firstItemView.getTop();
            }
        }
        return super.onPrepareOptionsMenu(menu);
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
                if (topRatedMovieList.size() > 0) {
                    movieAdapter.clearAll();
                    movieAdapter.addAllMovies(topRatedMovieList);
                    movieAdapter.notifyDataSetChanged();

                } else {
                    apiCall(TOP_RATED);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gridLayoutManager.scrollToPositionWithOffset(topRatedScrollPosition, topRatedOffset);
                    }
                }, 200);
                return true;


            case R.id.popular:
                selected = id;
                item.setChecked(true);
                if (popularMovieList.size() > 0) {
                    movieAdapter.clearAll();
                    movieAdapter.addAllMovies(popularMovieList);
                    movieAdapter.notifyDataSetChanged();
                } else {
                    apiCall(POPULAR);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gridLayoutManager.scrollToPositionWithOffset(popularScrollPosition, popularOffset);
                    }
                }, 200);
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