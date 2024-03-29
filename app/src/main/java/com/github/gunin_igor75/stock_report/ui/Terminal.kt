package com.github.gunin_igor75.stock_report.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.gunin_igor75.stock_report.R
import com.github.gunin_igor75.stock_report.domain.entity.Bar
import com.github.gunin_igor75.stock_report.domain.entity.TerminalScreenState
import com.github.gunin_igor75.stock_report.domain.entity.TerminalState
import com.github.gunin_igor75.stock_report.domain.entity.TimeFrame
import com.github.gunin_igor75.stock_report.domain.entity.rememberTerminalState
import com.github.gunin_igor75.stock_report.presentation.MainViewModel
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.ui.text.rememberTextMeasurer as rememberTextMeasurer

private const val MIN_VISIBLE__COUNT_BAR = 20


@Composable
fun Terminal(
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel = hiltViewModel()
    val screenState = viewModel.state.collectAsState()

    when (val currentState = screenState.value) {
        is TerminalScreenState.Initial -> {}
        is TerminalScreenState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is TerminalScreenState.Content -> {
            val terminalState = rememberTerminalState(currentState.bars)

            StockChart(
                modifier = modifier,
                terminalState = terminalState,
                timeframe = currentState.timeFrame,
                omChangeTerminalState = {
                    terminalState.value = it
                }
            )

            currentState.bars.firstOrNull()?.let {
                LegendPrices(
                    modifier = modifier,
                    terminalState = terminalState,
                    lastPrice = it.close,
                )
            }
            TimeFrames(
                selectedFrame = currentState.timeFrame,
                onTimeFrameSelected = {
                    viewModel.loadBars(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeFrames(
    selectedFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit

) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFrame.values().forEach { timeframe ->
            val isSelected = selectedFrame == timeframe
            AssistChip(
                onClick = {
                    onTimeFrameSelected(timeframe)
                },
                label = {
                    Text(text = stringResource(id = getLabelResId(timeframe)))
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) Color.White else Color.Black,
                    labelColor = if (isSelected) Color.Black else Color.White
                )
            )
        }
    }
}

@Composable
private fun getLabelResId(timeframe: TimeFrame): Int {
    return when (timeframe) {
        TimeFrame.MIN_5 -> R.string.timeframe_5_minutes
        TimeFrame.MIN_15 -> R.string.timeframe_15_minutes
        TimeFrame.MIN_30 -> R.string.timeframe_30_minutes
        TimeFrame.HOUR_1 -> R.string.timeframe_1_hour
    }
}


@OptIn(ExperimentalTextApi::class)
@Composable
private fun StockChart(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    timeframe: TimeFrame,
    omChangeTerminalState: (TerminalState) -> Unit

) {
    val currentState = terminalState.value
    val transformableState = rememberTransformableState { zoomChange, panChange, _: Float ->
        with(currentState) {
            val visibleBarCount = (visibleBarCount / zoomChange).roundToInt()
                .coerceIn(MIN_VISIBLE__COUNT_BAR, bars.size)

            val scrolledBy = (scrolledBy + panChange.x)
                .coerceAtLeast(0f)
                .coerceAtMost(bars.size * widthBar - terminalWidth)
            omChangeTerminalState(
                copy(
                    visibleBarCount = visibleBarCount,
                    scrolledBy = scrolledBy
                )
            )
        }
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(
                top = 32.dp,
                bottom = 32.dp,
                end = 32.dp
            )
            .transformable(transformableState)
            .onSizeChanged {
                omChangeTerminalState(
                    currentState.copy(
                        terminalWidth = it.width.toFloat(),
                        terminalHeight = it.height.toFloat()
                    )
                )
            },
    ) {
        translate(left = currentState.scrolledBy) {
            currentState.bars.forEachIndexed { index, bar ->
                val xOffset = size.width - index * currentState.widthBar
                drawLine(
                    color = Color.White,
                    start = Offset(
                        xOffset,
                        size.height - ((bar.low - currentState.minLow) * currentState.pxPerPoint)
                    ),
                    end = Offset(
                        xOffset,
                        size.height - ((bar.high - currentState.minLow) * currentState.pxPerPoint)
                    ),
                    strokeWidth = 1f
                )
                val nextBar = if (index < currentState.bars.size - 1) currentState.bars[index + 1] else null

                drawLegendData(
                    bar = bar,
                    nextBar = nextBar,
                    timeframe = timeframe,
                    offsetX = xOffset,
                    textMeasurer = textMeasurer
                )

                drawLine(
                    color = if (bar.open < bar.close) Color.Green else Color.Red,
                    start = Offset(
                        xOffset,
                        size.height - ((bar.open - currentState.minLow) * currentState.pxPerPoint)
                    ),
                    end = Offset(
                        xOffset,
                        size.height - ((bar.close - currentState.minLow) * currentState.pxPerPoint)
                    ),
                    strokeWidth = currentState.widthBar / 2
                )

            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawLegendData(
    bar: Bar,
    nextBar: Bar?,
    timeframe: TimeFrame,
    offsetX: Float,
    textMeasurer: TextMeasurer
) {
    val isDrawingLegendDate = isDrawLegendDate(
        timeframe, bar, nextBar
    )

    if (!isDrawingLegendDate) return

    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )

    val text = getTextLegendDate(timeframe, bar)
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
    )
}

private fun getTextLegendDate(timeframe: TimeFrame, bar: Bar): String {
    val calendar = bar.time
    return when (timeframe) {
        TimeFrame.MIN_5, TimeFrame.MIN_15 -> {
            val hour = calendar.get(Calendar.HOUR)
            String.format("%02d:00", hour)
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            String.format("%s %s", day, month)
        }
    }
}

private fun isDrawLegendDate(timeframe: TimeFrame, bar: Bar, nextBar: Bar?): Boolean {
    val currentCalendar = bar.time
    val minute = currentCalendar.get(Calendar.MINUTE)
    val hour = currentCalendar.get(Calendar.HOUR)
    val day = currentCalendar.get(Calendar.DAY_OF_MONTH)
    return when (timeframe) {
        TimeFrame.MIN_5 -> {
            minute == 0
        }

        TimeFrame.MIN_15 -> {
            minute == 0 && hour % 2 == 0
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            if (nextBar == null) return false
            val nexDay = nextBar.time.get(Calendar.DAY_OF_MONTH)
            day != nexDay
        }
    }
}


@OptIn(ExperimentalTextApi::class)
@Composable
private fun LegendPrices(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    lastPrice: Float,
) {
    val currentState = terminalState.value
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(vertical = 32.dp),
    ) {

        drawLegendPrices(
            max = currentState.maxHigh,
            min = currentState.minLow,
            lastPrice = lastPrice,
            pxPerPoint = currentState.pxPerPoint,
            textMeasurer = textMeasurer
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawLegendPrices(
    max: Float,
    min: Float,
    lastPrice: Float,
    pxPerPoint: Float,
    textMeasurer: TextMeasurer,
) {

    val maxOffSetY = 0f
    drawDottedLine(
        start = Offset(0f, maxOffSetY),
        end = Offset(size.width, maxOffSetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxOffSetY
    )

    val lastOffsetY = size.height - (lastPrice - min) * pxPerPoint
    drawDottedLine(
        start = Offset(0f, lastOffsetY),
        end = Offset(size.width, lastOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastOffsetY
    )

    val minOffsetY = size.height
    drawDottedLine(
        start = Offset(0f, minOffsetY),
        end = Offset(size.width, minOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = minOffsetY
    )
}

private fun DrawScope.drawDottedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}