package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.Movie

interface RemoteDataSource {
    suspend fun getAllPopularMovies(
        apikey: String?,
        language: String?,
        region: String?
    ): List<Movie>?

}