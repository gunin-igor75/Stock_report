package com.github.gunin_igor75.stock_report.data.network

import com.github.gunin_igor75.stock_report.data.dto.Result
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("aggs/ticker/AAPL/range/{timeframe}/2022-01-01/2023-01-01?adjusted=true&sort=desc&limit=50000")
    suspend fun loadBars(@Path("timeframe") param: String): Result
}