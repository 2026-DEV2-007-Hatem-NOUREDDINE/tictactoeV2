package com.hatem.noureddine.tictactoe.domain.usecase

import com.hatem.noureddine.tictactoe.domain.model.Player
import com.hatem.noureddine.tictactoe.domain.repository.FakeGameRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetSnapshotUseCaseTest {
    @Test
    fun `invoke returns correct snapshot`() {
        // Given
        val repository = FakeGameRepository()
        val getSnapshotUseCase = GetSnapshotUseCase(repository)
        repository.game.play(0, 0) // X plays at 0,0

        // When
        val snapshot = getSnapshotUseCase()

        // Then
        assertEquals(Player.X, snapshot.board["0,0"])
        assertEquals(Player.O, snapshot.currentPlayer)
    }
}
