package com.example.vinilosapp.domain.model

import com.google.gson.annotations.SerializedName

data class AlbumComment(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String?,
    @SerializedName("rating") val rating: Int?
)
