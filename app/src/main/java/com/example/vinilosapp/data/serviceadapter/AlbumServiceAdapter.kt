package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.domain.model.Album
import retrofit2.Response

class AlbumServiceAdapter(
    private val apiService: VinilosApiService = RetrofitInstance.api
) {
    suspend fun getAlbums(): Response<List<Album>> = apiService.getAlbums()
    suspend fun getAlbum(id: Int): Response<Album> = apiService.getAlbum(id)
    suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = apiService.createAlbum(request)
}
