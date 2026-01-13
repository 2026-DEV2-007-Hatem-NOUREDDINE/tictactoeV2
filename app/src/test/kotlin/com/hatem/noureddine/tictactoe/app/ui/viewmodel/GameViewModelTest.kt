package com.hatem.noureddine.tictactoe.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.hatem.noureddine.tictactoe.app.util.MainDispatcherExtension
import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.model.GameException
import com.hatem.noureddine.tictactoe.domain.model.GameState
import com.hatem.noureddine.tictactoe.domain.model.Player
import com.hatem.noureddine.tictactoe.domain.usecase.GetGameUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.GetSnapshotUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.LoadGameUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.PlayTurnUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.ResetGameUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@ExperimentalCoroutinesApi
class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension(testDispatcher)

    private lateinit var viewModel: GameViewModel
    private val playTurnUseCase: PlayTurnUseCase = mockk(relaxed = true)
    private val resetGameUseCase: ResetGameUseCase = mockk(relaxed = true)
    private val getGameUseCase: GetGameUseCase = mockk()
    private val loadGameUseCase: LoadGameUseCase = mockk(relaxed = true)
    private val getSnapshotUseCase: GetSnapshotUseCase = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        every { getGameUseCase() } returns Game()
        every { savedStateHandle.get<GameState>("game_state") } returns null
        viewModel =
            GameViewModel(
                savedStateHandle,
                playTurnUseCase,
                resetGameUseCase,
                getGameUseCase,
                loadGameUseCase,
                getSnapshotUseCase,
            )
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            val initialState = viewModel.uiState.value
            assertEquals(3, initialState.boardSize)
            assertEquals(Player.X, initialState.currentPlayer)
            assertEquals(null, initialState.winner)
            assertEquals(false, initialState.isDraw)
        }

    @Test
    fun `play calls playTurnUseCase and updates state`() =
        runTest {
            // Given
            val row = 0
            val col = 0
            val gameAfterPlay = Game().apply { play(row, col) }
            every { getGameUseCase() } returns gameAfterPlay

            // When
            viewModel.play(row, col)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            verify { playTurnUseCase(row, col) }
            verify { getSnapshotUseCase() }
            val currentState = viewModel.uiState.value
            assertEquals(Player.O, currentState.currentPlayer)
        }

    @Test
    fun `reset calls resetGameUseCase and updates state`() =
        runTest {
            // Given
            val newSize = 4
            val newGame = Game(size = newSize)
            every { getGameUseCase() } returns newGame

            // When
            viewModel.reset(newSize)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            verify { resetGameUseCase(newSize) }
            verify { getSnapshotUseCase() }
            val currentState = viewModel.uiState.value
            assertEquals(newSize, currentState.boardSize)
            assertEquals(Player.X, currentState.currentPlayer)
        }

    @Test
    fun `reset with default size uses 3`() =
        runTest {
            // Given
            val defaultGame = Game(size = 3)
            every { getGameUseCase() } returns defaultGame

            // When
            viewModel.reset()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            verify { resetGameUseCase(3) }
            assertEquals(3, viewModel.uiState.value.boardSize)
        }

    @Test
    fun `play ignores PositionTaken error`() =
        runTest {
            // Given
            val game = mockk<Game>(relaxed = true)
            every { getGameUseCase() } returns game
            every { playTurnUseCase(any(), any()) } throws GameException.PositionTaken()

            // When
            viewModel.play(0, 0)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val currentState = viewModel.uiState.value
            // PositionTaken is now ignored
            assertEquals(null, currentState.errorMessageId)
        }

    @Test
    fun `play calls generic error when Exception occurs`() =
        runTest {
            // Given
            every { playTurnUseCase(any(), any()) } throws RuntimeException("Unexpected error")

            // When
            viewModel.play(0, 0)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val currentState = viewModel.uiState.value
            assert(currentState.errorMessageId != null)
        }

    @Test
    fun `clearError sets errorMessageId to null`() =
        runTest {
            // Trigger an error first
            every { playTurnUseCase(any(), any()) } throws RuntimeException("Error")
            viewModel.play(0, 0)

            // Clear it
            viewModel.clearError()

            val currentState = viewModel.uiState.value
            assertEquals(null, currentState.errorMessageId)
        }

    @Test
    fun `handleException maps GameOver correctly`() =
        runTest {
            every { playTurnUseCase(any(), any()) } throws GameException.GameOver()
            viewModel.play(0, 0)
            // Verify R.string.error_game_over (can't check int value easily but can check non-null)
            assert(viewModel.uiState.value.errorMessageId != null)
        }

    @Test
    fun `handleException ignores InvalidPosition`() =
        runTest {
            every { playTurnUseCase(any(), any()) } throws GameException.InvalidPosition()
            viewModel.play(0, 0)
            // Verify no error
            assertEquals(null, viewModel.uiState.value.errorMessageId)
        }

    @Test
    fun `play ignores error if cell is not empty`() =
        runTest {
            // Setup a game where 0,0 is taken.
            val game = Game().apply { play(0, 0) }
            val exception = GameException.PositionTaken()

            every { getGameUseCase() } returns game
            // playTurnUseCase throws when attempting to play on 0,0
            every { playTurnUseCase(0, 0) } throws exception

            val newViewModel =
                GameViewModel(
                    savedStateHandle,
                    playTurnUseCase,
                    resetGameUseCase,
                    getGameUseCase,
                    loadGameUseCase,
                    getSnapshotUseCase,
                )

            newViewModel.play(0, 0)
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify error is NOT set
            assertEquals(null, newViewModel.uiState.value.errorMessageId)
        }

    @Test
    fun `play sets error if winner exists`() =
        runTest {
            val game =
                Game().apply {
                    play(0, 0)
                    play(0, 1)
                    play(1, 1)
                    play(0, 2)
                    play(2, 2) // X wins
                }

            every { getGameUseCase() } returns game
            every { playTurnUseCase(any(), any()) } throws GameException.GameOver()

            val newViewModel =
                GameViewModel(
                    savedStateHandle,
                    playTurnUseCase,
                    resetGameUseCase,
                    getGameUseCase,
                    loadGameUseCase,
                    getSnapshotUseCase,
                )

            newViewModel.play(1, 0) // Empty spot but game over
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify error is set
            assert(newViewModel.uiState.value.errorMessageId != null)
        }

    @Test
    fun `init with saved state calls loadGameUseCase`() =
        runTest {
            // Given
            val savedState =
                GameState(
                    board = mapOf("0,0" to Player.X, "1,1" to Player.O),
                    currentPlayer = Player.X,
                    winner = null,
                    isDraw = false,
                    size = 3,
                )
            val restoredGame = Game(initialState = savedState)

            val newSavedStateHandle: SavedStateHandle = mockk(relaxed = true)
            every { newSavedStateHandle.get<GameState>("game_state") } returns savedState
            every { getGameUseCase() } returns restoredGame

            // When
            val newViewModel =
                GameViewModel(
                    newSavedStateHandle,
                    playTurnUseCase,
                    resetGameUseCase,
                    getGameUseCase,
                    loadGameUseCase,
                    getSnapshotUseCase,
                )
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            verify { loadGameUseCase(savedState) }
            assertEquals(Player.X, newViewModel.uiState.value.currentPlayer)
        }

    @Test
    fun `uiState reflects winner when game is won`() =
        runTest {
            // Given a game with a winner
            val wonGame =
                Game().apply {
                    play(0, 0) // X
                    play(1, 0) // O
                    play(0, 1) // X
                    play(1, 1) // O
                    play(0, 2) // X wins
                }
            every { getGameUseCase() } returns wonGame

            // When
            val newViewModel =
                GameViewModel(
                    savedStateHandle,
                    playTurnUseCase,
                    resetGameUseCase,
                    getGameUseCase,
                    loadGameUseCase,
                    getSnapshotUseCase,
                )

            // Then
            assertEquals(Player.X, newViewModel.uiState.value.winner)
        }

    @Test
    fun `uiState reflects isDraw when game is drawn`() =
        runTest {
            // Given a drawn game
            val drawnGame =
                Game().apply {
                    play(0, 0) // X
                    play(1, 1) // O
                    play(0, 1) // X
                    play(0, 2) // O
                    play(2, 0) // X
                    play(1, 0) // O
                    play(1, 2) // X
                    play(2, 1) // O
                    play(2, 2) // X - Draw
                }
            every { getGameUseCase() } returns drawnGame

            // When
            val newViewModel =
                GameViewModel(
                    savedStateHandle,
                    playTurnUseCase,
                    resetGameUseCase,
                    getGameUseCase,
                    loadGameUseCase,
                    getSnapshotUseCase,
                )

            // Then
            assertEquals(true, newViewModel.uiState.value.isDraw)
        }

    @Test
    fun `board in uiState matches game board`() =
        runTest {
            // Given a game with some moves
            val game =
                Game().apply {
                    play(0, 0) // X
                    play(1, 1) // O
                }
            every { getGameUseCase() } returns game

            // When
            val newViewModel =
                GameViewModel(
                    savedStateHandle,
                    playTurnUseCase,
                    resetGameUseCase,
                    getGameUseCase,
                    loadGameUseCase,
                    getSnapshotUseCase,
                )

            // Then
            val board = newViewModel.uiState.value.board
            assertEquals(Player.X, board[0][0])
            assertEquals(Player.O, board[1][1])
            assertEquals(null, board[0][1])
        }
}
