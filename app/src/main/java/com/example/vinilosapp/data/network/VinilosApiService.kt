package com.example.vinilosapp.data.network

import com.example.vinilosapp.domain.model.Album
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VinilosApiService {
    @GET("albums")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: Int): Response<Album>
}