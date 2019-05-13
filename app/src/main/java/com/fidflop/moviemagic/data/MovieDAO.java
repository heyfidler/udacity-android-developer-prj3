package com.fidflop.moviemagic.data;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> loadAllMovies();

    @Insert
    void insertMovie(Movie taskEntry);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * from movie where id=:id")
    Movie getMovie(int id);
}
