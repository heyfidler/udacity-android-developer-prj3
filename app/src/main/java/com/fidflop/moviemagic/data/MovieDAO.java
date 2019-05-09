package com.fidflop.moviemagic.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie")
    List<Movie> loadAllMovies();

    @Insert
    void insertMovie(Movie taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * from movie where id=:id")
    Movie getMovie(int id);
}
