package com.fidflop.moviemagic;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.lang.ref.WeakReference;

import com.fidflop.moviemagic.data.Movie;
import com.fidflop.moviemagic.util.JSONUtility;
import com.fidflop.moviemagic.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        sortByPopularity();
    }

    private void sortByPopularity() {
        URL url = NetworkUtils.getMovieDBURL(
                getString(R.string.movie_db_popular_url),
                getString(R.string.movie_db_api_key)
        );
        new movieDBQueryTask(this).execute(url);
    }

    private void sortByRating() {
        URL url = NetworkUtils.getMovieDBURL(
                getString(R.string.movie_db_top_rated_url),
                getString(R.string.movie_db_api_key)
        );
        new movieDBQueryTask(this).execute(url);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private static class movieDBQueryTask extends AsyncTask<URL, Void, String> {
        private final WeakReference<MovieListActivity> activityReference;

        // only retain a weak reference to the activity
        movieDBQueryTask(MovieListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String results = null;
            try {
                results = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String results) {
            final MovieListActivity activity = activityReference.get();
            GridView gridview = activity.findViewById(R.id.grid);

            final List<Movie> movies = JSONUtility.parseJSON(results);
            MovieGridAdapter movieGridAdapter = new MovieGridAdapter(activity, movies);
            gridview.setAdapter(movieGridAdapter);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity, MovieDetailActivity.class).putExtra("movie",movies.get(position));
                    activity.startActivity(intent);
                }
            });
        }
    }
}
