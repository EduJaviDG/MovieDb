package com.example.mymovies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.mymovies.data.datasources.remote.MovieApiResult
import com.example.mymovies.data.datasources.remote.MovieApi
import com.example.mymovies.domain.Movie
import com.example.mymovies.framework.ui.main.MainViewModel
import com.example.mymovies.usecases.LoadPopularMovies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var loadPopularMovies: LoadPopularMovies

    @Mock
    lateinit var observer: Observer<List<Movie>>

    @Test
    fun `load popular movies`() = runBlocking {
        val apiKey = "143ff3f1cb015e7c03f8655b40163d46"
        val region = "US"
        val language = "en-US"


        whenever(
            loadPopularMovies.getPopularMovies(
                apikey = apiKey,
                language = language,
                region = region
            )
        ).thenReturn(fakeList)

        val vm = MainViewModel(loadPopularMovies)

        vm.movies.observeForever(observer)

        vm.apikey = apiKey
        vm.language = language
        vm.region = region
        vm.getPopularMovies()

        verify(observer).onChanged(fakeList)
    }


}