package com.github.gunin_igor75.stock_report.domain.entity

import java.util.Calendar

data class Bar(
    val open: Float,
    val close: Float,
    val low: Float,
    val high: Float,
    val time: Calendar
)
