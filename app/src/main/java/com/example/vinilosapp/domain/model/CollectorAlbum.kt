package com.example.vinilosapp.domain.model

import com.google.gson.annotations.SerializedName

data class CollectorAlbum(
    @SerializedName("id") val id: Int,
    @SerializedName("price") val price: Int?,
    @SerializedName("status") val status: String?,
)
