package com.hatem.noureddine.tictactoe.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * TDD-style tests for [GameState] data class.
 *
 * ## TDD Design Journey
 *
 * GameState was created when we needed to persist/restore game state:
 * 1. 🔴 RED: Tests required `game.getSnapshot()` to return serializable state
 * 2. 🟢 GREEN: Created `data class GameState(board, currentPlayer, winner, isDraw, size)`
 * 3. 🔵 REFACTOR: Made all fields have sensible defaults for easy construction
 *
 * ## Purpose
 *
 * GameState is an immutable snapshot that can be:
 * - Serialized for persistence (SavedStateHandle, SharedPreferences)
 * - Used to restore a game via `game.restore(state)`
 * - Transferred between layers without exposing mutable Game internals
 */
@DisplayName("GameState Data Class")
class GameStateTest {
    // =========================================================================
    // Default Values
    // =========================================================================
    // 🔴 RED: Need GameState() to work without arguments
    // 🟢 GREEN: Added default values to all constructor parameters
    // 🔵 REFACTOR: Ensured defaults match a "new game" state
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Default Values")
    inner class DefaultValues {
        @Test
        fun `default state has empty board`() {
            // Given/When: Creating state with no arguments
            val state = GameState()

            // Then: Board is empty
            assertEquals(emptyMap<String, Player?>(), state.board)
        }

        @Test
        fun `default state starts with Player X`() {
            // Given/When: Creating state with no arguments
            val state = GameState()

            // Then: X goes first (game rule #1)
            assertEquals(Player.X, state.currentPlayer)
        }

        @Test
        fun `default state has no winner`() {
            // Given/When: Creating state with no arguments
            val state = GameState()

            // Then: No winner at game start
            assertNull(state.winner)
        }

        @Test
        fun `default state is not a draw`() {
            // Given/When: Creating state with no arguments
            val state = GameState()

            // Then: Not a draw at game start
            assertFalse(state.isDraw)
        }

        @Test
        fun `default state has size 3`() {
            // Given/When: Creating state with no arguments
            val state = GameState()

            // Then: Standard 3x3 board
            assertEquals(3, state.size)
        }
    }

    // =========================================================================
    // Custom Values
    // =========================================================================
    // 🔴 RED: Tests required GameState to preserve custom values
    // 🟢 GREEN: Data class automatically preserves all constructor params
    // 🔵 REFACTOR: Kotlin data class provides this out of the box
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Custom Values")
    inner class CustomValues {
        @Test
        fun `custom board is preserved`() {
            // Given: A custom board configuration
            val board = mapOf("0,0" to Player.X, "1,1" to Player.O)

            // When: Creating state with custom board
            val state = GameState(board = board)

            // Then: Board is preserved exactly
            assertEquals(board, state.board)
        }

        @Test
        fun `custom currentPlayer is preserved`() {
            // Given/When: Creating state with O as current player
            val state = GameState(currentPlayer = Player.O)

            // Then: O is current player
            assertEquals(Player.O, state.currentPlayer)
        }

        @Test
        fun `custom winner is preserved`() {
            // Given/When: Creating state with X as winner
            val state = GameState(winner = Player.X)

            // Then: X is winner
            assertEquals(Player.X, state.winner)
        }

        @Test
        fun `custom isDraw is preserved`() {
            // Given/When: Creating state with isDraw = true
            val state = GameState(isDraw = true)

            // Then: Is a draw
            assertTrue(state.isDraw)
        }

        @Test
        fun `custom size is preserved`() {
            // Given/When: Creating state with 5x5 board
            val state = GameState(size = 5)

            // Then: Size is 5
            assertEquals(5, state.size)
        }
    }

    // =========================================================================
    // Data Class Features
    // =========================================================================
    // 🔴 RED: Tests required equals/hashCode/copy/toString
    // 🟢 GREEN: Kotlin data class generates these automatically
    // 🔵 REFACTOR: No custom implementation needed
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Data Class Features")
    inner class DataClassFeatures {
        @Test
        fun `equals returns true for identical states`() {
            // Given: Two states with same values
            val state1 =
                GameState(
                    board = mapOf("0,0" to Player.X),
                    currentPlayer = Player.O,
                )
            val state2 =
                GameState(
                    board = mapOf("0,0" to Player.X),
                    currentPlayer = Player.O,
                )

            // Then: They are equal
            assertEquals(state1, state2)
        }

        @Test
        fun `copy preserves unchanged values`() {
            // Given: An original state
            val original =
                GameState(
                    board = mapOf("0,0" to Player.X),
                    currentPlayer = Player.O,
                )

            // When: Copying with only winner changed
            val copied = original.copy(winner = Player.X)

            // Then: Other values are preserved
            assertEquals(Player.X, copied.winner)
            assertEquals(original.board, copied.board)
            assertEquals(original.currentPlayer, copied.currentPlayer)
        }

        @Test
        fun `hashCode is consistent for equal states`() {
            // Given: Two equal states
            val state1 = GameState(board = mapOf("0,0" to Player.X))
            val state2 = GameState(board = mapOf("0,0" to Player.X))

            // Then: Hash codes are equal
            assertEquals(state1.hashCode(), state2.hashCode())
        }

        @Test
        fun `toString contains relevant info`() {
            // Given: A state
            val state =
                GameState(
                    board = mapOf("0,0" to Player.X),
                    currentPlayer = Player.O,
                )

            // When: Converting to string
            val str = state.toString()

            // Then: Contains class name and key properties
            assertTrue(str.contains("GameState"))
            assertTrue(str.contains("currentPlayer"))
        }
    }

    // =========================================================================
    // Real-World Scenarios
    // =========================================================================
    // 🔴 RED: Tests for won and drawn game states
    // 🟢 GREEN: GameState can represent any game outcome
    // 🔵 REFACTOR: Added convenience for testing all game end states
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Real-World Scenarios")
    inner class RealWorldScenarios {
        @Test
        fun `state can represent a won game`() {
            // Given: A won game state (X wins with top row)
            val wonState =
                GameState(
                    board =
                        mapOf(
                            "0,0" to Player.X,
                            "0,1" to Player.X,
                            "0,2" to Player.X,
                            "1,0" to Player.O,
                            "1,1" to Player.O,
                        ),
                    currentPlayer = Player.O,
                    winner = Player.X,
                    isDraw = false,
                )

            // Then: Winner is set, not a draw
            assertEquals(Player.X, wonState.winner)
            assertFalse(wonState.isDraw)
        }

        @Test
        fun `state can represent a drawn game`() {
            // Given: A drawn game state (full board, no winner)
            val drawnState =
                GameState(
                    board =
                        mapOf(
                            "0,0" to Player.X,
                            "0,1" to Player.O,
                            "0,2" to Player.X,
                            "1,0" to Player.X,
                            "1,1" to Player.O,
                            "1,2" to Player.O,
                            "2,0" to Player.O,
                            "2,1" to Player.X,
                            "2,2" to Player.X,
                        ),
                    currentPlayer = Player.X,
                    winner = null,
                    isDraw = true,
                )

            // Then: No winner, is a draw
            assertNull(drawnState.winner)
            assertTrue(drawnState.isDraw)
        }
    }
}
