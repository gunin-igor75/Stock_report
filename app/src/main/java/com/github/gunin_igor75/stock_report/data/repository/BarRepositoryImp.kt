package com.github.gunin_igor75.stock_report.data.repository

import com.github.gunin_igor75.stock_report.data.mapper.MapperBar
import com.github.gunin_igor75.stock_report.data.network.ApiService
import com.github.gunin_igor75.stock_report.domain.entity.Bar
import com.github.gunin_igor75.stock_report.domain.repository.BarRepository
import javax.inject.Inject

class BarRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val mapperBar: MapperBar
) : BarRepository {

    override suspend fun loadBars(): List<Bar> {
        val result = apiService.loadBars()
        return result.results.map { mapperBar.fromBarDtoToBear(it) }
    }
}