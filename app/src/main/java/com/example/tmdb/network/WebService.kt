package com.example.tmdb.network

import com.example.tmdb.data.Movie
import com.example.tmdb.data.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {
    @GET("movie/now_playing")
    suspend fun getBillboard(
        @Query("api_key") apiKey:String,
        @Query("language") language:String
    ):Response<MovieResponse>
    @GET("movie/popular")
    suspend fun getPopulares(
        @Query("api_key") apiKey:String,
        @Query("language") language:String
    ):Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieById(
        @Path("movie_id") movieId:Int,
        @Query("api_key") apiKey:String,
        @Query("language") language:String
    ):Response<Movie>

    @GET("search/movie")
    suspend fun searchbyname(
        @Query("query") query:String,
        @Query("api_key") apiKey:String,
        @Query("language") language:String
    ):Response<MovieResponse>




}