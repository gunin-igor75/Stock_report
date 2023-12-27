package com.github.gunin_igor75.stock_report.data.dto

import com.google.gson.annotations.SerializedName


data class BarDto(
    @SerializedName("o") val open: Float,
    @SerializedName("c") val close: Float,
    @SerializedName("l") val low: Float,
    @SerializedName("h") val high: Float,
    @SerializedName("t") val time: Long
)
