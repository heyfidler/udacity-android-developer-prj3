package com.fidflop.moviemagic.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private final LiveData<List<Movie>> movies;

    public MovieViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(this.getClass().getSimpleName(), "Actively retrieving the movies from the DataBase");
        movies = database.movieDao().loadAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
