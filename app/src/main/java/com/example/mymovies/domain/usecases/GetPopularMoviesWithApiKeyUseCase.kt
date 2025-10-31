package com.example.mymovies.domain.usecases

import com.example.mymovies.data.repositories.MovieRepository
import javax.inject.Inject

class GetPopularMoviesWithApiKeyUseCase @Inject constructor(private val repository: MovieRepository) {

    suspend operator fun invoke(apikey: String?, language: String?, region: String?) =
        repository.getPopularMoviesWithApiKey(
            apikey = apikey,
            language = language,
            region = region)
}