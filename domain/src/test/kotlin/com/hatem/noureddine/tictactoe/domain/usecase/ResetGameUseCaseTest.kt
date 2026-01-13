package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.repository.FakeGameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test

class ResetGameUseCaseTest {
    @Test
    fun `invoke with defaults resets game to size 3`() {
        // Given
        val repository = FakeGameRepository()
        val resetGameUseCase = ResetGameUseCase(repository)
        val oldGame = repository.game

        // When
        resetGameUseCase()

        // Then
        val newGame = repository.game
        assertNotSame(oldGame, newGame)
        assertEquals(3, newGame.size)
    }

    @Test
    fun `invoke with custom size resets game to that size`() {
        // Given
        val repository = FakeGameRepository()
        val resetGameUseCase = ResetGameUseCase(repository)

        // When
        resetGameUseCase(5)

        // Then
        assertEquals(5, repository.game.size)
    }
}
