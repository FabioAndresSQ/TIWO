package com.faesfa.tiwo.domain.model

import androidx.room.ColumnInfo
import com.faesfa.tiwo.data.database.entities.PresetEntity
import com.faesfa.tiwo.data.model.PresetsModel
import java.io.Serializable

data class Preset (
    val bodyPart  : String,
    val equipment : String,
    val gifUrl    : String,
    val id        : String,
    val name      : String,
    val target    : String
        ) : Serializable

fun PresetsModel.toDomain() = Preset(bodyPart!!,equipment!!, gifUrl!!, id!!, name!!, target!!)
fun PresetEntity.toDomain() = Preset(bodyPart!!,equipment!!, gifUrl!!, id!!, name!!, target!!)