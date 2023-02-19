package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class SearchResults(
    @SerializedName("Search")
    val movies: List<Movie>,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("Response")
    val response: String
)

data class Movie(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val imdbId: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val posterUrl: String
)

data class MovieDetails(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val imdbId: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val posterUrl: String,
    @SerializedName("Plot")
    val plot: String,
    @SerializedName("Runtime")
    val runtime: String,
    @SerializedName("Genre")
    val genre: String,
    @SerializedName("Director")
    val director: String,
    @SerializedName("Writer")
    val writer: String,
    @SerializedName("Actors")
    val actors: String
)
