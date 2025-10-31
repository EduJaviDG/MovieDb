package com.example.mymovies.domain.usecases

import com.example.mymovies.data.repositories.MovieRepository

class LoadPopularMovies(private val repository: MovieRepository) {
    suspend fun getPopularMovies(apikey: String?, language: String?, region: String?) =
        repository.getPopularMoviesWithApiKey(
            apikey = apikey,
            language = language,
            region = region)

}