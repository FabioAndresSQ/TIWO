package com.faesfa.tiwo.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/exercises/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}