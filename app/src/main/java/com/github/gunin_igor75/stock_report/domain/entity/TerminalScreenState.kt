package com.github.gunin_igor75.stock_report.domain.entity

sealed class TerminalScreenState {

    object Initial: TerminalScreenState()
    object Loading: TerminalScreenState()
    data class Content(
        val bars: List<Bar>,
        val timeFrame: TimeFrame
        ): TerminalScreenState()
}