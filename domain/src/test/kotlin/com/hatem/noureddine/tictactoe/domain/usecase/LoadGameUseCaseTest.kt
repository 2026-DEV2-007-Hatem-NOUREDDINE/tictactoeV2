package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.GameState
import com.hatem.noureddine.tictactoe.domain.model.Player
import com.hatem.noureddine.tictactoe.domain.repository.FakeGameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class LoadGameUseCaseTest {
    @Test
    fun `invoke with state restores the game`() {
        // Given
        val repository = FakeGameRepository()
        val loadGameUseCase = LoadGameUseCase(repository)
        val snapshot =
            GameState(
                board = mapOf("0,0" to Player.X),
                currentPlayer = Player.O,
                winner = null,
                isDraw = false,
                size = 3,
            )

        // When
        loadGameUseCase(snapshot)

        // Then
        assertEquals(Player.X, repository.game.getCell(0, 0))
        assertEquals(Player.O, repository.game.currentPlayer)
    }

    @Test
    fun `invoke with null does nothing`() {
        // Given
        val repository = FakeGameRepository()
        val loadGameUseCase = LoadGameUseCase(repository)
        val initialGame = repository.game

        // When
        loadGameUseCase(null)

        // Then
        assertSame(initialGame, repository.game)
    }
}
