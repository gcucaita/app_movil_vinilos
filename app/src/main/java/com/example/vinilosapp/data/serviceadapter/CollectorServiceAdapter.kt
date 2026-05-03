package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Collector
import retrofit2.Response

class CollectorServiceAdapter(
    private val apiService: VinilosApiService = RetrofitInstance.api
) {
    suspend fun getCollectors(): Response<List<Collector>> = apiService.getCollectors()
}
