package com.sukhjinder.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sukhjinder.popularmovies.data.MovieContract;
import com.sukhjinder.popularmovies.R;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context context;
    private static final String BASE_URL = "http://image.tmdb.org/t/p/w342";


    public FavoritesAdapter(Context context, Cursor mCursor) {
        this.mCursor = mCursor;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView fav_movie_poster;
        private TextView fav_movie_title;

        public ViewHolder(View itemView) {
            super(itemView);
            fav_movie_poster = itemView.findViewById(R.id.favorite_movie_poster);
            fav_movie_title = itemView.findViewById(R.id.favorite_movie_title);
        }

        public void bind(final int position) {
            if (!mCursor.moveToPosition(position))
                return;

            int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            String title = mCursor.getString(titleIndex);
            int posterIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            String poster_path = mCursor.getString(posterIndex);

            fav_movie_title.setText(title);
            fav_movie_poster.setAdjustViewBounds(true);
            Picasso.get()
                    .load(BASE_URL + poster_path)
                    .into(fav_movie_poster);
        }
    }
}
