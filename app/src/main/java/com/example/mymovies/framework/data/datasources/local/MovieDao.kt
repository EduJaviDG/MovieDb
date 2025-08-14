package com.example.mymovies.framework.data.datasources.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mymovies.data.datasources.local.MovieDb

@Dao
interface MovieDao {
@Query("SELECT * FROM moviedb")
suspend fun getAll(): List<MovieDb>

@Query("SELECT * FROM moviedb WHERE id like :id")
suspend fun findById(id: Int): MovieDb

@Query("SELECT COUNT(*) FROM moviedb")
suspend fun countMovies(): Int
@Query("SELECT COUNT(*) FROM moviedb")
suspend fun size(): Int

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertMovie(vararg movie: MovieDb)

@Insert
suspend fun insertAll(movies: List<MovieDb>)

@Update
suspend fun updateMovie(vararg movie: MovieDb)

@Delete
suspend fun delete(movie: MovieDb)

}