package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.repository.FakeGameRepository
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class GetGameUseCaseTest {
    @Test
    fun `GetGameUseCase returns the game from the repository`() {
        // Given
        val repository = FakeGameRepository()
        val getGameUseCase = GetGameUseCase(repository)

        // When
        val game = getGameUseCase()

        // Then
        assertSame(repository.game, game)
    }
}
