package com.faesfa.tiwo.data.network

import android.util.Log
import com.faesfa.tiwo.core.RetrofitHelper
import com.faesfa.tiwo.data.model.PresetsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PresetsService @Inject constructor(private val api: APIService) {
    //private val retrofit = RetrofitHelper.getRetrofit()
    suspend fun searchPresets(query: String):List<PresetsModel>{
        return withContext(Dispatchers.IO) {
            val call: Response<List<PresetsModel>> = api.getPresets(query)
            call.body() ?: emptyList<PresetsModel>()
        }
    }

}