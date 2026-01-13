package com.hatem.noureddine.tictactoe.app.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.hatem.noureddine.tictactoe.app.ui.theme.TicTacToeTheme
import com.hatem.noureddine.tictactoe.app.ui.viewmodel.BoardUiState
import com.hatem.noureddine.tictactoe.domain.model.Player
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w411dp-h915dp-420dpi")
class GameScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInitialScreen() {
        composeTestRule.setContent {
            TicTacToeTheme {
                GameScreenContent(
                    uiState = BoardUiState(),
                    onPlay = { _, _ -> },
                    onReset = {},
                    onBoardSizeChange = {},
                    onClearError = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun testPlayerXWins() {
        val winningBoard =
            List(3) { r ->
                List(3) { c ->
                    if (r == 0) Player.X else null // X wins top row
                }.toImmutableList()
            }.toImmutableList()

        composeTestRule.setContent {
            TicTacToeTheme {
                GameScreenContent(
                    uiState =
                        BoardUiState(
                            board = winningBoard,
                            winner = Player.X,
                            currentPlayer = Player.O,
                        ),
                    onPlay = { _, _ -> },
                    onReset = {},
                    onBoardSizeChange = {},
                    onClearError = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun testDraw() {
        // Create a draw board
        // X O X
        // X O X
        // O X O
        val drawBoard =
            listOf(
                listOf(Player.X, Player.O, Player.X).toImmutableList(),
                listOf(Player.X, Player.O, Player.X).toImmutableList(),
                listOf(Player.O, Player.X, Player.O).toImmutableList(),
            ).toImmutableList()

        composeTestRule.setContent {
            TicTacToeTheme {
                GameScreenContent(
                    uiState =
                        BoardUiState(
                            board = drawBoard,
                            isDraw = true,
                            currentPlayer = Player.X,
                        ),
                    onPlay = { _, _ -> },
                    onReset = {},
                    onBoardSizeChange = {},
                    onClearError = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun test4x4Grid() {
        // Initial 4x4 Board
        val board = List(4) { List(4) { null as Player? }.toImmutableList() }.toImmutableList()
        composeTestRule.setContent {
            TicTacToeTheme {
                GameScreenContent(
                    uiState = BoardUiState(board = board, boardSize = 4),
                    onPlay = { _, _ -> },
                    onReset = {},
                    onBoardSizeChange = {},
                    onClearError = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun test5x5Grid() {
        // Initial 5x5 Board
        val board = List(5) { List(5) { null as Player? }.toImmutableList() }.toImmutableList()
        composeTestRule.setContent {
            TicTacToeTheme {
                GameScreenContent(
                    uiState = BoardUiState(board = board, boardSize = 5),
                    onPlay = { _, _ -> },
                    onReset = {},
                    onBoardSizeChange = {},
                    onClearError = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Suppress("CascadingCallWrapping")
    @Test
    fun testFullGameFlow() {
        // ... (existing code)
        // Simulate a full game
        val moves =
            listOf(
                Pair(0, 0),
                // X
                Pair(0, 1),
                // O
                Pair(1, 0),
                // X
                Pair(1, 1),
                // O
                Pair(2, 0),
                // X Wins
            )

        val initialBoard = List(3) { List(3) { null as Player? }.toImmutableList() }.toImmutableList()
        val uiState =
            mutableStateOf(
                BoardUiState(
                    board = initialBoard,
                    currentPlayer = Player.X,
                    winner = null,
                ),
            )

        composeTestRule.setContent {
            TicTacToeTheme {
                GameScreenContent(
                    uiState = uiState.value,
                    onPlay = { _, _ -> },
                    onReset = {},
                    onBoardSizeChange = {},
                    onClearError = {},
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("../snapshots/roborazzi/game_flow_00_initial.png")

        var currentBoard = initialBoard
        var currentPlayer = Player.X

        moves.forEachIndexed { index, (row, col) ->
            // Update Board
            currentBoard =
                currentBoard
                    .mapIndexed { r, rowList ->
                        if (r == row) {
                            rowList
                                .mapIndexed { c, cell ->
                                    if (c == col) currentPlayer else cell
                                }.toImmutableList()
                        } else {
                            rowList
                        }
                    }.toImmutableList()

            var winner: Player? = null
            if (index == 4) { // Last move
                winner = Player.X
            }

            // Switch Player
            currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X

            // Update State to trigger recomposition
            uiState.value =
                BoardUiState(
                    board = currentBoard,
                    currentPlayer = currentPlayer,
                    winner = winner,
                )

            composeTestRule.waitForIdle()
            composeTestRule.onRoot().captureRoboImage(
                filePath = "../snapshots/roborazzi/game_flow_0${index + 1}_move_${row}_$col.png",
            )
        }
    }
}
