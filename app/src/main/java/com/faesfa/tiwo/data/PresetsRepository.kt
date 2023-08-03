package com.faesfa.tiwo.data

import android.util.Log
import com.faesfa.tiwo.data.database.dao.PresetsDao
import com.faesfa.tiwo.data.database.entities.PresetEntity
import com.faesfa.tiwo.data.model.PresetsModel
import com.faesfa.tiwo.data.network.PresetsService
import com.faesfa.tiwo.domain.model.Preset
import com.faesfa.tiwo.domain.model.toDomain
import javax.inject.Inject

class PresetsRepository @Inject  constructor(
    private val api : PresetsService,
    private val presetsDao: PresetsDao
) {

    suspend fun getPresetsFromApi(query:String):List<Preset>{
        val response = api.searchPresets(query)
        return response.map { it.toDomain() }
    }

    suspend fun getAllPresetsFromDb():List<Preset>{
        val response = presetsDao.getAllPresets()
        return response.map { it.toDomain() }
    }
    suspend fun getPresetsByMuscleFromDb(query:String):List<Preset>{
        val response = presetsDao.getPresetsByMuscle(query)
        return response.map { it.toDomain() }
    }

    suspend fun getPresetsBySearchFromDb(query: String):List<Preset>{
        val response = presetsDao.getPresetsBySearch(query)
        return response.map { it.toDomain() }
    }

    suspend fun getPresetsById(query: String):Preset{
        val response = presetsDao.getPresetsById(query)
        return response.toDomain()
    }

    suspend fun getPresetIdByName(name: String):Preset?{
        val response = presetsDao.getPresetIdByName(name)
        Log.d("NAME CHECK", "RepositoryResponse: $response")
        return if (response != null) {response.toDomain()} else {null}
    }

    suspend fun insertAllPresets(presets:List<PresetEntity>){
        presetsDao.insertAllPresets(presets)
    }

    suspend fun clearPresetsTable(){
        presetsDao.clearTable()
    }
}