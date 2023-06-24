package com.faesfa.tiwo.data.network

import android.util.Log
import com.faesfa.tiwo.core.RetrofitHelper
import com.faesfa.tiwo.data.model.PresetsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PresetsService {
    suspend fun searchPresets(query: String):List<PresetsModel>{
        return withContext(Dispatchers.IO) {
            val call: Response<List<PresetsModel>> = RetrofitHelper.getRetrofit().create(APIService::class.java).getPresets(query)
            call.body() ?: emptyList<PresetsModel>()
        }
    }

}