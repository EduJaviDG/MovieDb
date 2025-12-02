package com.example.mymovies.domain.usecases

import com.example.mymovies.data.repositories.MovieRepository
import javax.inject.Inject

class GetPopularMoviesWithTokenUseCase @Inject constructor(private val repository: MovieRepository) {

    suspend operator fun invoke(
        headers: Map<String, String>?,
        language: String?,
        region: String?,
        page: Int?) =
        repository.getPopularMoviesWithAccessToken(
            headers = headers,
            language = language,
            region = region,
            page = page)
}