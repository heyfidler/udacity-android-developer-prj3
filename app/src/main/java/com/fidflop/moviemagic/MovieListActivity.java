package com.fidflop.moviemagic;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fidflop.moviemagic.data.Movie;
import com.fidflop.moviemagic.data.MovieViewModel;
import com.fidflop.moviemagic.util.JSONUtility;
import com.fidflop.moviemagic.util.NetworkHelper;
import org.json.JSONObject;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private static final int NUMBER_OF_COLUMNS = 2;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String LIST_STATE_KEY = "LIST_STATE_KEY";
    private List<Movie> favoriteMovies;
    private boolean isFavoritesView = false;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_list);

        recyclerView = findViewById(R.id.grid);
        gridLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        recyclerView.setLayoutManager(gridLayoutManager);

        setupViewModel();
        sortByPopularity();
    }

    private void sortByPopularity() {
        isFavoritesView = false;
        String url = NetworkHelper.getMovieDBURL(
                BuildConfig.MOVIE_DB_BASE_URL + POPULAR,
                BuildConfig.MOVIE_DB_API_KEY);
        updateGrid(url);
    }

    private void sortByRating() {
        isFavoritesView = false;
        String url = NetworkHelper.getMovieDBURL(
                BuildConfig.MOVIE_DB_BASE_URL + TOP_RATED,
                BuildConfig.MOVIE_DB_API_KEY);
        updateGrid(url);
    }

    private void sortByFavorites() {
        isFavoritesView = true;
        updateGrid(favoriteMovies);
    }

    private void setupViewModel() {
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                Log.d(this.getClass().getSimpleName(), "Updating list of movies from LiveData in ViewModel");
                favoriteMovies = movies;
                if (isFavoritesView) {
                    updateGrid(favoriteMovies);
                }
            }
        });
    }

    private void updateGrid(String url) {
        final MovieListActivity activity = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final List<Movie> movies = JSONUtility.parseMovies(response);

                        activity.updateGrid(movies);
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

    private void updateGrid(final List<Movie> movies) {
        MovieGridAdapter adapter = new MovieGridAdapter(this, movies);

        adapter.setClickListener(new MovieGridAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class).putExtra("movie",movies.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sortByPopularity) {
            sortByPopularity();
            return true;
        } else if ( id == R.id.sortByRating) {
            sortByRating();
            return true;
        } else if ( id == R.id.sortByFavorites) {
            sortByFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        if (gridLayoutManager != null) {
            Parcelable mListState = gridLayoutManager.onSaveInstanceState();
            state.putParcelable(LIST_STATE_KEY, mListState);
        }
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        if (state != null && gridLayoutManager != null) {
            gridLayoutManager.onRestoreInstanceState(state.getParcelable(LIST_STATE_KEY));
        }
    }
}
