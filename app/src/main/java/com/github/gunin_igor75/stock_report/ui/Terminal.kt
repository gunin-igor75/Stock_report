package com.github.gunin_igor75.stock_report.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import com.github.gunin_igor75.stock_report.domain.entity.Bar
import kotlin.math.roundToInt

private const val MIN_VISIBLE__COUNT_BAR = 20

@Composable
fun Terminal(bars: List<Bar>) {

    var terminalWidth by remember {
        mutableStateOf(0f)
    }

    var visibleBarCount by remember {
        mutableStateOf(100)
    }

    var scrolledBy by remember {
        mutableStateOf(0f)
    }

    val widthBar by remember {
        derivedStateOf {
            terminalWidth / visibleBarCount
        }
    }

    val visibleBars by remember {
        derivedStateOf {
            val startIndex = (scrolledBy / widthBar).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarCount).coerceAtMost(bars.size)
            bars.subList(startIndex, endIndex)
        }
    }

    val transformableState = TransformableState { zoomChange, panChange, _: Float ->
        visibleBarCount = (visibleBarCount / zoomChange).roundToInt()
            .coerceIn(MIN_VISIBLE__COUNT_BAR, bars.size)

        scrolledBy = (scrolledBy + panChange.x)
            .coerceAtLeast(0f)
            .coerceAtMost(bars.size * widthBar - terminalWidth)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .transformable(transformableState),
    ) {
        terminalWidth = size.width

        val maxHigh = visibleBars.maxOf { it.high }
        val minLow = visibleBars.minOf { it.low }
        val pxPerPoint = size.height / (maxHigh - minLow)

        translate(left = scrolledBy) {
            bars.forEachIndexed { index, bar ->
                val xOffset = size.width - index * widthBar
                drawLine(
                    color = Color.White,
                    start = Offset(xOffset, size.height - ((bar.low - minLow) * pxPerPoint)),
                    end = Offset(xOffset, size.height - ((bar.high - minLow) * pxPerPoint)),
                    strokeWidth = 1f
                )
                drawLine(
                    color = if (bar.open < bar.close) Color.Green else Color.Red,
                    start = Offset(xOffset, size.height - ((bar.open - minLow) * pxPerPoint)),
                    end = Offset(xOffset, size.height - ((bar.close - minLow) * pxPerPoint)),
                    strokeWidth = widthBar / 2
                )
            }
        }
    }
}