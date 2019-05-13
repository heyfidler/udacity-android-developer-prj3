package com.fidflop.moviemagic.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkHelper {
    private static NetworkHelper instance;
    private RequestQueue requestQueue;

    private static final String API_QUERY_STRING = "?api_key=";
    private static final String VIDEOS = "/videos";
    private static final String REVIEWS = "/reviews";

    private NetworkHelper() {
    }

    public static synchronized NetworkHelper getInstance() {
        if (instance == null) {
            instance = new NetworkHelper();
        }
        return instance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public static String getMovieDBURL(String urlString, String apiKey) {
        return urlString + API_QUERY_STRING + apiKey;
    }

    public static String getVideoDBURL(String urlString, String apiKey, String id) {
        return urlString + id + VIDEOS + API_QUERY_STRING + apiKey;
    }

    public static String getReviewDBURL(String urlString, String apiKey, String id) {
        return urlString + id + REVIEWS + API_QUERY_STRING + apiKey;
    }
}
