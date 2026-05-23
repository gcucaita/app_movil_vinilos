package com.example.vinilosapp.data.network

import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface VinilosApiService {
    @GET("albums")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: Int): Response<Album>

    @POST("albums")
    suspend fun createAlbum(@Body request: CreateAlbumRequest): Response<Album>

    @GET("collectors")
    suspend fun getCollectors(): Response<List<Collector>>

    @GET("collectors/{id}")
    suspend fun getCollector(@Path("id") id: Int): Response<Collector>

    @GET("musicians")
    suspend fun getMusicians(): Response<List<Performer>>

    @GET("musicians/{id}")
    suspend fun getMusician(@Path("id") id: Int): Response<Performer>

    @POST("albums/{id}/tracks")
    suspend fun addTrack(@Path("id") albumId: Int, @Body request: CreateTrackRequest): Response<Track>

    @GET("bands")
    suspend fun getBands(): Response<List<Performer>>
    @POST("albums/{albumId}/performers/{performerId}")
    suspend fun addPerformerToAlbum(
        @Path("albumId") albumId: Int,
        @Path("performerId") performerId: Int
    ): Response<Album>


}
