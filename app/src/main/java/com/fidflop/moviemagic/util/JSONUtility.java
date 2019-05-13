package com.fidflop.moviemagic.util;

import com.fidflop.moviemagic.data.Movie;
import com.fidflop.moviemagic.data.Review;
import com.fidflop.moviemagic.data.Video;

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
    private static final String KEY = "key";
    private static final String TYPE = "type";
    private static final String SITE = "site";
    private static final String TRAILER = "Trailer";
    private static final String YOUTUBE = "YouTube";
    private static final String URL = "url";

    public static List<Movie> parseMovies(JSONObject json) {
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

    public static List<Video> parseVideos(JSONObject json) {
        List<Video> videos = new ArrayList<>();

        if (json == null) {
            return videos;
        }

        try {
            JSONArray jsonResults = json.getJSONArray(RESULTS);

            if (jsonResults == null) {
                return videos;
            }

            for (int i = 0; i < jsonResults.length(); i++) {
                Video video = new Video();
                video.setKey(jsonResults.getJSONObject(i).getString(KEY));

                // only get trailers from youtube
                if (TRAILER.equalsIgnoreCase(jsonResults.getJSONObject(i).getString(TYPE))
                    && YOUTUBE.equalsIgnoreCase(jsonResults.getJSONObject(i).getString(SITE))) {
                    videos.add(video);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videos;
    }

    public static List<Review> parseReviews(JSONObject json) {
        List<Review> reviews = new ArrayList<>();

        if (json == null) {
            return reviews;
        }

        try {
            JSONArray jsonResults = json.getJSONArray(RESULTS);

            if (jsonResults == null) {
                return reviews;
            }

            for (int i = 0; i < jsonResults.length(); i++) {
                Review review = new Review();
                review.setUrl(jsonResults.getJSONObject(i).getString(URL));

                reviews.add(review);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
    }
}
