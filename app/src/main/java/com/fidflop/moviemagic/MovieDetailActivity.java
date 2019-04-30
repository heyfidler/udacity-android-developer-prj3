package com.fidflop.moviemagic;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fidflop.moviemagic.data.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.movie_detail);

        // enable back button on action bar
        ActionBar actionBar =getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Movie movie = getIntent().getExtras().getParcelable("movie");

            if (movie == null) {
                return;
            }

            ImageView imageView = findViewById(R.id.poster_img);
            String imageURL = getString(R.string.movie_db_base_image_url)
                    + getString(R.string.movie_db_image_size)
                    + movie.getPosterURL();

            Picasso.get()
                    .load(imageURL)
                    .into(imageView);

            ((TextView) findViewById(R.id.movie_release_date)).setText(getString(R.string.release_date, movie.getReleaseDate()));
            ((TextView) findViewById(R.id.movie_vote)).setText(getString(R.string.vote_average, movie.getVoteAverage()));
            ((TextView) findViewById(R.id.detail_title_tv)).setText(movie.getTitle());
            ((TextView) findViewById(R.id.movie_overview)).setText(movie.getOverView());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
