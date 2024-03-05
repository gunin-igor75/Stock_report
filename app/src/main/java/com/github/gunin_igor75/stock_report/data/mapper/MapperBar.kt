package com.github.gunin_igor75.stock_report.data.mapper

import com.github.gunin_igor75.stock_report.data.dto.BarDto
import com.github.gunin_igor75.stock_report.domain.entity.Bar
import com.github.gunin_igor75.stock_report.domain.entity.TimeFrame
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class MapperBar @Inject constructor(){

    fun fromBarDtoToBear(barDto: BarDto): Bar {
        return Bar(
            open = barDto.open,
            close = barDto.close,
            low = barDto.low,
            high = barDto.high,
            time = convertLongToCalendar(barDto.time)
        )
    }

    fun convertTimeFrameToParam(timeFrame: TimeFrame): String {
        return when (timeFrame) {
            TimeFrame.MIN_5 ->  PARAM_5_MINUTES
            TimeFrame.MIN_15 -> PARAM_15_MINUTES
            TimeFrame.MIN_30 -> PARAM_30_MINUTES
            TimeFrame.HOUR_1 -> PARAM_1_HOUR
        }
    }

    private fun convertLongToCalendar(num: Long): Calendar {
        return Calendar.getInstance().apply { time = (Date(num)) }
    }

    companion object {
        private const val PARAM_5_MINUTES = "5/minute"
        private const val PARAM_15_MINUTES = "15/minute"
        private const val PARAM_30_MINUTES = "30/minute"
        private const val PARAM_1_HOUR = "1/hour"
    }
}