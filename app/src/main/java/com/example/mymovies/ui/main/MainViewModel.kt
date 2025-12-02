package com.example.mymovies.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovies.domain.model.MoviesResponse
import com.example.mymovies.domain.usecases.NetworkUseCases
import com.example.mymovies.util.Constants.Companion.INITIAL_PAGE
import com.example.mymovies.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val networkUseCases: NetworkUseCases) :
    ViewModel() {
    var apikey: String? = null
    var language: String? = null
    var region: String? = null
    var headers: Map<String, String>? = null
    var page: Int = INITIAL_PAGE

    private val _popularMovies = MutableLiveData<Resource<MoviesResponse>>()
    val popularMovies: LiveData<Resource<MoviesResponse>> get() = _popularMovies

    private var popularMoviesResponse: MoviesResponse? = null
    fun getPopularMoviesWithApiKey() =
        viewModelScope.launch {
            _popularMovies.postValue(Resource.Loading())
            val response = networkUseCases.getPopularMoviesWithApiKey(
                apikey = apikey,
                language = language,
                region = region,
                page = page
            )
            _popularMovies.postValue(handlePopularMoviesResponse(response))
        }


    fun getPopularMoviesWithToken() =
        viewModelScope.launch {
            _popularMovies.postValue(Resource.Loading())
            val response = networkUseCases.getPopularMoviesWithToken(
                headers = headers,
                language = language,
                region = region,
                page = page
            )
            _popularMovies.postValue(handlePopularMoviesResponse(response))
        }

    private fun handlePopularMoviesResponse(
        response: Response<MoviesResponse>
    ): Resource<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                page++

                if (popularMoviesResponse == null) {
                    popularMoviesResponse = result
                } else {

                    val currentPage = popularMoviesResponse?.page?.plus(1)

                    val oldMovies = popularMoviesResponse?.movies?.toMutableList()
                    val newMovies = result.movies

                    oldMovies?.addAll(newMovies ?: emptyList())
                    val updateMovies = oldMovies?.toList()

                    popularMoviesResponse?.movies = updateMovies
                    popularMoviesResponse?.page = currentPage
                }
                return Resource.Success(popularMoviesResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }

}