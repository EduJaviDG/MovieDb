package com.example.mymovies.domain.model

import com.google.gson.annotations.SerializedName

data class MoviesResponse(
    @SerializedName("page")
    var page: Int?,
    @SerializedName("results")
    var movies: List<MovieApi>?,
    @SerializedName("total_pages")
    val totalPages: Int?,
    @SerializedName("total_results")
    val totalResults: Int?
)