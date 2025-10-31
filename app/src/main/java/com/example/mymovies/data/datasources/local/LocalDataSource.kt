package com.example.mymovies.data.datasources.local

import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.model.MovieDb

interface LocalDataSource {
   suspend fun isEmpty(): Boolean
   suspend fun saveAllPopularMovies(movies: List<MovieDb>)
   suspend fun getAllPopularMovies(): List<Movie>
   suspend fun getPopularMovie(id: Int?): Movie
   suspend fun size(): Int


}