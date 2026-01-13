package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.GameException
import com.hatem.noureddine.tictactoe.domain.model.Player
import com.hatem.noureddine.tictactoe.domain.repository.FakeGameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * TDD-style tests for [PlayTurnUseCase].
 *
 * ## TDD Design Journey
 *
 * ### Step 1: Basic Move Execution
 * 1. 🔴 RED: Test "invoke should call game.play(row, col)"
 * 2. 🟢 GREEN: Created UseCase that calls repository.game.play(row, col)
 * 3. 🔵 REFACTOR: Made constructor injection for repository
 *
 * ### Step 2: Error Propagation
 * 1. 🔴 RED: Test "invoke throws when game throws"
 * 2. 🟢 GREEN: Exceptions propagate naturally (no try-catch needed)
 * 3. 🔵 REFACTOR: UseCase is thin wrapper - no business logic
 *
 * ## Purpose
 *
 * PlayTurnUseCase bridges the presentation layer with domain logic:
 * - Encapsulates the "play a move" action
 * - Allows ViewModels to not depend directly on Repository
 * - Enables easy mocking in ViewModel tests
 */
@DisplayName("PlayTurnUseCase")
class PlayTurnUseCaseTest {
    // =========================================================================
    // Valid Moves
    // =========================================================================
    // 🔴 RED: Tests required invoke(row, col) to execute a game move
    // 🟢 GREEN: UseCase calls repository.game.play(row, col)
    // 🔵 REFACTOR: Single-line implementation - simple and clean
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Valid Moves")
    inner class ValidMoves {
        @Test
        fun `invoke executes move and switches current player`() {
            // Given: A fresh game and use case
            val repository = FakeGameRepository()
            val playTurnUseCase = PlayTurnUseCase(repository)

            // When: X plays at (0,0)
            playTurnUseCase(0, 0)

            // Then: Current player should switch to O
            assertSame(Player.O, repository.game.currentPlayer)
        }

        @Test
        fun `invoke records the move on the board`() {
            // Given: A fresh game and use case
            val repository = FakeGameRepository()
            val playTurnUseCase = PlayTurnUseCase(repository)

            // When: X plays at (1,1)
            playTurnUseCase(1, 1)

            // Then: Cell (1,1) should contain X
            assertEquals(Player.X, repository.game.getCell(1, 1))
        }

        @Test
        fun `invoke allows multiple valid moves in sequence`() {
            // Given: A game with use case
            val repository = FakeGameRepository()
            val playTurnUseCase = PlayTurnUseCase(repository)

            // When: X and O alternate moves
            playTurnUseCase(0, 0) // X
            playTurnUseCase(1, 1) // O
            playTurnUseCase(0, 1) // X

            // Then: All moves are recorded correctly
            assertEquals(Player.X, repository.game.getCell(0, 0))
            assertEquals(Player.O, repository.game.getCell(1, 1))
            assertEquals(Player.X, repository.game.getCell(0, 1))
            assertEquals(Player.O, repository.game.currentPlayer)
        }
    }

    // =========================================================================
    // Invalid Moves
    // =========================================================================
    // 🔴 RED: Tests required exceptions to propagate from Game
    // 🟢 GREEN: No try-catch in UseCase - exceptions bubble up
    // 🔵 REFACTOR: Let callers decide how to handle exceptions
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Invalid Moves")
    inner class InvalidMoves {
        @Test
        fun `invoke throws PositionTaken when cell is occupied`() {
            // Given: A game where (0,0) is already played
            val repository = FakeGameRepository()
            val playTurnUseCase = PlayTurnUseCase(repository)
            playTurnUseCase(0, 0) // X plays first

            // When/Then: O tries to play on same cell → exception
            assertThrows<GameException.PositionTaken> {
                playTurnUseCase(0, 0)
            }
        }

        @Test
        fun `invoke throws InvalidPosition for out of bounds`() {
            // Given: A game with use case
            val repository = FakeGameRepository()
            val playTurnUseCase = PlayTurnUseCase(repository)

            // When/Then: Playing outside board → exception
            assertThrows<GameException.InvalidPosition> {
                playTurnUseCase(-1, 0)
            }
        }

        @Test
        fun `invoke throws GameOver when game has winner`() {
            // Given: A won game
            val repository = FakeGameRepository()
            val playTurnUseCase = PlayTurnUseCase(repository)

            // X wins with top row
            playTurnUseCase(0, 0) // X
            playTurnUseCase(1, 0) // O
            playTurnUseCase(0, 1) // X
            playTurnUseCase(1, 1) // O
            playTurnUseCase(0, 2) // X wins!

            // When/Then: Any move after win → exception
            assertThrows<GameException.GameOver> {
                playTurnUseCase(2, 0)
            }
        }
    }
}
