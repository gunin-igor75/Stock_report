package com.github.gunin_igor75.stock_report.data.network

import com.github.gunin_igor75.stock_report.data.dto.Result
import retrofit2.http.GET

interface ApiService {

    @GET("aggs/ticker/AAPL/range/1/hour/2022-01-01/2023-01-01?adjusted=true&sort=asc&limit=50000")
    suspend fun loadBars(): Result
}