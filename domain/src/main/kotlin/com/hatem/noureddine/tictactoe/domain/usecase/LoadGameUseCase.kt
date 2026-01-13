package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.model.GameState
import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case to load a game from a saved state.
 *
 * @param gameRepository The repository to load the game into.
 */
class LoadGameUseCase
    @Inject
    constructor(
        private val gameRepository: GameRepository,
    ) {
        /**
         * Restores the game from the provided snapshot.
         *
         * @param state The saved [GameState] to restore, or null to start a new game.
         */
        operator fun invoke(state: GameState?) {
            if (state != null) {
                val game = Game(state.size)
                game.restore(state)
                gameRepository.updateGame(game)
            }
        }
    }
