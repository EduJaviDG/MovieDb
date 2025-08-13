package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.Movie
import retrofit2.http.Query

interface RemoteDataSource {
    suspend fun getPopularMovies(apikey: String?, language: String?, region: String?): List<Movie>?

}