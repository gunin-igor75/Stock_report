package com.github.gunin_igor75.stock_report.domain.entity

sealed class TerminalScreenState {

    object Initial: TerminalScreenState()
    data class Content(val bars: List<Bar>): TerminalScreenState()
}