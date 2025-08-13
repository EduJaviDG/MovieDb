package com.example.mymovies.util

import com.example.mymovies.data.datasources.local.MovieDb as DbMovie
import com.example.mymovies.data.datasources.remote.MovieApi as ServerMovie
import com.example.mymovies.domain.Movie as DomainMovie

fun ServerMovie.toDomainMovie(): DomainMovie = DomainMovie(
    adult = adult,
    backdropPath = backdropPath,
    genreIds = genreIds,
    id = id,
    originalLanguage =  originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    releaseDate = releaseDate,
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun DomainMovie.toDbMovie(): DbMovie = DbMovie(
    adult = adult,
    backdropPath = backdropPath,
    genreIds = genreIds?.map { it.toString() },
    id = id,
    originalLanguage =  originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    releaseDate = releaseDate,
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun DbMovie.toDomainMovie(): DomainMovie = DomainMovie(
    adult = adult,
    backdropPath = backdropPath,
    genreIds = genreIds?.map { it.toInt() },
    id = id,
    originalLanguage =  originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    releaseDate = releaseDate,
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount
)

