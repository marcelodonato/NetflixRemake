package com.marcelodonato.netflixremake.kotlin

import com.marcelodonato.netflixremake.model.Categories
import com.marcelodonato.netflixremake.model.MovieDetail
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface NetflixAPI {

    @GET("home")
    fun listCategories(): Call<Categories>

    @GET("{id}")
    fun getMovieBy(@Path("id") id: Int): Call<MovieDetail>

}

fun retrofit() : Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://tiagoaguiar.co/api/netflix/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}