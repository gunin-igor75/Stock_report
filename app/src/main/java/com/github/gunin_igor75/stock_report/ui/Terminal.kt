package com.github.gunin_igor75.stock_report.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.gunin_igor75.stock_report.domain.entity.TerminalScreenState
import com.github.gunin_igor75.stock_report.domain.entity.TerminalState
import com.github.gunin_igor75.stock_report.domain.entity.rememberTerminalState
import com.github.gunin_igor75.stock_report.presentation.MainViewModel
import kotlin.math.roundToInt

private const val MIN_VISIBLE__COUNT_BAR = 20


@Composable
fun Terminal(
    modifier: Modifier = Modifier,
) {
    val viewModel: MainViewModel =  hiltViewModel()
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val terminalState= rememberTerminalState(currentState.bars)

                StockChart(
                    modifier = modifier,
                    terminalState = terminalState,
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
            }
        }
    }


}

@Composable
private fun StockChart(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
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