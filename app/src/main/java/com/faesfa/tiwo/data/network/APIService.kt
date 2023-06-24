package com.faesfa.tiwo.data.network

import com.faesfa.tiwo.data.model.PresetsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface APIService {
    @Headers("X-RapidAPI-Key:fcc42eeacbmshc49741d95aa4f21p1c5e67jsnc662f1c6c319")
    @GET
    suspend fun getPresets(@Url url: String): Response<List<PresetsModel>>

}