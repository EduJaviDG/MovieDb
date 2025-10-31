package com.example.mymovies

import com.example.mymovies.domain.model.MovieDb as DbMovie
import com.example.mymovies.domain.model.Movie as DomainMovie

val fakeDomainMovie = DomainMovie(
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

val fakeDbMovie = DbMovie(
    adult = false,
    backdropPath = "/wSy4EZlZcbxyoLS5jQk5Vq3Az8.jpg",
    genreIds = listOf("878", "53"),
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

val fakeDomainList = listOf(fakeDomainMovie)

val fakeDbList = listOf(fakeDbMovie)

