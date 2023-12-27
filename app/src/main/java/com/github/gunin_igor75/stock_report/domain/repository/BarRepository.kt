package com.github.gunin_igor75.stock_report.domain.repository

import com.github.gunin_igor75.stock_report.domain.entity.Bar

interface BarRepository {

    suspend fun loadBars(): List<Bar>
}