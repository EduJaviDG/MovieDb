package com.example.mymovies.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.usecases.LoadPopularMovies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val loadPopularMovies: LoadPopularMovies) :
    ViewModel() {
    var apikey: String? = null
    var language: String? = null
    var region: String? = null
    var headers: Map<String, String>? = null

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies
    fun getPopularMovies() {
        viewModelScope.launch {
            _movies.value = loadPopularMovies.getPopularMovies(
                    apikey = apikey,
                    language = language,
                    region = region)
        }
    }
}