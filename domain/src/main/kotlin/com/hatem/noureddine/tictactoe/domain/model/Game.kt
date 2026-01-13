package com.hatem.noureddine.tictactoe.domain.model

/**
 * The core logic of a Tic-Tac-Toe game, designed using Test-Driven Development (TDD).
 *
 * ## TDD Design Journey
 *
 * This class was built incrementally, with each feature driven by a failing test:
 *
 * 1. **Initial State (Rule 1)**: Tests required `currentPlayer = X`, `winner = null`, `isDraw = false`
 * 2. **Making Moves (Rule 3)**: Tests required `play(row, col)` to switch players
 * 3. **Recording Moves**: Tests required `getCell(row, col)` to return the player at a position
 * 4. **Invalid Moves (Rule 2)**: Tests required guard clauses throwing [GameException]
 * 5. **Win Detection (Rules 4-5)**: Tests required `checkWin()` logic for rows, columns, diagonals
 * 6. **Draw Detection (Rule 6)**: Tests required move counting and `isDraw` state
 * 7. **Game Over Guard**: Tests required blocking moves after win or draw
 *
 * ## Game Rules (Specification)
 *
 * 1. X always goes first
 * 2. Players cannot play on a played position
 * 3. Players alternate placing X's and O's on the board
 * 4. Game ends when one player has three in a row (horizontal, vertical, or diagonal)
 * 5. Three in a row = win
 * 6. All squares filled with no winner = draw
 *
 * @property size The size of the game board (default: 3 for a 3x3 grid)
 * @param initialState Optional [GameState] to restore from a saved game
 */
class Game(
    val size: Int = DEFAULT_BOARD_SIZE,
    initialState: GameState? = null,
) {
    // =========================================================================
    // State Properties (TDD Step 1: Define initial state)
    // Tests: "new game should start with Player X", "winner should be null"
    // =========================================================================

    /**
     * The player whose turn it is.
     * Starts as X (Rule 1: X always goes first).
     */
    var currentPlayer: Player = initialState?.currentPlayer ?: Player.X
        private set

    /**
     * The winner of the game, or `null` if no winner yet.
     * Set when [checkWin] returns true (Rule 5).
     */
    var winner: Player? = initialState?.winner
        private set

    /**
     * `true` if the game is a draw (Rule 6: all squares filled, no winner).
     */
    var isDraw: Boolean = initialState?.isDraw ?: false
        private set

    // =========================================================================
    // Internal State (TDD Step 3: Record moves on a board)
    // Tests: "playing a move updates the board"
    // =========================================================================

    /** Internal representation of the game board. */
    private val board = Array(size) { arrayOfNulls<Player>(size) }

    /** Tracks move count for draw detection (TDD Step 6). */
    private var moveCount = 0

    init {
        // TDD: Tests for restore functionality drove this implementation
        if (initialState != null) {
            restore(initialState)
        }
    }

    // =========================================================================
    // Core Game Action: play() (TDD Steps 2-7)
    // =========================================================================

    /**
     * Places the [currentPlayer]'s mark on the specified cell.
     *
     * This method implements the core game loop, driven by TDD:
     * - **TDD Step 4**: Validate move (guard clauses)
     * - **TDD Step 3**: Record the move on the board
     * - **TDD Step 5**: Check for win condition
     * - **TDD Step 6**: Check for draw condition
     * - **TDD Step 2**: Switch to next player
     *
     * @param row The row of the cell (0-indexed)
     * @param col The column of the cell (0-indexed)
     * @throws GameException.GameOver if the game has already ended
     * @throws GameException.InvalidPosition if the position is out of bounds
     * @throws GameException.PositionTaken if the cell is already occupied
     */
    fun play(
        row: Int,
        col: Int,
    ) {
        // TDD Step 4: Guard clauses (tests for invalid moves)
        validateMove(row, col)

        // TDD Step 3: Record the move
        board[row][col] = currentPlayer
        moveCount++

        // TDD Step 5: Check for win (tests: "playing a winning move updates the winner")
        if (checkWin(currentPlayer)) {
            winner = currentPlayer
            return
        }

        // TDD Step 6: Check for draw (tests: "game should be a draw when board is full")
        if (moveCount == size * size) {
            isDraw = true
            return
        }

        // TDD Step 2: Alternate players (tests: "after X plays, it should be O's turn")
        currentPlayer = currentPlayer.opponent()
    }

    // =========================================================================
    // Additional Actions
    // =========================================================================

    /**
     * Allows the current player to forfeit, making the opponent the winner.
     *
     * @throws GameException.GameOver if the game is already over
     */
    fun forfeit() {
        if (winner != null || isDraw) {
            throw GameException.GameOver()
        }
        winner = currentPlayer.opponent()
    }

    // =========================================================================
    // State Query Methods (TDD Step 3)
    // =========================================================================

    /**
     * Retrieves the player who has marked a specific cell.
     *
     * @param row The row of the cell
     * @param col The column of the cell
     * @return The [Player] in the cell, or `null` if empty or position is invalid
     */
    fun getCell(
        row: Int,
        col: Int,
    ): Player? {
        if (row !in 0 until size || col !in 0 until size) {
            return null
        }
        return board[row][col]
    }

    // =========================================================================
    // Snapshot & Restore (for persistence)
    // =========================================================================

    /**
     * Captures the current state of the game as an immutable [GameState].
     *
     * @return A snapshot that can be saved and later restored
     */
    fun getSnapshot(): GameState {
        val boardMap = mutableMapOf<String, Player?>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                board[r][c]?.let { boardMap["$r,$c"] = it }
            }
        }
        return GameState(
            board = boardMap,
            currentPlayer = currentPlayer,
            winner = winner,
            isDraw = isDraw,
            size = size,
        )
    }

    /**
     * Restores the game from a previously saved [GameState].
     *
     * @param snapshot The state to restore from
     */
    fun restore(snapshot: GameState) {
        // Clear the board
        for (r in 0 until size) {
            for (c in 0 until size) {
                board[r][c] = null
            }
        }
        moveCount = 0

        // Restore moves from the snapshot
        snapshot.board.forEach { (key, value) ->
            restoreCell(key, value)
        }
        currentPlayer = snapshot.currentPlayer
        winner = snapshot.winner
        isDraw = snapshot.isDraw
    }

    // =========================================================================
    // Private Helper Methods
    // =========================================================================

    /**
     * Validates that a move is legal.
     *
     * TDD: Each exception type was added when a test required it:
     * - GameOver: "playing after win should throw GameOver"
     * - InvalidPosition: "playing outside board throws InvalidPosition"
     * - PositionTaken: "playing on occupied cell should throw PositionTaken"
     */
    @Suppress("ThrowsCount")
    private fun validateMove(
        row: Int,
        col: Int,
    ) {
        // TDD Step 7: Game over guard
        if (winner != null || isDraw) {
            throw GameException.GameOver()
        }
        // TDD: Invalid position test
        if (row !in 0 until size || col !in 0 until size) {
            throw GameException.InvalidPosition()
        }
        // TDD Step 4: Position taken test
        if (board[row][col] != null) {
            throw GameException.PositionTaken()
        }
    }

    /**
     * Checks if the specified player has won the game.
     *
     * TDD: This method was refactored after initial tests passed.
     * First test only checked one row, then refactored to check all win conditions.
     */
    private fun checkWin(player: Player): Boolean {
        // Check all rows
        val hasWinningRow =
            (0 until size).any { row ->
                (0 until size).all { col -> board[row][col] == player }
            }

        // Check all columns
        val hasWinningCol =
            (0 until size).any { col ->
                (0 until size).all { row -> board[row][col] == player }
            }

        // Check main diagonal (top-left to bottom-right)
        val hasWinningDiagonal =
            (0 until size).all { i ->
                board[i][i] == player
            }

        // Check anti-diagonal (top-right to bottom-left)
        val hasWinningAntiDiagonal =
            (0 until size).all { i ->
                board[i][size - 1 - i] == player
            }

        return hasWinningRow || hasWinningCol || hasWinningDiagonal || hasWinningAntiDiagonal
    }

    /**
     * Restores a single cell from a saved snapshot.
     * Handles invalid key formats gracefully.
     */
    private fun restoreCell(
        key: String,
        value: Player?,
    ) {
        val parts = key.split(",")
        if (parts.size == 2) {
            val r = parts[0].toIntOrNull()
            val c = parts[1].toIntOrNull()
            if (isValidSavedMove(r, c, value)) {
                board[r!!][c!!] = value
                moveCount++
            }
        }
    }

    /**
     * Validates that a saved move can be restored.
     */
    private fun isValidSavedMove(
        r: Int?,
        c: Int?,
        value: Player?,
    ): Boolean = r != null && c != null && value != null && r in 0 until size && c in 0 until size

    /**
     * Returns the opponent of this player.
     */
    private fun Player.opponent(): Player = if (this == Player.X) Player.O else Player.X

    companion object {
        private const val DEFAULT_BOARD_SIZE = 3
    }
}
