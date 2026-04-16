package com.example.vinilosapp.data.model

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("duration") val duration: String?
)
