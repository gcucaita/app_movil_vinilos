package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Performer
import retrofit2.Response

class MusicianServiceAdapter(
    private val apiService: VinilosApiService = RetrofitInstance.api
) {
    suspend fun getMusicians(): Response<List<Performer>> = apiService.getMusicians()
}
