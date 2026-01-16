package com.hatem.noureddine.tictactoe.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hatem.noureddine.tictactoe.R
import com.hatem.noureddine.tictactoe.app.ui.components.Board
import com.hatem.noureddine.tictactoe.app.ui.components.GameControls
import com.hatem.noureddine.tictactoe.app.ui.components.GameStatus
import com.hatem.noureddine.tictactoe.app.ui.viewmodel.GameViewModel

/**
 * The main screen of the Tic Tac Toe application.
 *
 * This stateful composable holds the [GameViewModel] and manages the UI state flow.
 * It acts as the entry point for the game UI, hoisting state to [GameScreenContent].
 *
 * @param modifier The modifier to be applied to the layout.
 * @param viewModel The ViewModel that holds the game logic and state.
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreenContent(
        uiState = uiState,
        onPlay = { row, col -> viewModel.play(row, col) },
        onReset = { viewModel.reset(uiState.boardSize) },
        onBoardSizeChange = { size -> viewModel.reset(size) },
        onClearError = { viewModel.clearError() },
        modifier = modifier,
    )
}

/**
 * The stateless content composable for the Game Screen.
 *
 * This UI component displays the game status, controls, and the board itself.
 * It is decoupled from the ViewModel to facilitate easy testing and previewing.
 *
 * @param uiState The current state of the game board and player status.
 * @param onPlay Callback invoked when a cell is clicked (row, col).
 * @param onReset Callback invoked when the reset game button is clicked.
 * @param onBoardSizeChange Callback invoked when the board size is changed.
 * @param onClearError Callback invoked to clear any transient error messages.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun GameScreenContent(
    uiState: com.hatem.noureddine.tictactoe.app.ui.viewmodel.BoardUiState,
    onPlay: (Int, Int) -> Unit,
    onReset: () -> Unit,
    onBoardSizeChange: (Int) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessageId = uiState.errorMessageId

    val shakeOffset = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current

    // Effect to show snackbar when error changes
    if (errorMessageId != null) {
        val message = stringResource(id = errorMessageId)
        val isGameOver = errorMessageId == R.string.error_game_over

        LaunchedEffect(errorMessageId) {
            if (isGameOver) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                shakeOffset.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        keyframes {
                            durationMillis = 400
                            0f at 0
                            (-20f) at 50
                            20f at 100
                            (-20f) at 150
                            20f at 200
                            (-10f) at 250
                            10f at 300
                            0f at 400
                        },
                )
            }
            snackbarHostState.showSnackbar(message)
            onClearError()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                androidx.compose.material3.Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape =
                        androidx.compose.foundation.shape
                            .RoundedCornerShape(16.dp),
                ) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Rounded.Warning,
                            contentDescription = null,
                        )
                        androidx.compose.material3.Text(text = data.visuals.message)
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            GameStatus(
                winner = uiState.winner,
                isDraw = uiState.isDraw,
                currentPlayer = uiState.currentPlayer,
            )

            GameControls(
                boardSize = uiState.boardSize,
                onBoardSizeChange = onBoardSizeChange,
                onReset = onReset,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Board(
                boardSize = uiState.boardSize,
                board = uiState.board,
                onCellClick = onPlay,
                isDraw = uiState.isDraw,
                modifier =
                    Modifier.offset {
                        IntOffset(
                            x = shakeOffset.value.dp.roundToPx(),
                            y = 0,
                        )
                    },
            )
        }
    }
}

/**
 * Preview for [GameScreenContent] showing a sample initial state.
 */
@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    MaterialTheme {
        GameScreenContent(
            uiState =
                com.hatem.noureddine.tictactoe.app.ui.viewmodel
                    .BoardUiState(),
            onPlay = { _, _ -> },
            onReset = {},
            onBoardSizeChange = {},
            onClearError = {},
        )
    }
}
