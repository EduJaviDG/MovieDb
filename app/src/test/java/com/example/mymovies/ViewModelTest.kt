package com.example.mymovies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.mymovies.data.datasources.remote.MovieApiResult
import com.example.mymovies.data.datasources.remote.MovieApi
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var loadPopularMovies: LoadPopularMovies

    @Mock
    lateinit var observer: Observer<List<MovieApi>>
    @Test
    fun `test every works`() {
        Assert.assertTrue(true)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp(){
        Dispatchers.setMain(dispatcher)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown(){
        Dispatchers.resetMain()

    }

    @Test
    fun `load popular movies`() = runBlocking {
        val apiKey = "143ff3f1cb015e7c03f8655b40163d46"
        val region = "US"
        val language = "en-US"
        val fakeMovie = MovieApi(
            adult = false,
            backdropPath = "/wSy4EZlZcbxyoLS5jQk5Vq3Az8.jpg",
            genreIds = listOf(878, 53),
            id = 755898,
            originalLanguage = "en",
            originalTitle = "War of the Worlds",
            overview = "Will Radford is a top analyst for Homeland Security who tracks potential threats through a mass surveillance program, until one day an attack by an unknown entity leads him to question whether the government is hiding something from him... and from the rest of the world.",
            popularity = 2226.0499,
            posterPath = "/yvirUYrva23IudARHn3mMGVxWqM.jpg",
            releaseDate = "2025-07-29",
            title = "War of the Worlds",
            video = false,
            voteAverage = 4.543,
            voteCount = 207
        )
        val fakeList = listOf(fakeMovie)
        val fakeResult =
            MovieApiResult(page = 1, results = fakeList, totalPages = 1, totalResults = 1)

        whenever(
            loadPopularMovies.getPopularMovie(
                apikey = apiKey,
                language = language,
                region = region
            )
        ).thenReturn(fakeResult)

        val vm = MainViewModel(loadPopularMovies)

        vm.movies.observeForever(observer)

        vm.apikey = apiKey
        vm.language = language
        vm.region = region
        vm.getPopularMovies()

        verify(observer).onChanged(fakeList)
    }


}