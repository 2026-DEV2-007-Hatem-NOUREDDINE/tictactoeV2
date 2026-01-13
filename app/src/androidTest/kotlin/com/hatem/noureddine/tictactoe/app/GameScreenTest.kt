package com.hatem.noureddine.tictactoe.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hatem.noureddine.tictactoe.R
import com.hatem.noureddine.tictactoe.app.ui.GameScreenContent
import com.hatem.noureddine.tictactoe.app.ui.viewmodel.BoardUiState
import com.hatem.noureddine.tictactoe.domain.model.Player
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class GameScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // =========================================================================
    // Initial State Tests
    // =========================================================================
    @Test
    fun gameScreen_verifiesInitialState() {
        val initialState =
            BoardUiState(
                boardSize = 3,
                board =
                    persistentListOf(
                        persistentListOf(null, null, null),
                        persistentListOf(null, null, null),
                        persistentListOf(null, null, null),
                    ),
                currentPlayer = Player.X,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = initialState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }
        // Then
        composeTestRule.onNodeWithText("Current Player:").assertIsDisplayed()
        // Player X is shown as an Icon with content description "X" (from Player.name)
        composeTestRule.onNodeWithContentDescription("X").assertIsDisplayed()
        // Check for empty cells (content description)
        composeTestRule.onNodeWithContentDescription("Empty cell at row 1, column 1").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysAllNineCells() {
        val initialState = BoardUiState()

        composeTestRule.setContent {
            GameScreenContent(
                uiState = initialState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        // Verify all 9 cells are displayed
        for (row in 1..3) {
            for (col in 1..3) {
                composeTestRule
                    .onNodeWithContentDescription(
                        "Empty cell at row $row, column $col",
                    ).assertIsDisplayed()
            }
        }
    }

    // =========================================================================
    // Cell Interaction Tests
    // =========================================================================
    @Test
    fun gameScreen_verifiesCellClick() {
        var clickedRow = -1
        var clickedCol = -1
        val initialState = BoardUiState()

        composeTestRule.setContent {
            GameScreenContent(
                uiState = initialState,
                onPlay = { r, c ->
                    clickedRow = r
                    clickedCol = c
                },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Empty cell at row 1, column 1").performClick()

        assert(clickedRow == 0)
        assert(clickedCol == 0)
    }

    @Test
    fun gameScreen_verifiesDifferentCellClicks() {
        var clickedRow = -1
        var clickedCol = -1
        val initialState = BoardUiState()

        composeTestRule.setContent {
            GameScreenContent(
                uiState = initialState,
                onPlay = { r, c ->
                    clickedRow = r
                    clickedCol = c
                },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        // Click center cell
        composeTestRule.onNodeWithContentDescription("Empty cell at row 2, column 2").performClick()
        assert(clickedRow == 1 && clickedCol == 1)

        // Click bottom-right cell
        composeTestRule.onNodeWithContentDescription("Empty cell at row 3, column 3").performClick()
        assert(clickedRow == 2 && clickedCol == 2)
    }

    // =========================================================================
    // Game Over Tests
    // =========================================================================
    @Test
    fun gameScreen_whenGameIsOver_clickingCellShowsError() {
        val winnerState = BoardUiState(winner = Player.X, errorMessageId = R.string.error_game_over)

        composeTestRule.setContent {
            GameScreenContent(
                uiState = winnerState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Empty cell at row 1, column 1").performClick()
        composeTestRule.onNodeWithText("Game is over").assertIsDisplayed()
    }

    @Test
    fun gameScreen_whenDraw_clickingCellShowsError() {
        val drawState = BoardUiState(isDraw = true, errorMessageId = R.string.error_game_over)

        composeTestRule.setContent {
            GameScreenContent(
                uiState = drawState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Empty cell at row 1, column 1").performClick()
        composeTestRule.onNodeWithText("Game is over").assertIsDisplayed()
    }

    // =========================================================================
    // Winner Display Tests
    // =========================================================================
    @Test
    fun gameScreen_verifiesWinnerX() {
        val winnerState =
            BoardUiState(
                winner = Player.X,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = winnerState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        // Check for "Winner:" label
        composeTestRule.onNodeWithText("Winner:").assertIsDisplayed()
        // Check for the icon description "Winner: O" (Player.name is used in GameStatus)
        composeTestRule.onNodeWithContentDescription("Winner: X").assertIsDisplayed()
    }

    @Test
    fun gameScreen_verifiesWinnerO() {
        val winnerState =
            BoardUiState(
                winner = Player.O,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = winnerState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        // Check for "Winner:" label
        composeTestRule.onNodeWithText("Winner:").assertIsDisplayed()
        // Check for the icon description "Winner: O" (Player.name is used in GameStatus)
        composeTestRule.onNodeWithContentDescription("Winner: O").assertIsDisplayed()
    }

    @Test
    fun gameScreen_verifiesDraw() {
        val drawState =
            BoardUiState(
                isDraw = true,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = drawState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithText("Draw!").assertIsDisplayed()
    }

    // =========================================================================
    // In Progress Game Tests
    // =========================================================================
    @Test
    fun gameScreen_verifiesInProgress() {
        val inProgressState =
            BoardUiState(
                board =
                    persistentListOf(
                        persistentListOf(Player.X, Player.O, null),
                        persistentListOf(null, Player.X, null),
                        persistentListOf(null, null, Player.O),
                    ),
                currentPlayer = Player.X,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = inProgressState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithText("Current Player:").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("X").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysOTurn() {
        val oTurnState =
            BoardUiState(
                board =
                    persistentListOf(
                        persistentListOf(Player.X, null, null),
                        persistentListOf(null, null, null),
                        persistentListOf(null, null, null),
                    ),
                currentPlayer = Player.O,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = oTurnState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithText("Current Player:").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("O").assertIsDisplayed()
    }

    // =========================================================================
    // Control Button Tests
    // =========================================================================
    @Test
    fun gameScreen_verifiesReset() {
        var resetCalled = false
        val uiState = BoardUiState()

        composeTestRule.setContent {
            GameScreenContent(
                uiState = uiState,
                onPlay = { _, _ -> },
                onReset = { resetCalled = true },
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithText("Restart Game").performClick()
        assert(resetCalled)
    }

    @Test
    fun gameScreen_restartButtonIsAlwaysDisplayed() {
        val uiState = BoardUiState()

        composeTestRule.setContent {
            GameScreenContent(
                uiState = uiState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithText("Restart Game").assertIsDisplayed()
    }

    // =========================================================================
    // Error Display Tests
    // =========================================================================
    @Test
    fun gameScreen_verifiesError() {
        val errorState =
            BoardUiState(
                errorMessageId = R.string.error_unknown,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = errorState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithText("Unknown error").assertIsDisplayed()
    }

    @Test
    fun gameScreen_clearsErrorAfterDisplay() {
        var errorCleared = false
        val errorState =
            BoardUiState(
                errorMessageId = R.string.error_game_over,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = errorState,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = { errorCleared = true },
            )
        }

        // Wait for snackbar animation
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1000)

        assert(errorCleared)
    }

    // =========================================================================
    // Board Size Tests
    // =========================================================================
    @Test
    fun gameScreen_displays4x4Board() {
        val largeBoard =
            BoardUiState(
                boardSize = 4,
                board =
                    persistentListOf(
                        persistentListOf(null, null, null, null),
                        persistentListOf(null, null, null, null),
                        persistentListOf(null, null, null, null),
                        persistentListOf(null, null, null, null),
                    ),
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = largeBoard,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        // Verify cell at row 4, column 4 exists
        composeTestRule.onNodeWithContentDescription("Empty cell at row 4, column 4").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displays5x5Board() {
        val largeBoard =
            BoardUiState(
                boardSize = 5,
                board =
                    persistentListOf(
                        persistentListOf(null, null, null, null, null),
                        persistentListOf(null, null, null, null, null),
                        persistentListOf(null, null, null, null, null),
                        persistentListOf(null, null, null, null, null),
                        persistentListOf(null, null, null, null, null),
                    ),
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = largeBoard,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        // Verify cell at row 5, column 5 exists
        composeTestRule.onNodeWithContentDescription("Empty cell at row 5, column 5").assertIsDisplayed()
    }

    // =========================================================================
    // Occupied Cell Display Tests
    // =========================================================================
    @Test
    fun gameScreen_displaysXInCell() {
        val stateWithX =
            BoardUiState(
                board =
                    persistentListOf(
                        persistentListOf(Player.X, null, null),
                        persistentListOf(null, null, null),
                        persistentListOf(null, null, null),
                    ),
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = stateWithX,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("X at row 1, column 1").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysOInCell() {
        val stateWithO =
            BoardUiState(
                board =
                    persistentListOf(
                        persistentListOf(null, Player.O, null),
                        persistentListOf(null, null, null),
                        persistentListOf(null, null, null),
                    ),
                currentPlayer = Player.X,
            )

        composeTestRule.setContent {
            GameScreenContent(
                uiState = stateWithO,
                onPlay = { _, _ -> },
                onReset = {},
                onBoardSizeChange = {},
                onClearError = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("O at row 1, column 2").assertIsDisplayed()
    }
}
