package com.faesfa.tiwo.data

import com.faesfa.tiwo.data.model.PresetsModel
import com.faesfa.tiwo.data.network.PresetsService

class PresetsRepository {
    private val api = PresetsService()

    suspend fun getPresets(query:String):List<PresetsModel>{
        val response = api.searchPresets(query)
        return response
    }
}