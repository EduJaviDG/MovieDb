package com.example.mymovies.data.datasources.remote

import com.example.mymovies.data.datasources.remote.MovieApi
import com.google.gson.annotations.SerializedName

data class MovieApiResult(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("results")
    val results: List<MovieApi>?,
    @SerializedName("total_pages")
    val totalPages: Int?,
    @SerializedName("total_results")
    val totalResults: Int?
)