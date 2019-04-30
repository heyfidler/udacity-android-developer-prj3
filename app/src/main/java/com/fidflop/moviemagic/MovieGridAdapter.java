package com.fidflop.moviemagic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fidflop.moviemagic.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

class MovieGridAdapter extends BaseAdapter {

    private final LayoutInflater layoutinflater;
    private final List<Movie> movies;
    private final Context context;

    MovieGridAdapter(Context context, List<Movie> movieListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        movies = movieListView;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null){
            convertView = layoutinflater.inflate(R.layout.movie, parent, false);
            imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(imageView);
        } else {
            imageView = (ImageView) convertView.getTag();
        }

        String imageURL = context.getString(R.string.movie_db_base_image_url)
                + context.getString(R.string.movie_db_image_size)
                + movies.get(position).getPosterURL();

        //Log.d(this.getClass().getName(), movies.get(position).getTitle());
        //Log.d(this.getClass().getName(), imageURL);

        Picasso.get()
                .load(imageURL)
                .into(imageView);

        return convertView;
    }
}

