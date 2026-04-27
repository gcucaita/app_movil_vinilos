package com.example.vinilosapp.data.network

import com.example.vinilosapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    @Volatile
    private var baseUrlOverride: String? = null

    @Volatile
    private var retrofit: Retrofit? = null

    private fun currentBaseUrl(): String = baseUrlOverride ?: BuildConfig.BASE_URL

    fun setBaseUrlForTesting(baseUrl: String?) {
        synchronized(this) {
            baseUrlOverride = baseUrl
            retrofit = null
        }
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: VinilosApiService
        get() {
            val instance = retrofit ?: synchronized(this) {
                retrofit ?: createRetrofit(currentBaseUrl()).also { retrofit = it }
            }
            return instance.create(VinilosApiService::class.java)
        }

    fun reset() {
        setBaseUrlForTesting(null)
    }
}