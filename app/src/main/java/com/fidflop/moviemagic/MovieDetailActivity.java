package com.fidflop.moviemagic;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fidflop.moviemagic.data.AppDatabase;
import com.fidflop.moviemagic.data.Movie;
import com.fidflop.moviemagic.databinding.MovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.movie_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.movie = getIntent().getExtras().getParcelable("movie");
            setupUI(movie);
        }

        Log.d(this.getClass().getSimpleName(), "Current movie: " + movie.getTitle() + "  " + movie.getId());
        List<Movie> movies = AppDatabase.getInstance(this).movieDao().loadAllMovies();
        for (Movie movie:movies) {
            Log.d(this.getClass().getSimpleName(), "MOVIE from DB: " + movie.getTitle());
        }
    }

    private void setupUI(Movie movie) {
        MovieDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.movie_detail);

        // enable back button on action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (movie == null) {
            return;
        }

        ImageView imageView = findViewById(R.id.poster_img);
        String imageURL = BuildConfig.MOVIE_DB_BASE_IMAGE_URL
                + BuildConfig.MOVIE_DB_IMAGE_SIZE
                + movie.getPosterURL();

        Picasso.get()
                .load(imageURL)
                .into(binding.posterImg);

        binding.setMovie(movie);
        binding.movieReleaseDate.setText(getString(R.string.release_date, movie.getReleaseDate()));
        binding.movieVote.setText(getString(R.string.vote_average, movie.getVoteAverage()));

        // is movie already a favorite?
        Movie dbMovie = AppDatabase.getInstance(this).movieDao().getMovie(movie.getId());
        if (dbMovie != null) {
            binding.favoriteCheckBox.setChecked(true);
        } else {
            binding.favoriteCheckBox.setChecked(false);
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void favoriteClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            AppDatabase.getInstance(this).movieDao().insertMovie(movie);
        } else {
            AppDatabase.getInstance(this).movieDao().deleteMovie(movie);
        }
    }
}