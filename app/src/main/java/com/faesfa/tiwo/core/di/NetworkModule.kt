package com.faesfa.tiwo.core.di

import com.faesfa.tiwo.data.network.APIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    //Provide / Inject Retrofit
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/exercises/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Provide / Inject ApiService
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): APIService{
        return retrofit.create(APIService::class.java)
    }
}