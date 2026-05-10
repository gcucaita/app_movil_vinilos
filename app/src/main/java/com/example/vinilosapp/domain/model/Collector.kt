package com.example.vinilosapp.domain.model

import com.google.gson.annotations.SerializedName

data class Collector(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("telephone") val telephone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("comments") val comments: List<AlbumComment>?,
    @SerializedName("favoritePerformers") val favoritePerformers: List<Performer>?,
    @SerializedName("collectorAlbums") val collectorAlbums: List<CollectorAlbum>?,
)
