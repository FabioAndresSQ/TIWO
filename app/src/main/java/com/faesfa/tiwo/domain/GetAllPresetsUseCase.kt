package com.faesfa.tiwo.domain

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.faesfa.tiwo.R
import com.faesfa.tiwo.data.PresetsRepository
import com.faesfa.tiwo.data.database.entities.toDatabase
import com.faesfa.tiwo.domain.model.Preset
import javax.inject.Inject

class GetAllPresetsUseCase @Inject constructor(private val repository: PresetsRepository){
    suspend operator fun invoke (context: Context): List<Preset>{
        try {
            val presets = repository.getPresetsFromApi("exercises/")
            return if (presets.isNotEmpty()){
                repository.clearPresetsTable()
                repository.insertAllPresets(presets.map { it.toDatabase() })
                presets
            } else {
                Log.d("DATEMATH", "Error: EMPTY LIST RETURNED")
                return repository.getAllPresetsFromDb()
            }
        } catch (e: Exception){
            val result = emptyList<Preset>()
            Log.d("DATEMATH", "Error Connecting to Internet")
            return result
        }

    }
}