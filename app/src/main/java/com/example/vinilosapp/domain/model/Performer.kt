package com.example.vinilosapp.domain.model

import com.google.gson.annotations.SerializedName

data class Performer(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("birthDate") val birthDate: String?
)
