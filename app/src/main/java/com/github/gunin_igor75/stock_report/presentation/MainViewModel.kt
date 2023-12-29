package com.github.gunin_igor75.stock_report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.gunin_igor75.stock_report.domain.entity.TerminalScreenState
import com.github.gunin_igor75.stock_report.domain.usecase.LoadBarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        loadBars()
    }

    private fun loadBars() {
        _state.value = TerminalScreenState.Loading
        viewModelScope.launch {
            val bars = barsLoadBarsUseCase()
            _state.value = TerminalScreenState.Content(bars)
        }
    }
}