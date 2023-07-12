package com.faesfa.tiwo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.faesfa.tiwo.data.database.dao.PresetsDao
import com.faesfa.tiwo.data.database.entities.PresetEntity

@Database(entities = [PresetEntity::class], version = 1)
abstract class PresetsDataBase:RoomDatabase() {
    abstract fun getPresetDao(): PresetsDao
}