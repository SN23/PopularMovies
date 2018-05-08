package com.sukhjinder.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
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


public class MovieDetails extends AppCompatActivity {

    private Movie movie;
    private static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/w185/";
    private static String Base_URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;
    private ArrayList<Trailer> trailers;
    private RecyclerView reviewRecyclerView;
    private ImageButton favoriteBtn;

    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviews;
    private boolean onlineStatus;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        final Context context = this;
        ImageView moviePoster = findViewById(R.id.movie_details_poster);
        TextView movieTitle = findViewById(R.id.movie_title);
        TextView movieYears = findViewById(R.id.movie_year);
        TextView movieRating = findViewById(R.id.movie_rating);
        TextView moviePlotSynopsis = findViewById(R.id.movie_plot_synopsis);
        trailerRecyclerView = findViewById(R.id.trailer_recycler);
        reviewRecyclerView = findViewById(R.id.review_recycler);

        Utils utils = new Utils();
        onlineStatus = utils.isOnline(context);

        movie = getIntent().getParcelableExtra("movieDetails");
        String movieID = String.valueOf(movie.getId());
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();

        trailerAdapter = new TrailerAdapter(trailers, new TrailerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Trailer trailer) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Base_URL_YOUTUBE + trailer.getKey())));
            }
        });
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
        trailerRecyclerView.setAdapter(trailerAdapter);
        DividerItemDecoration trailerItemDecor = new DividerItemDecoration(context, HORIZONTAL);
        trailerRecyclerView.addItemDecoration(trailerItemDecor);

        reviewAdapter = new ReviewAdapter(reviews);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        reviewRecyclerView.setAdapter(reviewAdapter);
        DividerItemDecoration reviewItemDecor = new DividerItemDecoration(context, VERTICAL);
        reviewRecyclerView.addItemDecoration(reviewItemDecor);

        trailerApiCall(movieID);
        reviewApiCall(movieID);


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

    private void trailerApiCall(String movieID) {
        if (onlineStatus) {
            trailerAdapter.clearAll();
            new FetchTrailers(trailerAdapter).execute(movieID);
        } else if (onlineStatus) {
            Toast.makeText(MovieDetails.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    private void reviewApiCall(String movieID) {
        if (onlineStatus) {
            reviewAdapter.clearAll();
            new FetchReviews(reviewAdapter).execute(movieID);
        } else if (onlineStatus) {
            Toast.makeText(MovieDetails.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

}
