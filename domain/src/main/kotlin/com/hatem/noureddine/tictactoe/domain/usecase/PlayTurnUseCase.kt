package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case to execute a turn in the game.
 *
 * @param gameRepository The repository holding the current game state.
 */
class PlayTurnUseCase
    @Inject
    constructor(
        private val gameRepository: GameRepository,
    ) {
        /**
         * Executes a move at the given position.
         *
         * @param row The row index.
         * @param col The column index.
         * @throws GameException If the move is invalid or the game is valid.
         */
        operator fun invoke(
            row: Int,
            col: Int,
        ) {
            val game = gameRepository.game
            game.play(row, col)
            gameRepository.updateGame(game)
        }
    }
