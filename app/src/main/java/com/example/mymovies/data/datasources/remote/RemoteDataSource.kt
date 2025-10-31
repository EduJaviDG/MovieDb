package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.Movie

interface RemoteDataSource {
    suspend fun getAllPopularMoviesWithApiKey(
        apikey: String?,
        language: String?,
        region: String?
    ): List<Movie>?

    suspend fun getAllPopularMoviesWithAccessToken(
        headers: Map<String, String>,
        language: String?,
        region: String?
    ): List<Movie>?

}