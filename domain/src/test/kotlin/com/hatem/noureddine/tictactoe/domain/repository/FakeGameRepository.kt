package com.hatem.noureddine.tictactoe.domain.repository

import com.hatem.noureddine.tictactoe.domain.model.Game

/**
 * Fake implementation of [GameRepository] for testing purposes.
 *
 * @param initialSize The initial board size (default: 3)
 */
class FakeGameRepository(
    initialSize: Int = 3,
) : GameRepository {
    override var game: Game = Game(size = initialSize)

    override fun updateGame(newGame: Game) {
        game = newGame
    }
}
