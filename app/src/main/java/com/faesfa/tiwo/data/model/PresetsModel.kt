package com.faesfa.tiwo.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PresetsModel (
    @SerializedName("bodyPart"  ) var bodyPart  : String? = null,
    @SerializedName("equipment" ) var equipment : String? = null,
    @SerializedName("gifUrl"    ) var gifUrl    : String? = null,
    @SerializedName("id"        ) var id        : String? = null,
    @SerializedName("name"      ) var name      : String? = null,
    @SerializedName("target"    ) var target    : String? = null
) : Serializable