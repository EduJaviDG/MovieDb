package com.example.mymovies.data.repositories

import com.example.mymovies.data.datasources.local.MovieLocalDataSource
import com.example.mymovies.data.datasources.remote.MovieRemoteDataSource
import com.example.mymovies.domain.model.MoviesResponse
import com.example.mymovies.util.Constants.Companion.INITIAL_PAGE
import retrofit2.Response
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) {
    suspend fun getPopularMoviesWithApiKey(
        apikey: String?,
        language: String?,
        region: String?,
        page: Int?
    ): Response<MoviesResponse> =
        movieRemoteDataSource.getAllPopularMoviesWithApiKey(
            apikey = apikey ?: "",
            language = language ?: "",
            region = region ?: "",
            page = page ?: INITIAL_PAGE
        )

    suspend fun getPopularMoviesWithAccessToken(
        headers: Map<String, String>?,
        language: String?,
        region: String?,
        page: Int?
    ): Response<MoviesResponse> =
        movieRemoteDataSource.getAllPopularMoviesWithAccessToken(
            headers = headers,
            language = language ?: "",
            region = region ?: "",
            page = page ?: INITIAL_PAGE
        )

}