package com.sukhjinder.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sukhjinder.popularmovies.data.MovieContract;
import com.sukhjinder.popularmovies.data.MovieProvider;
import com.sukhjinder.popularmovies.adapter.ReviewAdapter;
import com.sukhjinder.popularmovies.adapter.TrailerAdapter;
import com.sukhjinder.popularmovies.api.FetchReviews;
import com.sukhjinder.popularmovies.api.FetchTrailers;
import com.sukhjinder.popularmovies.model.Movie;
import com.sukhjinder.popularmovies.model.Review;
import com.sukhjinder.popularmovies.model.Trailer;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;
import static android.support.v7.widget.RecyclerView.VERTICAL;


public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Movie movie;
    private String movieID;
    private static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/w342/";
    private static final String BASE_URL_BACKDROP = "http://image.tmdb.org/t/p/w1280/";
    private static String Base_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;
    private ArrayList<Trailer> trailers;

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviews;

    private FloatingActionButton favoriteBtn;
    private boolean onlineStatus;

    MovieProvider movieProvider;
    private static final int CURSOR_LOADER_ID = 23;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        movie = getIntent().getParcelableExtra("movieDetails");
        movieID = String.valueOf(movie.getId());

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        movieProvider = new MovieProvider();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(movie.getTitle());

        final Context context = this;
        ImageView moviePoster = findViewById(R.id.movie_details_poster);
        ImageView movieBackdrop = findViewById(R.id.backdrop);
        TextView movieYears = findViewById(R.id.movie_year);
        TextView movieRating = findViewById(R.id.movie_rating);
        TextView moviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);
        trailerRecyclerView = findViewById(R.id.trailer_recycler);
        reviewRecyclerView = findViewById(R.id.review_recycler);
        favoriteBtn = findViewById(R.id.favoriteBtn);

        Utils utils = new Utils();
        onlineStatus = utils.isOnline(context);


        trailers = new ArrayList<>();
        reviews = new ArrayList<>();

        trailerAdapter = new TrailerAdapter(trailers, new TrailerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Trailer trailer) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Base_URL_YOUTUBE + trailer.getKey())));
            }
        });
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        trailerRecyclerView.setAdapter(trailerAdapter);
        DividerItemDecoration trailerItemDecor = new DividerItemDecoration(context, HORIZONTAL);
        trailerRecyclerView.addItemDecoration(trailerItemDecor);

        reviewAdapter = new ReviewAdapter(reviews);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        reviewRecyclerView.setAdapter(reviewAdapter);
        DividerItemDecoration reviewItemDecor = new DividerItemDecoration(context, VERTICAL);
        reviewRecyclerView.addItemDecoration(reviewItemDecor);

        if (onlineStatus) {
            trailerApiCall(movieID);
            reviewApiCall(movieID);
        } else {
            Toast.makeText(MovieDetails.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }


        Picasso.get()
                .load(BASE_URL_POSTER + movie.getPoster())
                .into(moviePoster);

        Picasso.get()
                .load(BASE_URL_BACKDROP + movie.getBackdrop())
                .fit()
                .centerCrop()
                .into(movieBackdrop);

        movieYears.setText(movie.getReleaseDate().substring(0, 4));
        movieRating.setText(Double.toString(movie.getUserRating()) + "/10");
        moviePlotSynopsis.setText(movie.getPlotSynopsis());

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addToFavorites(movie.getId(), movie.getTitle(), movie.getPoster()) == null) {
                    Toast.makeText(context, "Already in Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void trailerApiCall(String movieID) {
        trailerAdapter.clearAll();
        new FetchTrailers(trailerAdapter).execute(movieID);

    }

    private void reviewApiCall(String movieID) {
        reviewAdapter.clearAll();
        new FetchReviews(reviewAdapter).execute(movieID);

    }

    private Uri addToFavorites(int id, String title, String poster) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ID, id);
        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER, poster);
        return getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
