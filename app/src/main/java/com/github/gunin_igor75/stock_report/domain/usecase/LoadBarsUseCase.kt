package com.github.gunin_igor75.stock_report.domain.usecase

import com.github.gunin_igor75.stock_report.domain.repository.BarRepository
import javax.inject.Inject

class LoadBarsUseCase @Inject constructor(
    private val repository: BarRepository
) {

    suspend operator fun invoke() = repository.loadBars()
}