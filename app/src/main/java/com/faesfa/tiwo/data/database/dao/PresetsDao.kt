package com.faesfa.tiwo.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.faesfa.tiwo.data.database.entities.PresetEntity
import com.faesfa.tiwo.data.model.PresetsModel

@Dao
interface PresetsDao {
    @Insert
    suspend fun insertAllPresets(presets : List<PresetEntity>)

    @Query("SELECT * FROM all_presets_table WHERE bodyPart=(:muscle)")
    suspend fun getPresetsByMuscle(muscle:String):List<PresetEntity>

    @Query("SELECT * FROM all_presets_table WHERE name LIKE '%' || :text || '%'")
    suspend fun getPresetsBySearch(text:String):List<PresetEntity>

    @Query("SELECT * FROM all_presets_table WHERE id=:text")
    suspend fun getPresetsById(text:String):PresetEntity

    @Query("SELECT * FROM all_presets_table WHERE name=:name")
    suspend fun getPresetIdByName(name:String): PresetEntity

    @Query("SELECT * FROM all_presets_table")
    suspend fun getAllPresets():List<PresetEntity>

    @Query("DELETE FROM all_presets_table")
    suspend fun clearTable()
}