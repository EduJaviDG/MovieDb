package com.example.mymovies.framework.data.datasources.remote

import com.example.mymovies.data.datasources.remote.RemoteDataSource
import com.example.mymovies.domain.Movie
import com.example.mymovies.util.toDomainMovie
import com.example.mymovies.domain.Movie as DomainMovie

class MovieRemoteDataSource(
    private val client: MovieRemoteClient?
) : RemoteDataSource {
    override suspend fun getAllPopularMoviesWithApiKey(apikey: String?, language: String?, region: String?): List<DomainMovie>? {
        val response = client?.service?.getPopularMoviesWithApiKey(
            apikey = apikey ?: "",
            language = language ?: "",
            region = region ?: ""
        )

        val result = response?.results

        val movies = result?.map { it.toDomainMovie() }

        return movies
    }
    override suspend fun getAllPopularMoviesWithAccessToken(
        headers: Map<String, String>,
        language: String?,
        region: String?
    ): List<Movie>? {
        val response = client?.service?.getPopularMoviesWithAccessToken(
            headers = headers,
            language = language ?: "",
            region = region ?: ""
            )

        val result = response?.results

        val movies = result?.map { it.toDomainMovie() }

        return  movies
    }

}