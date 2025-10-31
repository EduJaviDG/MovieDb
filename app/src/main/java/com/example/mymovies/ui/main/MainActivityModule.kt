package com.example.mymovies.ui.main

import com.example.mymovies.data.repositories.MovieRepository
import com.example.mymovies.domain.usecases.LoadPopularMovies
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class MainActivityModule {
    @Provides
    fun loadPopularMovies(repository: MovieRepository) = LoadPopularMovies(repository)

}