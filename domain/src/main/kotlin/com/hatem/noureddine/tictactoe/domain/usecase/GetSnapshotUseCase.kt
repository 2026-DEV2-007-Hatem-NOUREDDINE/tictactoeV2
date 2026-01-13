package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.GameState
import com.hatem.noureddine.tictactoe.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Use case to get a serializable snapshot of the current game.
 *
 * @param gameRepository The repository holding the current game.
 */
class GetSnapshotUseCase
    @Inject
    constructor(
        private val gameRepository: GameRepository,
    ) {
        /**
         * Returns a [GameState] snapshot of the current game.
         */
        operator fun invoke(): GameState = gameRepository.game.getSnapshot()
    }
