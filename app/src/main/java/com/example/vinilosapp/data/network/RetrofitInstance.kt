package com.example.vinilosapp.data.network

import com.example.vinilosapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    @Volatile
    private var baseUrlOverride: String? = null

    @Volatile
    private var apiOverride: VinilosApiService? = null

    @Volatile
    private var retrofit: Retrofit? = null

    private fun currentBaseUrl(): String = baseUrlOverride ?: BuildConfig.BASE_URL

    fun setBaseUrlForTesting(baseUrl: String?) {
        synchronized(this) {
            baseUrlOverride = baseUrl
            retrofit = null
        }
    }

    fun setApiForTesting(apiService: VinilosApiService?) {
        synchronized(this) {
            apiOverride = apiService
        }
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: VinilosApiService
        get() {
            apiOverride?.let { return it }

            val instance = retrofit ?: synchronized(this) {
                retrofit ?: createRetrofit(currentBaseUrl()).also { retrofit = it }
            }

            return instance.create(VinilosApiService::class.java)
        }

    fun reset() {
        synchronized(this) {
            apiOverride = null
            baseUrlOverride = null
            retrofit = null
        }
    }
}
