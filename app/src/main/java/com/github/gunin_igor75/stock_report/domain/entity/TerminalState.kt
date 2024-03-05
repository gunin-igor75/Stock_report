package com.github.gunin_igor75.stock_report.domain.entity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.math.roundToInt

data class TerminalState(
    val bars: List<Bar>,
    val terminalWidth: Float = 1f,
    val terminalHeight: Float = 1f,
    val visibleBarCount: Int = 100,
    val scrolledBy: Float = 0f
) {
    val widthBar: Float
        get() = terminalWidth / visibleBarCount

    private val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / widthBar).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarCount).coerceAtMost(bars.size)
            return bars.subList(startIndex, endIndex)
        }

    val maxHigh: Float
        get() =  visibleBars.maxOf { it.high }

    val minLow: Float
        get() = visibleBars.minOf { it.low }

    val pxPerPoint
        get() = terminalHeight / (maxHigh - minLow)

    companion object {

        val Saver: Saver<MutableState<TerminalState>, Any> = listSaver(
            save = {
                val terminalState = it.value
                listOf(
                    terminalState.bars,
                    terminalState.terminalWidth,
                    terminalState.visibleBarCount,
                    terminalState.scrolledBy
                )
            },
            restore = {
                val terminalState = TerminalState(
                    bars = (it[0] as List<Bar>),
                    terminalWidth = it[1] as Float,
                    visibleBarCount = it[2] as Int,
                    scrolledBy = it[3] as Float,
                )
                mutableStateOf(terminalState)
            }
        )
    }
}

@Composable
fun rememberTerminalState(bars: List<Bar>):MutableState<TerminalState>{
    return rememberSaveable(saver = TerminalState.Saver) {
        mutableStateOf(TerminalState(bars))
    }
}