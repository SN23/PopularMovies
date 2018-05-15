package com.sukhjinder.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sukhjinder.popularmovies.R;
import com.sukhjinder.popularmovies.model.Trailer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {


    private ArrayList<Trailer> trailers;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void OnItemClick(Trailer trailer);
    }

    public TrailerAdapter(ArrayList<Trailer> trailers, OnItemClickListener listener) {
        this.trailers = trailers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.ViewHolder holder, int position) {
        holder.bind(trailers.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void addTrailer(Trailer trailer) {
        trailers.add(trailer);
    }

    public void clearAll() {
        trailers.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_thumbnail)
        ImageView trailer_thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Trailer trailer, final OnItemClickListener listener) {
            trailer_thumbnail.setAdjustViewBounds(true);
            String BASE_URL_YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/";
            Picasso.get()
                    .load(BASE_URL_YOUTUBE_THUMBNAIL + trailer.getKey() + "/0.jpg")
                    .into(trailer_thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(trailer);
                }
            });
        }
    }
}
