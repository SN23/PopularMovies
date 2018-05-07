package com.sukhjinder.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sukhjinder.popularmovies.model.Movie;


public class MovieDetails extends AppCompatActivity {

    private Movie movie;
    private static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        ImageView moviePoster = findViewById(R.id.movie_details_poster);
        TextView movieTitle = findViewById(R.id.movie_title);
        TextView movieYears = findViewById(R.id.movie_year);
        TextView movieRating = findViewById(R.id.movie_rating);
        TextView moviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);

        movie = getIntent().getParcelableExtra("movieDetails");

        Picasso.with(MovieDetails.this)
                .load(BASE_URL_POSTER + movie.getPoster())
                .fit()
                .centerCrop()
                .into(moviePoster);

        movieTitle.setText(movie.getTitle());
        movieYears.setText(movie.getReleaseDate().substring(0, 4));

        movieRating.setText(Double.toString(movie.getUserRating()) + "/10");
        moviePlotSynopsis.setText(movie.getPlotSynopsis());
    }
}