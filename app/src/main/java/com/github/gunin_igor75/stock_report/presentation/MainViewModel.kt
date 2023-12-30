package com.github.gunin_igor75.stock_report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.gunin_igor75.stock_report.domain.entity.TerminalScreenState
import com.github.gunin_igor75.stock_report.domain.entity.TimeFrame
import com.github.gunin_igor75.stock_report.domain.usecase.LoadBarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val barsLoadBarsUseCase: LoadBarsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TerminalScreenState>(TerminalScreenState.Initial)
    val state = _state.asStateFlow()
    private val exceptionHandler = CoroutineExceptionHandler { _, _: Throwable ->
        _state.value = lastState
    }
    private var lastState: TerminalScreenState = TerminalScreenState.Initial

    init {
        loadBars()
    }

    fun loadBars(timeFrame: TimeFrame = TimeFrame.HOUR_1) {
        lastState = _state.value
        _state.value = TerminalScreenState.Loading
        viewModelScope.launch(exceptionHandler) {
            val bars = barsLoadBarsUseCase(timeFrame)
            _state.value = TerminalScreenState.Content(bars, timeFrame)
        }
    }
}