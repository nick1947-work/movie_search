package com.example.myapplication
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchQuery: String,
        @Query("type") type: String,
        @Query("page") page: Int
    ): Call<SearchResults>

    @GET("/")
    fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String
    ): Call<MovieDetails>
}
