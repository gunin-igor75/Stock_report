package com.github.gunin_igor75.stock_report.data.dto

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("results") val results: List<BarDto>
)
