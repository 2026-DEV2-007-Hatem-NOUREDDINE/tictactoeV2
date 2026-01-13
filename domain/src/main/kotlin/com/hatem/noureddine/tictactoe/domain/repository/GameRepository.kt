package com.hatem.noureddine.tictactoe.domain.repository

import com.hatem.noureddine.tictactoe.domain.model.Game

/**
 * Repository interface for managing the Game entity.
 *
 * Abstraction layer for data access, allowing for switching between different
 * data sources (in-memory, database, network) without affecting the domain logic.
 */
interface GameRepository {
    /** The current active game instance. */
    var game: Game

    /**
     * Updates the repository with a new game instance.
     * @param newGame The new game state to save.
     */
    fun updateGame(newGame: Game)
}
