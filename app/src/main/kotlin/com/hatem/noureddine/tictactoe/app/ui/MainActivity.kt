package com.hatem.noureddine.tictactoe.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hatem.noureddine.tictactoe.app.ui.theme.TicTacToeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity entry point for the application.
 *
 * Responsible for setting up the Android content view, enabling edge-to-edge display,
 * and initializing the Hilt dependency injection graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    GameScreen()
                }
            }
        }
    }
}
