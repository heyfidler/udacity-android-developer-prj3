package com.fidflop.moviemagic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fidflop.moviemagic.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private final LayoutInflater layoutinflater;
    private final List<Movie> movies;
    private final Context context;
    private ItemClickListener clickListener;

    MovieGridAdapter(Context context, List<Movie> movieListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        movies = movieListView;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutinflater.inflate(R.layout.movie, parent, false);
        return new ViewHolder(view);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageURL = BuildConfig.MOVIE_DB_BASE_IMAGE_URL
                + BuildConfig.MOVIE_DB_IMAGE_SIZE
                + movies.get(position).getPosterURL();

        Picasso.get()
                .load(imageURL)
                .into(holder.imageView);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}

