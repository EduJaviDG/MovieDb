package com.example.mymovies.data.datasources.local

import com.example.mymovies.domain.model.MovieDb
import com.example.mymovies.data.mappers.toDomainMovie
import javax.inject.Inject
import com.example.mymovies.domain.model.Movie as DomainMovie

class MovieLocalDataSource @Inject constructor (private val dao: MovieDao): LocalDataSource {
    override suspend fun isEmpty(): Boolean = dao.countMovies() <= 0
    override suspend fun saveAllPopularMovies(movies: List<MovieDb>) = dao.insertAll(movies)
    override suspend fun getAllPopularMovies(): List<DomainMovie> {
        val result =  dao.getAll()
        val movies = result.map { it.toDomainMovie() }
        //val movies = result.map { movies -> movies.map { it.toDomainMovie() } }

        return movies

    }

    override suspend fun getPopularMovie(id: Int?): DomainMovie {
        val result = dao.findById(id ?: 0)
        val movie = result.toDomainMovie()

        return movie
    }

    override suspend fun size(): Int = dao.size()

}