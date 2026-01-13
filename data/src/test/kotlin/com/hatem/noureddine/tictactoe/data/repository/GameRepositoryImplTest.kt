package com.hatem.noureddine.tictactoe.data.repository

import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * TDD-style tests for [GameRepositoryImpl].
 *
 * ## TDD Design Journey
 *
 * ### Step 1: Game Access
 * 1. 🔴 RED: Tests required access to current Game instance
 * 2. 🟢 GREEN: Added `game` property with lazy initialization
 * 3. 🔵 REFACTOR: Made it a var with backing property
 *
 * ### Step 2: Game Updates
 * 1. 🔴 RED: Tests required ability to replace the game (reset, restore)
 * 2. 🟢 GREEN: Added `updateGame(newGame: Game)` method
 * 3. 🔵 REFACTOR: Simple assignment - repository is stateless container
 *
 * ## Architecture Role
 *
 * GameRepositoryImpl sits in the :data module and:
 * - Implements the GameRepository interface from :domain
 * - Holds the single source of truth for game state
 * - Could be extended for persistence (Room, DataStore, etc.)
 */
@DisplayName("GameRepositoryImpl")
class GameRepositoryImplTest {
    private lateinit var repository: GameRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = GameRepositoryImpl()
    }

    // =========================================================================
    // Initial State
    // =========================================================================
    // 🔴 RED: Tests required repository to provide a valid Game on creation
    // 🟢 GREEN: Initialize game property with Game()
    // 🔵 REFACTOR: Default 3x3 game matches standard Tic-Tac-Toe
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Initial State")
    inner class InitialState {
        @Test
        fun `game property returns a non-null game instance`() {
            // Given: A new repository

            // When: Accessing the game
            val game = repository.game

            // Then: Game is not null
            assertNotNull(game)
        }

        @Test
        fun `initial game has default size of 3`() {
            // Given: A new repository

            // When: Checking game size
            val size = repository.game.size

            // Then: Size is 3 (standard Tic-Tac-Toe)
            assertEquals(3, size)
        }

        @Test
        fun `initial game starts with Player X`() {
            // Given: A new repository

            // Then: X is current player (game rule #1)
            assertEquals(Player.X, repository.game.currentPlayer)
        }

        @Test
        fun `initial game has no winner`() {
            // Given: A new repository

            // Then: No winner at start
            assertNull(repository.game.winner)
        }
    }

    // =========================================================================
    // Update Game
    // =========================================================================
    // 🔴 RED: Tests required ability to replace the game instance
    // 🟢 GREEN: Simple assignment in updateGame()
    // 🔵 REFACTOR: No validation needed - Game class handles its own invariants
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("updateGame")
    inner class UpdateGame {
        @Test
        fun `updateGame replaces the current game`() {
            // Given: A new game with different size
            val newGame = Game(size = 4)

            // When: updateGame is called
            repository.updateGame(newGame)

            // Then: The game property returns the new game
            assertSame(newGame, repository.game)
            assertEquals(4, repository.game.size)
        }

        @Test
        fun `updateGame allows resetting to fresh game`() {
            // Given: A game with some moves
            repository.game.play(0, 0)
            val freshGame = Game()

            // When: Updating with fresh game
            repository.updateGame(freshGame)

            // Then: Repository contains fresh game (empty board)
            assertNull(repository.game.getCell(0, 0))
        }

        @Test
        fun `updateGame preserves game state from argument`() {
            // Given: A game with X at center
            val gameWithMove = Game().apply { play(1, 1) }

            // When: Updating
            repository.updateGame(gameWithMove)

            // Then: Move is preserved
            assertEquals(Player.X, repository.game.getCell(1, 1))
        }
    }

    // =========================================================================
    // State Persistence
    // =========================================================================
    // 🔴 RED: Tests required moves to persist when accessed again
    // 🟢 GREEN: Same object reference returned each time
    // 🔵 REFACTOR: In-memory only for now - could add Room/DataStore later
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("State Persistence")
    inner class StatePersistence {
        @Test
        fun `moves are persisted in the game`() {
            // Given: The initial game
            val game = repository.game

            // When: Making a move
            game.play(0, 0)

            // Then: The move is persisted (same reference)
            assertEquals(Player.X, repository.game.getCell(0, 0))
        }

        @Test
        fun `game reference remains consistent until updated`() {
            // Given: The initial game
            val firstAccess = repository.game

            // When: Accessing again
            val secondAccess = repository.game

            // Then: Same instance (no recreation)
            assertSame(firstAccess, secondAccess)
        }

        @Test
        fun `updateGame changes the reference`() {
            // Given: The initial game
            val initialGame = repository.game
            val newGame = Game(size = 5)

            // When: Updating
            repository.updateGame(newGame)

            // Then: Reference changes
            assertNotSame(initialGame, repository.game)
        }
    }

    // =========================================================================
    // Real-World Scenarios
    // =========================================================================
    // 🔴 RED: Tests for complete game flows
    // 🟢 GREEN: Repository correctly holds state through game lifecycle
    // 🔵 REFACTOR: Added tests for reset use case
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Real-World Scenarios")
    inner class RealWorldScenarios {
        @Test
        fun `game state survives multiple moves`() {
            // Given: A fresh repository
            val game = repository.game

            // When: Playing multiple moves
            game.play(0, 0) // X
            game.play(1, 1) // O
            game.play(0, 1) // X

            // Then: All moves are tracked
            assertEquals(Player.X, repository.game.getCell(0, 0))
            assertEquals(Player.O, repository.game.getCell(1, 1))
            assertEquals(Player.X, repository.game.getCell(0, 1))
        }

        @Test
        fun `updateGame can be used to implement reset`() {
            // Given: A game with some moves
            repository.game.play(0, 0)
            repository.game.play(1, 1)

            // When: "Resetting" by updating with new game
            repository.updateGame(Game())

            // Then: State is reset
            assertNull(repository.game.getCell(0, 0))
            assertEquals(Player.X, repository.game.currentPlayer)
        }

        @Test
        fun `winner is preserved in game state`() {
            // Given: A game that results in a win
            val game = repository.game
            game.play(0, 0) // X
            game.play(1, 0) // O
            game.play(0, 1) // X
            game.play(1, 1) // O
            game.play(0, 2) // X wins

            // Then: Winner is accessible via repository
            assertEquals(Player.X, repository.game.winner)
        }
    }
}
