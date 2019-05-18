package com.fidflop.moviemagic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fidflop.moviemagic.data.AppDatabase;
import com.fidflop.moviemagic.data.Movie;
import com.fidflop.moviemagic.data.Review;
import com.fidflop.moviemagic.data.Video;
import com.fidflop.moviemagic.databinding.MovieDetailBinding;
import com.fidflop.moviemagic.util.JSONUtility;
import com.fidflop.moviemagic.util.NetworkHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;
    private MovieDetailBinding binding;
    private final List<View> videoAndReviewChain = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setContentView(R.layout.movie_detail);

        this.binding = DataBindingUtil.setContentView(this, R.layout.movie_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.movie = getIntent().getExtras().getParcelable("movie");
            setupUI(movie);
        }

        getVideos(movie);
    }

    private void setupUI(Movie movie) {
        // enable back button on action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (movie == null) {
            return;
        }

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

    @SuppressLint("ClickableViewAccessibility")
    private void updateReviews(List<Review> reviews) {
        int i = 1;
        for (final Review review:reviews) {
            TextView view = new TextView(this);
            view.setId(View.generateViewId());

            view.setText(getString(R.string.review, Integer.toString(i)));

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
                    startActivity(browserIntent);
                    return false;
                }
            });
            videoAndReviewChain.add(view);
            ++i;
        }

    }

    private void addVideoAndReviewChain() {
        if (videoAndReviewChain.isEmpty()) {
            return;
        } else if (1 == videoAndReviewChain.size()) {
            View v = new View(this);
            v.setId(View.generateViewId());
            videoAndReviewChain.add(v);
        }

        int[] chain = new  int [videoAndReviewChain.size()];
        int i = 0;
        for (final View view:videoAndReviewChain) {
            binding.detailConstraintLayout.addView(view);
            chain[i] = view.getId();
            ++i;
        }

        ConstraintSet set = new ConstraintSet();
        set.clone(binding.detailConstraintLayout);
        set.createVerticalChain(
                R.id.movie_overview,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,chain,
                null,
                ConstraintSet.CHAIN_PACKED);

        set.applyTo(binding.detailConstraintLayout);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void updateVideos(List<Video> videos) {
        int i = 1;
        for (final Video video:videos) {
            TextView view = new TextView(this);
            view.setId(View.generateViewId());

            view.setText(getString(R.string.trailer, Integer.toString(i)));

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(BuildConfig.MOVIE_TRAILER_BASE_URL+video.getKey()));
                    startActivity(browserIntent);
                    return false;
                }
            });
            videoAndReviewChain.add(view);
            ++i;
        }
    }

    private void getVideos(final Movie movie) {
        final MovieDetailActivity activity = this;
        String url = NetworkHelper.getVideoDBURL(
                BuildConfig.MOVIE_DB_BASE_URL,
                BuildConfig.MOVIE_DB_API_KEY,
                Integer.toString(movie.getId()));

        Log.d(this.getClass().getSimpleName(), "Video url: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.updateVideos(JSONUtility.parseVideos(response));
                        getReviews(movie);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        NetworkHelper.getInstance().getRequestQueue(this.getApplicationContext()).add(jsonObjectRequest);
    }

    private void getReviews(Movie movie) {
        final MovieDetailActivity activity = this;
        String url = NetworkHelper.getReviewDBURL(
                BuildConfig.MOVIE_DB_BASE_URL,
                BuildConfig.MOVIE_DB_API_KEY,
                Integer.toString(movie.getId()));

        Log.d(this.getClass().getSimpleName(), "Review url: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.updateReviews(JSONUtility.parseReviews(response));
                        addVideoAndReviewChain();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        NetworkHelper.getInstance().getRequestQueue(this.getApplicationContext()).add(jsonObjectRequest);
    }
}