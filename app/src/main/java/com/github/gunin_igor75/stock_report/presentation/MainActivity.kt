package com.github.gunin_igor75.stock_report.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.github.gunin_igor75.stock_report.domain.entity.TerminalScreenState
import com.github.gunin_igor75.stock_report.ui.Terminal
import com.github.gunin_igor75.stock_report.ui.theme.Stock_reportTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Stock_reportTheme {
                val screenState = viewModel.state.collectAsState()
                when(val currentState = screenState.value){
                    is TerminalScreenState.Content -> {
                        Terminal(bars = currentState.bars)
                    }
                    is TerminalScreenState.Initial ->{}
                }

            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

