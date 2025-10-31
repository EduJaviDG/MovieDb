package com.example.mymovies.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.usecases.NetworkUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val networkUseCases: NetworkUseCases) :
    ViewModel() {
    var apikey: String? = null
    var language: String? = null
    var region: String? = null
    var headers: Map<String, String>? = null

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies
    fun getPopularMoviesWithApiKey() {
        viewModelScope.launch {
            _movies.value = networkUseCases.getPopularMoviesWithApiKey(
                    apikey = apikey,
                    language = language,
                    region = region)
        }
    }
    fun getPopularMoviesWithToken() {
        viewModelScope.launch {
            _movies.value = networkUseCases.getPopularMoviesWithToken(
                headers = headers,
                language = language,
                region = region)
        }
    }
}