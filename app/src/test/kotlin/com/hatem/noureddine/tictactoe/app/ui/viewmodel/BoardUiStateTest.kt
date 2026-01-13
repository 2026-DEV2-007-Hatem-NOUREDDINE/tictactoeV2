package com.hatem.noureddine.tictactoe.app.ui.viewmodel

import com.hatem.noureddine.tictactoe.domain.model.Player
import kotlinx.collections.immutable.persistentListOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * TDD-style tests for [BoardUiState] data class.
 *
 * ## TDD Design Journey
 *
 * ### Step 1: Basic UI State
 * 1. 🔴 RED: Compose UI needs state holder for board display
 * 2. 🟢 GREEN: Created data class with board, currentPlayer, winner, isDraw
 * 3. 🔵 REFACTOR: Made board use ImmutableList for Compose stability
 *
 * ### Step 2: Error Handling
 * 1. 🔴 RED: UI needs to show error messages
 * 2. 🟢 GREEN: Added errorMessageId: Int? for string resource reference
 * 3. 🔵 REFACTOR: Nullable to allow clearing errors
 *
 * ### Step 3: Dynamic Board Size
 * 1. 🔴 RED: Support for different board sizes (4x4, 5x5)
 * 2. 🟢 GREEN: Added boardSize property and dynamic board generation
 * 3. 🔵 REFACTOR: Default board generation uses boardSize
 *
 * ## Purpose
 *
 * BoardUiState separates UI concerns from domain logic:
 * - Immutable for Compose recomposition optimization
 * - Contains only display-ready data
 * - No business logic - pure data holder
 */
@DisplayName("BoardUiState")
class BoardUiStateTest {
    // =========================================================================
    // Default Values
    // =========================================================================
    // 🔴 RED: Tests required BoardUiState() to work without arguments
    // 🟢 GREEN: Added default values matching a new game
    // 🔵 REFACTOR: Default board is 3x3 grid of nulls
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Default Values")
    inner class DefaultValues {
        @Test
        fun `default boardSize is 3`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: Default 3x3 board
            assertEquals(3, state.boardSize)
        }

        @Test
        fun `default currentPlayer is X`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: X goes first (game rule #1)
            assertEquals(Player.X, state.currentPlayer)
        }

        @Test
        fun `default winner is null`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: No winner at start
            assertNull(state.winner)
        }

        @Test
        fun `default isDraw is false`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: Not a draw at start
            assertFalse(state.isDraw)
        }

        @Test
        fun `default errorMessageId is null`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: No error at start
            assertNull(state.errorMessageId)
        }

        @Test
        fun `default board is 3x3 grid`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: Board is 3x3
            assertEquals(3, state.board.size)
            assertEquals(3, state.board[0].size)
        }

        @Test
        fun `default board is all nulls - empty cells`() {
            // Given/When: Creating state with no arguments
            val state = BoardUiState()

            // Then: All cells are empty (null)
            for (row in state.board) {
                for (cell in row) {
                    assertNull(cell)
                }
            }
        }
    }

    // =========================================================================
    // Custom Values
    // =========================================================================
    // 🔴 RED: Tests required custom values to be preserved
    // 🟢 GREEN: Data class preserves all constructor params
    // 🔵 REFACTOR: Used ImmutableList for board (Compose optimization)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Custom Values")
    inner class CustomValues {
        @Test
        fun `custom board is preserved`() {
            // Given: A custom board
            val customBoard =
                persistentListOf(
                    persistentListOf(Player.X, null),
                    persistentListOf(null, Player.O),
                )

            // When: Creating state with custom board
            val state =
                BoardUiState(
                    boardSize = 2,
                    board = customBoard,
                )

            // Then: Board is preserved
            assertEquals(customBoard, state.board)
        }

        @Test
        fun `custom winner is preserved`() {
            // Given/When: State with X as winner
            val state = BoardUiState(winner = Player.X)

            // Then: Winner is X
            assertEquals(Player.X, state.winner)
        }

        @Test
        fun `custom isDraw is preserved`() {
            // Given/When: State with isDraw = true
            val state = BoardUiState(isDraw = true)

            // Then: Is a draw
            assertTrue(state.isDraw)
        }

        @Test
        fun `custom errorMessageId is preserved`() {
            // Given/When: State with error message
            val state = BoardUiState(errorMessageId = 123)

            // Then: Error ID is preserved
            assertEquals(123, state.errorMessageId)
        }

        @Test
        fun `all custom values are preserved together`() {
            // Given: Multiple custom values
            val customBoard =
                persistentListOf(
                    persistentListOf(Player.X, null),
                    persistentListOf(null, Player.O),
                )
            val state =
                BoardUiState(
                    boardSize = 2,
                    board = customBoard,
                    currentPlayer = Player.O,
                    winner = Player.X,
                    isDraw = false,
                    errorMessageId = 456,
                )

            // Then: All values are preserved
            assertEquals(2, state.boardSize)
            assertEquals(customBoard, state.board)
            assertEquals(Player.O, state.currentPlayer)
            assertEquals(Player.X, state.winner)
            assertFalse(state.isDraw)
            assertEquals(456, state.errorMessageId)
        }
    }

    // =========================================================================
    // Data Class Features
    // =========================================================================
    // 🔴 RED: Tests required copy, equals, hashCode for state management
    // 🟢 GREEN: Kotlin data class provides these automatically
    // 🔵 REFACTOR: ViewModel uses copy() for state updates
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Data Class Features")
    inner class DataClassFeatures {
        @Test
        fun `copy updates only specified properties`() {
            // Given: Original state
            val original = BoardUiState()

            // When: Copying with only winner changed
            val copied = original.copy(winner = Player.O)

            // Then: Only winner changed, others preserved
            assertEquals(Player.O, copied.winner)
            assertEquals(original.currentPlayer, copied.currentPlayer)
            assertEquals(original.boardSize, copied.boardSize)
        }

        @Test
        fun `equals returns true for identical states`() {
            // Given: Two states with same values
            val state1 = BoardUiState(currentPlayer = Player.X)
            val state2 = BoardUiState(currentPlayer = Player.X)

            // Then: They are equal
            assertEquals(state1, state2)
        }

        @Test
        fun `hashCode is consistent for equal states`() {
            // Given: Two equal states
            val state1 = BoardUiState()
            val state2 = BoardUiState()

            // Then: Hash codes are equal
            assertEquals(state1.hashCode(), state2.hashCode())
        }
    }

    // =========================================================================
    // UI Scenarios
    // =========================================================================
    // 🔴 RED: Tests for UI display scenarios
    // 🟢 GREEN: State correctly represents all game outcomes
    // 🔵 REFACTOR: Added tests for error display scenario
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("UI Scenarios")
    inner class UIScenarios {
        @Test
        fun `state can represent game in progress`() {
            // Given: Mid-game state
            val board =
                persistentListOf(
                    persistentListOf(Player.X, null, null),
                    persistentListOf(null, Player.O, null),
                    persistentListOf(null, null, null),
                )
            val state =
                BoardUiState(
                    board = board,
                    currentPlayer = Player.X,
                    winner = null,
                    isDraw = false,
                )

            // Then: Game is in progress
            assertNull(state.winner)
            assertFalse(state.isDraw)
        }

        @Test
        fun `state can represent won game`() {
            // Given: Won game state
            val state =
                BoardUiState(
                    winner = Player.X,
                    isDraw = false,
                )

            // Then: X has won
            assertEquals(Player.X, state.winner)
        }

        @Test
        fun `state can represent drawn game`() {
            // Given: Drawn game state
            val state =
                BoardUiState(
                    winner = null,
                    isDraw = true,
                )

            // Then: Game is a draw
            assertTrue(state.isDraw)
            assertNull(state.winner)
        }

        @Test
        fun `state can represent error condition`() {
            // Given: State with error
            val state = BoardUiState(errorMessageId = 999)

            // Then: Error is set
            assertEquals(999, state.errorMessageId)
        }
    }
}
