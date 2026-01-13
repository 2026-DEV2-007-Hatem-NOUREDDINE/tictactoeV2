package com.hatem.noureddine.tictactoe.data.repository

import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of the [GameRepository].
 *
 * Stores the game state in a local variable. This state handles the lifecycle
 * of the game session while the app is alive.
 */
@Singleton
class GameRepositoryImpl
    @Inject
    constructor() : GameRepository {
        override var game: Game = Game(size = 3)

        override fun updateGame(newGame: Game) {
            game = newGame
        }
    }
