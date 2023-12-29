package com.github.gunin_igor75.stock_report.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.gunin_igor75.stock_report.ui.Terminal
import com.github.gunin_igor75.stock_report.ui.theme.Stock_reportTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Stock_reportTheme {
                Terminal()
            }
        }
    }
}