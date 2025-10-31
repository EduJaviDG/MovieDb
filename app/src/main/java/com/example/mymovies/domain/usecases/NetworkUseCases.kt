package com.example.mymovies.domain.usecases

data class NetworkUseCases (
    val getPopularMoviesWithApiKey: GetPopularMoviesWithApiKeyUseCase,
    val getPopularMoviesWithToken: GetPopularMoviesWithTokenUseCase
)