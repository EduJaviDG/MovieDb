package com.example.mymovies.data.repositories

import com.example.mymovies.data.datasources.local.MovieLocalDataSource
import com.example.mymovies.data.datasources.remote.MovieRemoteDataSource
import com.example.mymovies.data.mappers.toDbMovie
import javax.inject.Inject
import com.example.mymovies.domain.model.Movie as DomainMovie

class MovieRepository @Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) {
    suspend fun getPopularMoviesWithApiKey(
        apikey: String?,
        language: String?,
        region: String?
    ): List<DomainMovie> {
        if (movieLocalDataSource.isEmpty()) {
            val movies = movieRemoteDataSource.getAllPopularMoviesWithApiKey(
                apikey = apikey ?: "",
                language = language ?: "",
                region = region ?: ""
            )
            val dbMovies = movies?.map { it.toDbMovie() }
            movieLocalDataSource.saveAllPopularMovies(dbMovies ?: emptyList())
        }

        return movieLocalDataSource.getAllPopularMovies()

    }

    suspend fun getPopularMoviesWithAccessToken(
        headers: Map<String, String>?,
        language: String?,
        region: String?
    ): List<DomainMovie> {
        if (movieLocalDataSource.isEmpty()) {
            val movies = movieRemoteDataSource.getAllPopularMoviesWithAccessToken(
               headers = headers,
                language = language ?: "",
                region = region ?: "")
            val dbMovies = movies?.map{ it.toDbMovie() }
            movieLocalDataSource.saveAllPopularMovies(dbMovies ?: emptyList())
        }

        return movieLocalDataSource.getAllPopularMovies()
    }

}