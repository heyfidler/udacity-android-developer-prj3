package com.fidflop.moviemagic.util;

import android.util.Log;

import com.fidflop.moviemagic.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONUtility {

    private static final String ID = "id";
    private static final String RESULTS = "results";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";


    public static List<Movie> parseJSON(JSONObject json) {
        List<Movie> movies = new ArrayList<>();

        if (json == null) {
            return movies;
        }

        try {
            JSONArray jsonResults = json.getJSONArray(RESULTS);

            if (jsonResults == null) {
                return movies;
            }

            for (int i = 0; i < jsonResults.length(); i++) {
                Movie movie = new Movie();
                movie.setId(jsonResults.getJSONObject(i).getInt(ID));
                Log.d(JSONUtility.class.getSimpleName(), "MOVIE ID: " + jsonResults.getJSONObject(i).getInt(ID));
                movie.setTitle(jsonResults.getJSONObject(i).getString(TITLE));
                movie.setPosterURL(jsonResults.getJSONObject(i).getString(POSTER_PATH));
                movie.setVoteAverage(jsonResults.getJSONObject(i).getString(VOTE_AVERAGE));
                movie.setOverView(jsonResults.getJSONObject(i).getString(OVERVIEW));
                movie.setReleaseDate(jsonResults.getJSONObject(i).getString(RELEASE_DATE));

                movies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
