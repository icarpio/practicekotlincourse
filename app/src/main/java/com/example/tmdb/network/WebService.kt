package com.example.tmdb.network

import com.example.tmdb.data.Movie
import com.example.tmdb.data.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    @GET("now_playing")
    suspend fun getBillboard(
        @Query("api_key") apiKey:String,
        @Query("language") language:String
    ):Response<MovieResponse>
    @GET("popular")
    suspend fun getPopulares(
        @Query("api_key") apiKey:String,
        @Query("language") language:String
    ):Response<MovieResponse>
}