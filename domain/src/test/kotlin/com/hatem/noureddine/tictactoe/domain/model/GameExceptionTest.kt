package com.hatem.noureddine.tictactoe.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * TDD-style tests for [GameException] sealed class hierarchy.
 *
 * ## TDD Design Journey
 *
 * GameException was created incrementally as game rules required validation:
 *
 * ### Step 1: PositionTaken
 * 1. 🔴 RED: Test "playing on occupied cell should throw exception"
 * 2. 🟢 GREEN: Created `sealed class GameException` with `PositionTaken` subclass
 * 3. 🔵 REFACTOR: Made it a sealed class for exhaustive when expressions
 *
 * ### Step 2: InvalidPosition
 * 1. 🔴 RED: Test "playing outside board should throw exception"
 * 2. 🟢 GREEN: Added `InvalidPosition` subclass
 * 3. 🔵 REFACTOR: Consistent naming convention
 *
 * ### Step 3: GameOver
 * 1. 🔴 RED: Test "playing after win should throw exception"
 * 2. 🟢 GREEN: Added `GameOver` subclass
 * 3. 🔵 REFACTOR: All exceptions follow same pattern
 *
 * ## Benefits of Sealed Class
 *
 * - Type-safe error handling
 * - Exhaustive when expressions (compiler checks all cases)
 * - Clear hierarchy visible in code
 */
@DisplayName("GameException Sealed Class")
class GameExceptionTest {
    // =========================================================================
    // Exception Creation
    // =========================================================================
    // 🔴 RED: Tests required each exception type to be instantiable
    // 🟢 GREEN: Created classes extending sealed class GameException
    // 🔵 REFACTOR: Made them data objects for simplicity (no state needed)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Exception Creation")
    inner class ExceptionCreation {
        @Test
        fun `GameOver can be created`() {
            // Given/When: Creating GameOver exception
            val exception = GameException.GameOver()

            // Then: Exception is not null
            assertNotNull(exception)
        }

        @Test
        fun `PositionTaken can be created`() {
            // Given/When: Creating PositionTaken exception
            val exception = GameException.PositionTaken()

            // Then: Exception is not null
            assertNotNull(exception)
        }

        @Test
        fun `InvalidPosition can be created`() {
            // Given/When: Creating InvalidPosition exception
            val exception = GameException.InvalidPosition()

            // Then: Exception is not null
            assertNotNull(exception)
        }
    }

    // =========================================================================
    // Exhaustive When Expression
    // =========================================================================
    // 🔴 RED: Tests required ability to handle all exception types
    // 🟢 GREEN: Sealed class enables exhaustive when expressions
    // 🔵 REFACTOR: Compiler now enforces handling all cases
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Exhaustive When Expression")
    inner class ExhaustiveWhen {
        @Test
        fun `all subtypes can be matched exhaustively - no else needed`() {
            // Given: List of all exception types
            val exceptions =
                listOf(
                    GameException.GameOver(),
                    GameException.PositionTaken(),
                    GameException.InvalidPosition(),
                )

            // When: Matching with when expression (no else branch needed!)
            val results =
                exceptions.map { exception ->
                    when (exception) {
                        is GameException.GameOver -> "GameOver"
                        is GameException.PositionTaken -> "PositionTaken"
                        is GameException.InvalidPosition -> "InvalidPosition"
                    }
                }

            // Then: All types are matched
            assertEquals(
                listOf("GameOver", "PositionTaken", "InvalidPosition"),
                results,
            )
        }
    }

    // =========================================================================
    // Game Integration
    // =========================================================================
    // 🔴 RED: Tests verified Game throws correct exceptions
    // 🟢 GREEN: Game.play() throws appropriate GameException subtypes
    // 🔵 REFACTOR: Guard clauses in Game.validateMove() check all conditions
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Game Integration")
    inner class GameIntegration {
        @Test
        fun `Game throws PositionTaken for occupied cell`() {
            // Given: A game where (0,0) is already played
            val game = Game()
            game.play(0, 0)

            // When/Then: Playing same cell throws PositionTaken
            assertThrows<GameException.PositionTaken> {
                game.play(0, 0)
            }
        }

        @Test
        fun `Game throws InvalidPosition for out of bounds`() {
            // Given: A game with 3x3 board
            val game = Game()

            // When/Then: Playing outside bounds throws InvalidPosition
            assertThrows<GameException.InvalidPosition> {
                game.play(10, 10)
            }
        }

        @Test
        fun `Game throws GameOver after win`() {
            // Given: A won game (X wins top row)
            val game = Game()
            game.play(0, 0)
            game.play(1, 0)
            game.play(0, 1)
            game.play(1, 1)
            game.play(0, 2) // X wins

            // When/Then: Any further move throws GameOver
            assertThrows<GameException.GameOver> {
                game.play(2, 0)
            }
        }

        @Test
        fun `Game throws GameOver after draw`() {
            // Given: A drawn game
            val game = Game()
            game.play(0, 0)
            game.play(1, 1)
            game.play(0, 1)
            game.play(0, 2)
            game.play(2, 0)
            game.play(1, 0)
            game.play(1, 2)
            game.play(2, 1)
            game.play(2, 2) // Draw

            // When/Then: Any move throws GameOver
            assertThrows<GameException.GameOver> {
                game.play(0, 0)
            }
        }
    }
}
