package com.hatem.noureddine.tictactoe.domain.model

/**
 * Sealed class representing all possible game logic errors.
 */
sealed class GameException : Exception() {
    /** Thrown when a player attempts to move after the game has ended. */
    class GameOver : GameException()

    /** Thrown when a player attempts to move to an occupied cell. */
    class PositionTaken : GameException()

    /** Thrown when a player attempts to move to a position outside the board dimensions. */
    class InvalidPosition : GameException()
}
