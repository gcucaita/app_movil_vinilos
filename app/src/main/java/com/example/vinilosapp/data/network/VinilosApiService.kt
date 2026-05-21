package com.example.vinilosapp.data.network

import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VinilosApiService {
    @GET("albums")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: Int): Response<Album>

    @GET("collectors")
    suspend fun getCollectors(): Response<List<Collector>>

    @GET("collectors/{id}")
    suspend fun getCollector(@Path("id") id: Int): Response<Collector>

    @GET("musicians")
    suspend fun getMusicians(): Response<List<Performer>>

    @GET("musicians/{id}")
    suspend fun getMusician(@Path("id") id: Int): Response<Performer>
}
