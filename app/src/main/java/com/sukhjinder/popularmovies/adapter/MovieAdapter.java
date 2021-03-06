package com.sukhjinder.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sukhjinder.popularmovies.R;
import com.sukhjinder.popularmovies.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;
    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185";
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void OnItemClick(Movie movie);
    }

    public MovieAdapter(ArrayList<Movie> movies, OnItemClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bind(movies.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void clearAll() {
        movies.clear();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void addAllMovies(ArrayList<Movie> movieList) {
        movies.addAll(movieList);
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_poster)
        ImageView movie_poster;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Movie movie, final OnItemClickListener listener) {
            Picasso.get()
                    .load(BASE_URL + movie.getPoster())
                    .into(movie_poster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(movie);
                }
            });
        }
    }

}