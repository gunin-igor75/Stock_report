package com.github.gunin_igor75.stock_report.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private const val BASE_URL = "https://api.polygon.io/v2/"
    private const val HEADER = "Authorization"
    private const val TOKEN = "Bearer oHe_sML9dnN1kQ_H015EBp9zIFd3R6an"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(createAuthorizationInterceptor())
        .addInterceptor(loggingInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    private fun loggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun createAuthorizationInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newBuilder = chain.request().newBuilder()
            newBuilder.addHeader(HEADER, TOKEN)
            return@Interceptor chain.proceed(newBuilder.build())
        }
    }
}