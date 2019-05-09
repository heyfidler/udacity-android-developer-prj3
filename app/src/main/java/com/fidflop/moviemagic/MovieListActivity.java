package com.fidflop.moviemagic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fidflop.moviemagic.data.AppDatabase;
import com.fidflop.moviemagic.data.Movie;
import com.fidflop.moviemagic.util.JSONUtility;
import com.fidflop.moviemagic.util.NetworkHelper;
import org.json.JSONObject;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private final int NUMBER_OF_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        sortByPopularity();
    }

    private void sortByPopularity() {
        String url = NetworkHelper.getMovieDBURL(
                BuildConfig.MOVIE_DB_POPULAR_URL,
                BuildConfig.MOVIE_DB_API_KEY);
        updateGrid(url);
    }

    private void sortByRating() {
        String url = NetworkHelper.getMovieDBURL(
                BuildConfig.MOVIE_DB_TOP_RATED_URL,
                BuildConfig.MOVIE_DB_API_KEY);
        updateGrid(url);
    }

    private void sortByFavorites() {
        updateGrid(AppDatabase.getInstance(this).movieDao().loadAllMovies());
    }

    private void updateGrid(String url) {
        final RecyclerView recyclerView = findViewById(R.id.grid);
        final Activity activity = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final List<Movie> movies = JSONUtility.parseJSON(response);

                        ((MovieListActivity) activity).updateGrid(movies);
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
        RecyclerView recyclerView = findViewById(R.id.grid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));
        MovieGridAdapter adapter = new MovieGridAdapter(this, movies);

        adapter.setClickListener(new MovieGridAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class).putExtra("movie",movies.get(position));
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
        } else if ( id == R.id.sortByFavories) {
            sortByFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
