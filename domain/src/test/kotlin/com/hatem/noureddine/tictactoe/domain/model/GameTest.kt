package com.hatem.noureddine.tictactoe.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * TDD-style tests for Tic-Tac-Toe game.
 *
 * These tests are organized by the official game rules and follow the TDD cycle:
 * 🔴 RED → 🟢 GREEN → 🔵 REFACTOR
 *
 * Each @Nested class corresponds to a specific rule, making these tests
 * serve as **executable documentation** of the game's behavior.
 *
 * ## Game Rules (specification):
 * 1. X always goes first
 * 2. Players cannot play on a played position
 * 3. Players alternate placing X's and O's on the board
 * 4. Game ends when one player has three in a row (H, V, D) or all squares filled
 * 5. Three in a row = win
 * 6. All nine squares filled with no winner = draw
 */
@DisplayName("Tic-Tac-Toe Game Rules")
class GameTest {
    // =========================================================================
    // RULE 1: X always goes first
    // =========================================================================
    // 🔴 RED: We need a Game class where X starts
    // 🟢 GREEN: Create Game with currentPlayer = Player.X
    // 🔵 REFACTOR: No refactoring needed
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Rule 1: X always goes first")
    inner class XAlwaysGoesFirst {
        @Test
        fun `new game should start with Player X`() {
            // Given: A new game is created
            val game = Game()

            // Then: X should be the current player
            assertEquals(Player.X, game.currentPlayer)
        }

        @Test
        fun `first move should place X on the board`() {
            // Given: A new game
            val game = Game()

            // When: The first move is played
            game.play(0, 0)

            // Then: X should be at that position
            assertEquals(Player.X, game.getCell(0, 0))
        }
    }

    // =========================================================================
    // RULE 2: Players cannot play on a played position
    // =========================================================================
    // 🔴 RED: Playing on occupied cell should fail
    // 🟢 GREEN: Add guard clause checking if cell is null
    // 🔵 REFACTOR: Extract to separate exception class
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Rule 2: Players cannot play on a played position")
    inner class CannotPlayOnPlayedPosition {
        @Test
        fun `playing on occupied cell should throw PositionTaken`() {
            // Given: A game where X has played at (0,0)
            val game = Game()
            game.play(0, 0)

            // When/Then: O tries to play at the same position → exception
            assertThrows<GameException.PositionTaken> {
                game.play(0, 0)
            }
        }

        @Test
        fun `playing on cell occupied by opponent should throw PositionTaken`() {
            // Given: A game with X at center and O at corner
            val game = Game()
            game.play(1, 1) // X plays center
            game.play(0, 0) // O plays corner

            // When/Then: X tries to play on O's position → exception
            assertThrows<GameException.PositionTaken> {
                game.play(0, 0)
            }
        }

        @Test
        fun `playing outside board boundaries should throw InvalidPosition`() {
            // Given: A new game
            val game = Game()

            // When/Then: Playing outside bounds → exception
            assertThrows<GameException.InvalidPosition> { game.play(-1, 0) }
            assertThrows<GameException.InvalidPosition> { game.play(0, -1) }
            assertThrows<GameException.InvalidPosition> { game.play(3, 0) }
            assertThrows<GameException.InvalidPosition> { game.play(0, 3) }
        }
    }

    // =========================================================================
    // RULE 3: Players alternate placing X's and O's on the board
    // =========================================================================
    // 🔴 RED: After X plays, currentPlayer should be O
    // 🟢 GREEN: Switch currentPlayer after each move
    // 🔵 REFACTOR: Use if/else or when expression
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Rule 3: Players alternate placing X's and O's")
    inner class PlayersAlternate {
        @Test
        fun `after X plays, it should be O's turn`() {
            // Given: A new game where X is current
            val game = Game()

            // When: X makes a move
            game.play(0, 0)

            // Then: O should be the current player
            assertEquals(Player.O, game.currentPlayer)
        }

        @Test
        fun `after O plays, it should be X's turn`() {
            // Given: A game where X has played
            val game = Game()
            game.play(0, 0) // X

            // When: O makes a move
            game.play(1, 1) // O

            // Then: X should be the current player again
            assertEquals(Player.X, game.currentPlayer)
        }

        @Test
        fun `players should continue alternating throughout the game`() {
            // Given: A new game
            val game = Game()

            // When/Then: Players alternate after each move
            game.play(0, 0) // X plays
            assertEquals(Player.O, game.currentPlayer)

            game.play(0, 1) // O plays
            assertEquals(Player.X, game.currentPlayer)

            game.play(0, 2) // X plays
            assertEquals(Player.O, game.currentPlayer)

            game.play(1, 0) // O plays
            assertEquals(Player.X, game.currentPlayer)
        }
    }

    // =========================================================================
    // RULE 4: Three in a row ends the game (horizontal, vertical, diagonal)
    // =========================================================================
    // 🔴 RED: Winning move should set winner
    // 🟢 GREEN: Check all win conditions after each move
    // 🔵 REFACTOR: Extract checkWin logic to separate method
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Rule 4: Three in a row ends the game")
    inner class ThreeInARowEndsGame {
        @Nested
        @DisplayName("Horizontal wins")
        inner class HorizontalWins {
            @Test
            fun `X wins with three in top row`() {
                // Given: A game in progress
                val game = Game()

                // When: X completes the top row
                game.play(0, 0) // X
                game.play(1, 0) // O
                game.play(0, 1) // X
                game.play(1, 1) // O
                game.play(0, 2) // X wins!

                // Then: X should be the winner
                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `X wins with three in middle row`() {
                val game = Game()
                game.play(1, 0) // X
                game.play(0, 0) // O
                game.play(1, 1) // X
                game.play(0, 1) // O
                game.play(1, 2) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `X wins with three in bottom row`() {
                val game = Game()
                game.play(2, 0) // X
                game.play(0, 0) // O
                game.play(2, 1) // X
                game.play(0, 1) // O
                game.play(2, 2) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `O wins with three in a row`() {
                val game = Game()
                game.play(0, 0) // X
                game.play(1, 0) // O
                game.play(0, 1) // X
                game.play(1, 1) // O
                game.play(2, 2) // X (not winning)
                game.play(1, 2) // O wins middle row!

                assertEquals(Player.O, game.winner)
            }
        }

        @Nested
        @DisplayName("Vertical wins")
        inner class VerticalWins {
            @Test
            fun `X wins with three in first column`() {
                val game = Game()
                game.play(0, 0) // X
                game.play(0, 1) // O
                game.play(1, 0) // X
                game.play(1, 1) // O
                game.play(2, 0) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `X wins with three in middle column`() {
                val game = Game()
                game.play(0, 1) // X
                game.play(0, 0) // O
                game.play(1, 1) // X
                game.play(1, 0) // O
                game.play(2, 1) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `X wins with three in last column`() {
                val game = Game()
                game.play(0, 2) // X
                game.play(0, 0) // O
                game.play(1, 2) // X
                game.play(1, 0) // O
                game.play(2, 2) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `O wins with three in a column`() {
                val game = Game()
                game.play(0, 0) // X
                game.play(0, 1) // O
                game.play(1, 0) // X
                game.play(1, 1) // O
                game.play(2, 2) // X (not winning)
                game.play(2, 1) // O wins middle column!

                assertEquals(Player.O, game.winner)
            }
        }

        @Nested
        @DisplayName("Diagonal wins")
        inner class DiagonalWins {
            @Test
            fun `X wins with three in main diagonal`() {
                val game = Game()
                game.play(0, 0) // X
                game.play(0, 1) // O
                game.play(1, 1) // X
                game.play(0, 2) // O
                game.play(2, 2) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `X wins with three in anti-diagonal`() {
                val game = Game()
                game.play(0, 2) // X
                game.play(0, 0) // O
                game.play(1, 1) // X
                game.play(0, 1) // O
                game.play(2, 0) // X wins!

                assertEquals(Player.X, game.winner)
            }

            @Test
            fun `O wins with diagonal`() {
                val game = Game()
                game.play(0, 1) // X
                game.play(0, 0) // O
                game.play(1, 0) // X
                game.play(1, 1) // O
                game.play(2, 1) // X
                game.play(2, 2) // O wins main diagonal!

                assertEquals(Player.O, game.winner)
            }
        }

        @Nested
        @DisplayName("Game Over - no more moves allowed")
        inner class GameCannotContinueAfterWin {
            @Test
            fun `playing after win should throw GameOver`() {
                // Given: X has won the game
                val game = Game()
                game.play(0, 0) // X
                game.play(1, 0) // O
                game.play(0, 1) // X
                game.play(1, 1) // O
                game.play(0, 2) // X wins!

                // When/Then: Any further move should throw GameOver
                assertThrows<GameException.GameOver> {
                    game.play(2, 0)
                }
            }
        }
    }

    // =========================================================================
    // RULE 5: Three in a row = win (explicit win state)
    // =========================================================================
    // 🔴 RED: Winner property should be set, isDraw should be false
    // 🟢 GREEN: Set winner when checkWin returns true
    // 🔵 REFACTOR: Ensure mutual exclusivity between win and draw
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Rule 5: Three in a row = win")
    inner class ThreeInARowIsWin {
        @Test
        fun `game should have winner when three in a row`() {
            // Given: A game where X completes a row
            val game = Game()
            game.play(0, 0) // X
            game.play(1, 0) // O
            game.play(0, 1) // X
            game.play(1, 1) // O
            game.play(0, 2) // X wins!

            // Then: X is the winner and it's not a draw
            assertEquals(Player.X, game.winner)
            assertFalse(game.isDraw)
        }

        @Test
        fun `game should not be a draw when there is a winner`() {
            // Given: A game where X has won
            val game = Game()
            game.play(0, 0) // X
            game.play(1, 0) // O
            game.play(0, 1) // X
            game.play(1, 1) // O
            game.play(0, 2) // X wins!

            // Then: isDraw must be false
            assertFalse(game.isDraw)
        }
    }

    // =========================================================================
    // RULE 6: All nine squares filled with no winner = draw
    // =========================================================================
    // 🔴 RED: isDraw should be true when board is full without winner
    // 🟢 GREEN: Track move count and check for draw condition
    // 🔵 REFACTOR: Use size * size for dynamic board support
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Rule 6: All squares filled without winner = draw")
    inner class DrawWhenBoardFull {
        @Test
        fun `game should be a draw when board is full with no winner`() {
            // Given: A game that will end in a draw
            val game = Game()

            // When: All 9 moves are played without a winner
            // Board state:
            // X | X | O
            // O | O | X
            // X | O | X
            game.play(0, 0) // X
            game.play(1, 1) // O
            game.play(0, 1) // X
            game.play(0, 2) // O
            game.play(2, 0) // X
            game.play(1, 0) // O
            game.play(1, 2) // X
            game.play(2, 1) // O
            game.play(2, 2) // X - Draw!

            // Then: It should be a draw with no winner
            assertTrue(game.isDraw)
            assertNull(game.winner)
        }

        @Test
        fun `game cannot continue after draw`() {
            // Given: A drawn game
            val game = Game()
            game.play(0, 0) // X
            game.play(1, 1) // O
            game.play(0, 1) // X
            game.play(0, 2) // O
            game.play(2, 0) // X
            game.play(1, 0) // O
            game.play(1, 2) // X
            game.play(2, 1) // O
            game.play(2, 2) // Draw!

            // When/Then: Any move should throw GameOver
            assertThrows<GameException.GameOver> {
                game.play(0, 0)
            }
        }
    }

    // =========================================================================
    // ADDITIONAL: Snapshot and Restore (for state persistence)
    // =========================================================================
    @Nested
    @DisplayName("Snapshot and Restore")
    inner class SnapshotAndRestore {
        @Test
        fun `getSnapshot should capture current board state`() {
            // Given: A game with some moves
            val game = Game()
            game.play(0, 0) // X
            game.play(1, 1) // O

            // When: Taking a snapshot
            val snapshot = game.getSnapshot()

            // Then: Snapshot should contain the board state
            assertEquals(Player.X, snapshot.board["0,0"])
            assertEquals(Player.O, snapshot.board["1,1"])
            assertEquals(Player.X, snapshot.currentPlayer)
        }

        @Test
        fun `restore should recreate game from snapshot`() {
            // Given: A game and a snapshot with existing moves
            val game = Game()
            val snapshot =
                GameState(
                    board = mapOf("0,0" to Player.X, "1,1" to Player.O),
                    currentPlayer = Player.X,
                )

            // When: Restoring the snapshot
            game.restore(snapshot)

            // Then: Game state should match the snapshot
            assertEquals(Player.X, game.getCell(0, 0))
            assertEquals(Player.O, game.getCell(1, 1))
            assertEquals(Player.X, game.currentPlayer)
        }

        @Test
        fun `game can continue after restore`() {
            // Given: A restored game with X's turn
            val game = Game()
            val snapshot =
                GameState(
                    board = mapOf("0,0" to Player.X),
                    currentPlayer = Player.O,
                )
            game.restore(snapshot)

            // When: O makes a move
            game.play(1, 1)

            // Then: Game continues normally
            assertEquals(Player.O, game.getCell(1, 1))
            assertEquals(Player.X, game.currentPlayer)
        }

        @Test
        fun `restore should handle invalid key format gracefully`() {
            // Given: A snapshot with malformed keys
            val game = Game()
            val snapshot =
                GameState(
                    board =
                        mapOf(
                            "invalid" to Player.X, // No comma
                            "0,0" to Player.O, // Valid
                        ),
                    currentPlayer = Player.X,
                )

            // When: Restoring
            game.restore(snapshot)

            // Then: Only valid keys are restored
            assertEquals(Player.O, game.getCell(0, 0))
            assertNull(game.getCell(1, 1)) // Invalid key ignored
        }

        @Test
        fun `restore should handle non-numeric coordinates gracefully`() {
            // Given: A snapshot with non-numeric coordinates
            val game = Game()
            val snapshot =
                GameState(
                    board =
                        mapOf(
                            "a,b" to Player.X, // Non-numeric
                            "1,1" to Player.O, // Valid
                        ),
                    currentPlayer = Player.X,
                )

            // When: Restoring
            game.restore(snapshot)

            // Then: Only valid entries are restored
            assertEquals(Player.O, game.getCell(1, 1))
        }

        @Test
        fun `restore should handle out-of-bounds coordinates gracefully`() {
            // Given: A snapshot with out-of-bounds coordinates
            val game = Game(3)
            val snapshot =
                GameState(
                    board =
                        mapOf(
                            "5,5" to Player.X, // Out of bounds
                            "0,0" to Player.O, // Valid
                        ),
                    currentPlayer = Player.X,
                )

            // When: Restoring
            game.restore(snapshot)

            // Then: Only valid entries are restored
            assertEquals(Player.O, game.getCell(0, 0))
        }

        @Test
        fun `restore should handle null values in board gracefully`() {
            // Given: A snapshot with null value
            val game = Game()
            val snapshot =
                GameState(
                    board =
                        mapOf(
                            "0,0" to null, // Null value
                            "1,1" to Player.O, // Valid
                        ),
                    currentPlayer = Player.X,
                )

            // When: Restoring
            game.restore(snapshot)

            // Then: Null values are ignored, valid ones restored
            assertNull(game.getCell(0, 0))
            assertEquals(Player.O, game.getCell(1, 1))
        }

        @Test
        fun `Game constructor with initialState should restore state`() {
            // Given: An initial state
            val initialState =
                GameState(
                    board = mapOf("0,0" to Player.X, "1,1" to Player.O),
                    currentPlayer = Player.X,
                    winner = null,
                    isDraw = false,
                )

            // When: Creating game with initial state
            val game = Game(initialState = initialState)

            // Then: State is restored
            assertEquals(Player.X, game.getCell(0, 0))
            assertEquals(Player.O, game.getCell(1, 1))
            assertEquals(Player.X, game.currentPlayer)
        }

        @Test
        fun `getSnapshot should capture winner state`() {
            // Given: A won game
            val game = Game()
            game.play(0, 0) // X
            game.play(1, 0) // O
            game.play(0, 1) // X
            game.play(1, 1) // O
            game.play(0, 2) // X wins

            // When: Taking a snapshot
            val snapshot = game.getSnapshot()

            // Then: Winner is captured
            assertEquals(Player.X, snapshot.winner)
            assertFalse(snapshot.isDraw)
        }

        @Test
        fun `getSnapshot should capture draw state`() {
            // Given: A drawn game
            val game = Game()
            game.play(0, 0) // X
            game.play(1, 1) // O
            game.play(0, 1) // X
            game.play(0, 2) // O
            game.play(2, 0) // X
            game.play(1, 0) // O
            game.play(1, 2) // X
            game.play(2, 1) // O
            game.play(2, 2) // Draw

            // When: Taking a snapshot
            val snapshot = game.getSnapshot()

            // Then: Draw is captured
            assertNull(snapshot.winner)
            assertTrue(snapshot.isDraw)
        }
    }

    // =========================================================================
    // ADDITIONAL: getCell edge cases
    // =========================================================================
    @Nested
    @DisplayName("getCell Edge Cases")
    inner class GetCellEdgeCases {
        @Test
        fun `getCell should return null for negative row`() {
            val game = Game()
            assertNull(game.getCell(-1, 0))
        }

        @Test
        fun `getCell should return null for negative column`() {
            val game = Game()
            assertNull(game.getCell(0, -1))
        }

        @Test
        fun `getCell should return null for row equal to size`() {
            val game = Game(3)
            assertNull(game.getCell(3, 0))
        }

        @Test
        fun `getCell should return null for column equal to size`() {
            val game = Game(3)
            assertNull(game.getCell(0, 3))
        }

        @Test
        fun `getCell should return null for row greater than size`() {
            val game = Game(3)
            assertNull(game.getCell(10, 0))
        }

        @Test
        fun `getCell should return null for column greater than size`() {
            val game = Game(3)
            assertNull(game.getCell(0, 10))
        }

        @Test
        fun `getCell should return player for valid occupied cell`() {
            val game = Game()
            game.play(1, 1) // X
            assertEquals(Player.X, game.getCell(1, 1))
        }

        @Test
        fun `getCell should return null for valid empty cell`() {
            val game = Game()
            assertNull(game.getCell(0, 0))
        }
    }

    // =========================================================================
    // ADDITIONAL: Forfeit functionality
    // =========================================================================
    @Nested
    @DisplayName("Forfeit Functionality")
    inner class ForfeitFunctionality {
        @Test
        fun `forfeit by X should make O the winner`() {
            // Given: A new game (X's turn)
            val game = Game()

            // When: X forfeits
            game.forfeit()

            // Then: O wins
            assertEquals(Player.O, game.winner)
        }

        @Test
        fun `forfeit by O should make X the winner`() {
            // Given: A game where it's O's turn
            val game = Game()
            game.play(0, 0) // X plays, now O's turn

            // When: O forfeits
            game.forfeit()

            // Then: X wins
            assertEquals(Player.X, game.winner)
        }

        @Test
        fun `forfeit after win should throw GameOver`() {
            // Given: A won game
            val game = Game()
            game.play(0, 0)
            game.play(1, 0)
            game.play(0, 1)
            game.play(1, 1)
            game.play(0, 2) // X wins

            // When/Then: Forfeit should throw
            assertThrows<GameException.GameOver> {
                game.forfeit()
            }
        }

        @Test
        fun `forfeit after draw should throw GameOver`() {
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

            // When/Then: Forfeit should throw
            assertThrows<GameException.GameOver> {
                game.forfeit()
            }
        }
    }

    // =========================================================================
    // ADDITIONAL: Custom board sizes
    // =========================================================================
    @Nested
    @DisplayName("Custom Board Sizes")
    inner class CustomBoardSizes {
        @Test
        fun `game with size 4 should require 4 in a row to win`() {
            val game = Game(size = 4)

            // Play 4 in a row
            game.play(0, 0) // X
            game.play(1, 0) // O
            game.play(0, 1) // X
            game.play(1, 1) // O
            game.play(0, 2) // X
            game.play(1, 2) // O
            game.play(0, 3) // X wins

            assertEquals(Player.X, game.winner)
        }

        @Test
        fun `game with size 4 should detect column win`() {
            val game = Game(size = 4)

            game.play(0, 0) // X
            game.play(0, 1) // O
            game.play(1, 0) // X
            game.play(1, 1) // O
            game.play(2, 0) // X
            game.play(2, 1) // O
            game.play(3, 0) // X wins

            assertEquals(Player.X, game.winner)
        }

        @Test
        fun `game with size 4 should detect diagonal win`() {
            val game = Game(size = 4)

            game.play(0, 0) // X
            game.play(0, 1) // O
            game.play(1, 1) // X
            game.play(0, 2) // O
            game.play(2, 2) // X
            game.play(0, 3) // O
            game.play(3, 3) // X wins

            assertEquals(Player.X, game.winner)
        }

        @Test
        fun `getSnapshot should include board size`() {
            val game = Game(size = 5)
            val snapshot = game.getSnapshot()

            assertEquals(5, snapshot.size)
        }
    }
}
