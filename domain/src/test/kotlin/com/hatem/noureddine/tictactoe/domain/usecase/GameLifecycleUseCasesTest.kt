package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.GameState
import com.hatem.noureddine.tictactoe.domain.model.Player
import com.hatem.noureddine.tictactoe.domain.repository.FakeGameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * TDD-style tests for game lifecycle UseCases.
 *
 * ## TDD Design Journey
 *
 * These UseCases were created as the app needed more game management features:
 *
 * ### GetGameUseCase
 * 1. 🔴 RED: ViewModel needs access to current Game
 * 2. 🟢 GREEN: UseCase returns repository.game
 * 3. 🔵 REFACTOR: Operator fun invoke() for clean syntax
 *
 * ### ResetGameUseCase
 * 1. 🔴 RED: User should be able to start new game
 * 2. 🟢 GREEN: UseCase creates new Game and updates repository
 * 3. 🔵 REFACTOR: Added size parameter for different board sizes
 *
 * ### LoadGameUseCase
 * 1. 🔴 RED: App should restore saved game on launch
 * 2. 🟢 GREEN: UseCase calls game.restore(state)
 * 3. 🔵 REFACTOR: Handle null state gracefully (no-op)
 *
 * ### GetSnapshotUseCase
 * 1. 🔴 RED: App needs to save game state
 * 2. 🟢 GREEN: UseCase returns game.getSnapshot()
 * 3. 🔵 REFACTOR: Used by ViewModel after each move
 */
@DisplayName("Game Lifecycle UseCases")
class GameLifecycleUseCasesTest {
    // =========================================================================
    // GetGameUseCase
    // =========================================================================
    // 🔴 RED: Tests required ViewModel to access the current Game
    // 🟢 GREEN: Simple pass-through to repository.game
    // 🔵 REFACTOR: operator fun invoke() for cleaner syntax
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GetGameUseCase")
    inner class GetGameUseCaseTests {
        @Test
        fun `invoke returns the current game from repository`() {
            // Given: A repository with a game
            val repository = FakeGameRepository()
            val getGameUseCase = GetGameUseCase(repository)

            // When: Getting the game
            val game = getGameUseCase()

            // Then: Returns the same game instance
            assertSame(repository.game, game)
        }

        @Test
        fun `invoke returns updated game after moves`() {
            // Given: A repository and use cases
            val repository = FakeGameRepository()
            val getGameUseCase = GetGameUseCase(repository)
            val playTurnUseCase = PlayTurnUseCase(repository)

            // When: A move is made
            playTurnUseCase(0, 0)

            // Then: getGame returns the updated game
            val game = getGameUseCase()
            assertEquals(Player.X, game.getCell(0, 0))
        }
    }

    // =========================================================================
    // ResetGameUseCase
    // =========================================================================
    // 🔴 RED: Tests required ability to start fresh game
    // 🟢 GREEN: Creates new Game() and calls repository.updateGame()
    // 🔵 REFACTOR: Added size parameter with default value 3
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("ResetGameUseCase")
    inner class ResetGameUseCaseTests {
        @Test
        fun `invoke with default size creates new 3x3 game`() {
            // Given: A repository with an existing game
            val repository = FakeGameRepository()
            val resetGameUseCase = ResetGameUseCase(repository)
            val oldGame = repository.game

            // When: Resetting without size parameter
            resetGameUseCase()

            // Then: A new 3x3 game is created
            val newGame = repository.game
            assertNotSame(oldGame, newGame)
            assertEquals(3, newGame.size)
        }

        @Test
        fun `invoke with custom size creates game of that size`() {
            // Given: A repository
            val repository = FakeGameRepository()
            val resetGameUseCase = ResetGameUseCase(repository)

            // When: Resetting with size 5
            resetGameUseCase(5)

            // Then: A 5x5 game is created
            assertEquals(5, repository.game.size)
        }

        @Test
        fun `invoke clears any previous game state`() {
            // Given: A game with some moves
            val repository = FakeGameRepository()
            repository.game.play(0, 0)
            val resetGameUseCase = ResetGameUseCase(repository)

            // When: Resetting
            resetGameUseCase()

            // Then: New game has fresh state
            assertNull(repository.game.getCell(0, 0))
            assertEquals(Player.X, repository.game.currentPlayer)
        }
    }

    // =========================================================================
    // LoadGameUseCase
    // =========================================================================
    // 🔴 RED: Tests required restoring game from saved state
    // 🟢 GREEN: Calls game.restore(state) if state is not null
    // 🔵 REFACTOR: Guard clause for null state (no-op)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("LoadGameUseCase")
    inner class LoadGameUseCaseTests {
        @Test
        fun `invoke with valid state restores the game`() {
            // Given: A saved game state
            val repository = FakeGameRepository()
            val loadGameUseCase = LoadGameUseCase(repository)
            val savedState =
                GameState(
                    board = mapOf("0,0" to Player.X, "1,1" to Player.O),
                    currentPlayer = Player.X,
                    winner = null,
                    isDraw = false,
                    size = 3,
                )

            // When: Loading the saved state
            loadGameUseCase(savedState)

            // Then: Game is restored
            assertEquals(Player.X, repository.game.getCell(0, 0))
            assertEquals(Player.O, repository.game.getCell(1, 1))
            assertEquals(Player.X, repository.game.currentPlayer)
        }

        @Test
        fun `invoke with null state does nothing`() {
            // Given: An existing game
            val repository = FakeGameRepository()
            val loadGameUseCase = LoadGameUseCase(repository)
            val initialGame = repository.game

            // When: Loading null state
            loadGameUseCase(null)

            // Then: Game remains unchanged
            assertSame(initialGame, repository.game)
        }

        @Test
        fun `invoke with won game state preserves winner`() {
            // Given: A saved state with a winner
            val repository = FakeGameRepository()
            val loadGameUseCase = LoadGameUseCase(repository)
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
                    size = 3,
                )

            // When: Loading the won state
            loadGameUseCase(wonState)

            // Then: Winner is preserved
            assertEquals(Player.X, repository.game.winner)
        }
    }

    // =========================================================================
    // GetSnapshotUseCase
    // =========================================================================
    // 🔴 RED: Tests required capturing game state for persistence
    // 🟢 GREEN: Returns game.getSnapshot()
    // 🔵 REFACTOR: Called by ViewModel after each move for SavedStateHandle
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("GetSnapshotUseCase")
    inner class GetSnapshotUseCaseTests {
        @Test
        fun `invoke captures current game state`() {
            // Given: A game with some moves
            val repository = FakeGameRepository()
            repository.game.play(0, 0) // X
            repository.game.play(1, 1) // O
            val getSnapshotUseCase = GetSnapshotUseCase(repository)

            // When: Taking a snapshot
            val snapshot = getSnapshotUseCase()

            // Then: Snapshot contains the current state
            assertEquals(Player.X, snapshot.board["0,0"])
            assertEquals(Player.O, snapshot.board["1,1"])
            assertEquals(Player.X, snapshot.currentPlayer)
        }

        @Test
        fun `invoke captures winner state`() {
            // Given: A won game
            val repository = FakeGameRepository()
            repository.game.apply {
                play(0, 0)
                play(1, 0)
                play(0, 1)
                play(1, 1)
                play(0, 2) // X wins
            }
            val getSnapshotUseCase = GetSnapshotUseCase(repository)

            // When: Taking a snapshot
            val snapshot = getSnapshotUseCase()

            // Then: Winner is captured
            assertEquals(Player.X, snapshot.winner)
        }

        @Test
        fun `invoke captures board size`() {
            // Given: A 4x4 game
            val repository = FakeGameRepository(initialSize = 4)
            val getSnapshotUseCase = GetSnapshotUseCase(repository)

            // When: Taking a snapshot
            val snapshot = getSnapshotUseCase()

            // Then: Size is captured
            assertEquals(4, snapshot.size)
        }
    }
}
