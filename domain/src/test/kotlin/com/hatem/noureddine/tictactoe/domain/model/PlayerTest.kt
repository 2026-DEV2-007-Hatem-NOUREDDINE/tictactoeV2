package com.hatem.noureddine.tictactoe.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * TDD-style tests for [Player] enum class.
 *
 * ## TDD Design Journey
 *
 * The Player enum was created as part of the Game class TDD process:
 * 1. 🔴 RED: Test required `game.currentPlayer` to return a `Player`
 * 2. 🟢 GREEN: Created `enum class Player { X, O }`
 * 3. 🔵 REFACTOR: Enum is simple, no refactoring needed
 *
 * ## Purpose
 *
 * The Player enum represents the two players in Tic-Tac-Toe:
 * - Player.X: Always goes first (game rule #1)
 * - Player.O: Plays second, alternates with X
 */
@DisplayName("Player Enum")
class PlayerTest {
    // =========================================================================
    // Enum Structure
    // =========================================================================
    // 🔴 RED: Need exactly 2 players: X and O
    // 🟢 GREEN: enum class Player { X, O }
    // 🔵 REFACTOR: Simple structure, no changes needed
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Enum Structure")
    inner class EnumStructure {
        @Test
        fun `Player enum has exactly two values`() {
            // Given: The Player enum
            val players = Player.entries

            // Then: There should be exactly 2 players
            assertEquals(2, players.size)
        }

        @Test
        fun `Player values are X and O in order`() {
            // Given: The Player enum values
            val values = Player.entries

            // Then: X comes first (rule #1: X always goes first)
            assertEquals(Player.X, values[0])
            assertEquals(Player.O, values[1])
        }
    }

    // =========================================================================
    // Player X
    // =========================================================================
    // 🔴 RED: Tests require Player.X to exist with correct properties
    // 🟢 GREEN: X is the first enum constant
    // 🔵 REFACTOR: None needed - enum provides name and ordinal automatically
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Player X")
    inner class PlayerXTests {
        @Test
        fun `Player X has correct name`() {
            // Given/When: Accessing X's name
            // Then: Name is "X"
            assertEquals("X", Player.X.name)
        }

        @Test
        fun `Player X has ordinal 0 - first player`() {
            // Given/When: Accessing X's ordinal
            // Then: Ordinal is 0 (first in enum)
            assertEquals(0, Player.X.ordinal)
        }

        @Test
        fun `Player valueOf X returns Player X`() {
            // Given: String "X"
            // When: Looking up by name
            val player = Player.valueOf("X")

            // Then: Returns Player.X
            assertEquals(Player.X, player)
        }
    }

    // =========================================================================
    // Player O
    // =========================================================================
    // 🔴 RED: Tests require Player.O to exist with correct properties
    // 🟢 GREEN: O is the second enum constant
    // 🔵 REFACTOR: None needed - mirrors X structure
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Player O")
    inner class PlayerOTests {
        @Test
        fun `Player O has correct name`() {
            // Given/When: Accessing O's name
            // Then: Name is "O"
            assertEquals("O", Player.O.name)
        }

        @Test
        fun `Player O has ordinal 1 - second player`() {
            // Given/When: Accessing O's ordinal
            // Then: Ordinal is 1 (second in enum)
            assertEquals(1, Player.O.ordinal)
        }

        @Test
        fun `Player valueOf O returns Player O`() {
            // Given: String "O"
            // When: Looking up by name
            val player = Player.valueOf("O")

            // Then: Returns Player.O
            assertEquals(Player.O, player)
        }
    }
}
