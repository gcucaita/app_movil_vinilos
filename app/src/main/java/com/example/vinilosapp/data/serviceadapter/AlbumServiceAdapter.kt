package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Track
import retrofit2.Response

class AlbumServiceAdapter(
    private val apiService: VinilosApiService = RetrofitInstance.api
) {
    suspend fun getAlbums(): Response<List<Album>> = apiService.getAlbums()
    suspend fun getAlbum(id: Int): Response<Album> = apiService.getAlbum(id)
    suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = apiService.createAlbum(request)
    suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Response<Track> = apiService.addTrack(albumId, request)
    suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Response<Album> =
        apiService.addPerformerToAlbum(albumId, performerId)
}
