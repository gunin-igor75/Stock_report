package com.github.gunin_igor75.stock_report.data.mapper

import com.github.gunin_igor75.stock_report.data.dto.BarDto
import com.github.gunin_igor75.stock_report.domain.entity.Bar
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

    private fun convertLongToCalendar(num: Long): Calendar {
        return Calendar.getInstance().apply { time = (Date(num)) }
    }
}