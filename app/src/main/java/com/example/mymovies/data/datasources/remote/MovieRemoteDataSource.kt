package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.model.Movie
import com.example.mymovies.data.mappers.toDomainMovie
import com.example.mymovies.domain.model.Movie as DomainMovie

class MovieRemoteDataSource(
    private val service: MovieRemoteService?
) : RemoteDataSource {
    override suspend fun getAllPopularMoviesWithApiKey(apikey: String?, language: String?, region: String?): List<DomainMovie>? {
        val response = service?.getPopularMoviesWithApiKey(
            apikey = apikey ?: "",
            language = language ?: "",
            region = region ?: ""
        )

        val result = response?.results

        val movies = result?.map { it.toDomainMovie() }

        return movies
    }
    override suspend fun getAllPopularMoviesWithAccessToken(
        headers: Map<String, String>?,
        language: String?,
        region: String?
    ): List<Movie>? {
        val response = service?.getPopularMoviesWithAccessToken(
            headers = headers ?: mapOf(),
            language = language ?: "",
            region = region ?: ""
            )

        val result = response?.results

        val movies = result?.map { it.toDomainMovie() }

        return  movies
    }

}