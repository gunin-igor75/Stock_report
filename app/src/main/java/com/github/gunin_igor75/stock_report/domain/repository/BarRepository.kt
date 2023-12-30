package com.github.gunin_igor75.stock_report.domain.repository

import com.github.gunin_igor75.stock_report.domain.entity.Bar
import com.github.gunin_igor75.stock_report.domain.entity.TimeFrame

interface BarRepository {

    suspend fun loadBars(timeFrame: TimeFrame): List<Bar>
}