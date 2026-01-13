package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case to reset the game state.
 *
 * @param gameRepository The repository managing the game state.
 */
class ResetGameUseCase
    @Inject
    constructor(
        private val gameRepository: GameRepository,
    ) {
        /**
         * Resets the game to a fresh state with the specified board size.
         *
         * @param size The size of the new board (default is 3).
         */
        operator fun invoke(size: Int = 3) {
            gameRepository.updateGame(Game(size = size))
        }
    }
