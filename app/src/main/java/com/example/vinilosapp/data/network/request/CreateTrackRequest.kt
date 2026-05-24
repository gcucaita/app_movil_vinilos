package com.example.vinilosapp.data.network.request

import com.google.gson.annotations.SerializedName

data class CreateTrackRequest(
    @SerializedName("name") val name: String,
    @SerializedName("duration") val duration: String
)