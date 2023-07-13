package com.faesfa.tiwo.data.network

import com.faesfa.tiwo.BuildConfig
import com.faesfa.tiwo.data.model.PresetsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface APIService {
    @Headers(BuildConfig.API_KEY)
    @GET
    suspend fun getPresets(@Url url: String): Response<List<PresetsModel>>

}