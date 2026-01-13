package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case to retrieve the current game entity.
 *
 * @param gameRepository The repository holding the game state.
 */
class GetGameUseCase
    @Inject
    constructor(
        private val gameRepository: GameRepository,
    ) {
        /**
         * Returns the current [Game] instance.
         */
        operator fun invoke(): Game = gameRepository.game
    }
