package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.model.MoviesResponse
import com.example.mymovies.util.Constants.Companion.INITIAL_PAGE
import retrofit2.Response

class MovieRemoteDataSource(
    private val service: MovieRemoteService
) : RemoteDataSource {
    override suspend fun getAllPopularMoviesWithApiKey(
        apikey: String?,
        language: String?,
        region: String?,
        page: Int?
    ): Response<MoviesResponse> =
        service.getPopularMoviesWithApiKey(
            apikey = apikey ?: "",
            language = language ?: "",
            region = region ?: "",
            page = page ?: INITIAL_PAGE
        )

    override suspend fun getAllPopularMoviesWithAccessToken(
        headers: Map<String, String>?,
        language: String?,
        region: String?,
        page: Int?
    ): Response<MoviesResponse> =
        service.getPopularMoviesWithAccessToken(
            headers = headers ?: mapOf(),
            language = language ?: "",
            region = region ?: "",
            page = page ?: INITIAL_PAGE
        )

}