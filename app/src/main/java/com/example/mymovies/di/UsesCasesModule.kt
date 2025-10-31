package com.example.mymovies.di

import com.example.mymovies.domain.usecases.GetPopularMoviesWithApiKeyUseCase
import com.example.mymovies.domain.usecases.GetPopularMoviesWithTokenUseCase
import com.example.mymovies.domain.usecases.NetworkUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UsesCasesModule {
    @Provides
    fun provideNetworkUseCases(
        getPopularMoviesWithApiKeyUseCase: GetPopularMoviesWithApiKeyUseCase,
        getPopularMoviesWithTokenUseCase: GetPopularMoviesWithTokenUseCase
    ) = NetworkUseCases(
        getPopularMoviesWithApiKey = getPopularMoviesWithApiKeyUseCase,
        getPopularMoviesWithToken = getPopularMoviesWithTokenUseCase)

}