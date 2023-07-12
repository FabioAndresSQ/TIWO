package com.faesfa.tiwo.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.faesfa.tiwo.domain.model.Preset
import com.google.gson.annotations.SerializedName

@Entity(tableName = "all_presets_table")
data class PresetEntity (
    @ColumnInfo (name = "bodyPart") var bodyPart  : String,
    @ColumnInfo (name = "equipment") var equipment : String,
    @ColumnInfo (name = "gifUrl") var gifUrl    : String,
    @PrimaryKey (autoGenerate = false) var id        : String,
    @ColumnInfo (name = "name") var name      : String,
    @ColumnInfo (name = "target") var target    : String
        )

fun Preset.toDatabase() = PresetEntity(bodyPart, equipment, gifUrl, id, name, target)