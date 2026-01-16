package com.hatem.noureddine.tictactoe.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.hatem.noureddine.tictactoe.R
import com.hatem.noureddine.tictactoe.domain.model.Game
import com.hatem.noureddine.tictactoe.domain.model.GameException
import com.hatem.noureddine.tictactoe.domain.model.GameState
import com.hatem.noureddine.tictactoe.domain.model.Player
import com.hatem.noureddine.tictactoe.domain.usecase.GetGameUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.GetSnapshotUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.LoadGameUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.PlayTurnUseCase
import com.hatem.noureddine.tictactoe.domain.usecase.ResetGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * UI state representing the current snapshot of the Game Board.
 *
 * @property boardSize The size of the board (e.g., 3 for 3x3).
 * @property board The grid of cells, represented as an ImmutableList of ImmutableLists.
 * @property currentPlayer The player whose turn it is (X or O).
 * @property winner The player who won the game, or null if no winner yet.
 * @property isDraw True if the game has ended in a draw, false otherwise.
 * @property errorMessageId Resource ID for any localized error message (e.g., Game Over), or null if no error.
 */
data class BoardUiState(
    val boardSize: Int = 3,
    val board: ImmutableList<ImmutableList<Player?>> =
        persistentListOf(
            persistentListOf(null, null, null),
            persistentListOf(null, null, null),
            persistentListOf(null, null, null),
        ),
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val errorMessageId: Int? = null,
)

/**
 * ViewModel responsible for managing the game state and business logic integration.
 *
 * It bridges the UI (Jetpack Compose) and the Domain Layer (Use Cases).
 * State is exposed via [uiState] as a StateFlow and persisted using [SavedStateHandle].
 *
 * @param savedStateHandle Handle to save and restore game state across process death.
 * @param playTurn Use case to execute a player's move.
 * @param resetGame Use case to reset the game state.
 * @param getGame Use case to retrieve the current game entity.
 * @param loadGame Use case to re-initialize the game engine from a saved state.
 * @param getSnapshot Use case to get a serializable snapshot of the current game.
 */
@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val playTurn: PlayTurnUseCase,
        private val resetGame: ResetGameUseCase,
        private val getGame: GetGameUseCase,
        private val loadGame: LoadGameUseCase,
        private val getSnapshot: GetSnapshotUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(BoardUiState())
        val uiState = _uiState.asStateFlow()

        init {
            val savedGame = savedStateHandle.get<GameState>("game_state")
            loadGame(savedGame)
            updateUiState()
        }

        /**
         * Attempts to play a move at the specified row and column.
         *
         * Handles game logic exceptions (e.g., Game Over, Position Taken) and updates
         * the UI state accordingly.
         *
         * @param row The row index (0-based).
         * @param col The column index (0-based).
         */
        fun play(
            row: Int,
            col: Int,
        ) {
            try {
                playTurn(row, col)
                saveState()
                updateUiState()
            } catch (e: GameException) {
                handleException(e)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessageId = R.string.error_unknown) }
            }
        }

        private fun handleException(e: GameException) {
            val errorId =
                when (e) {
                    is GameException.GameOver -> R.string.error_game_over
                    else -> null
                }

            if (errorId != null) {
                _uiState.update { it.copy(errorMessageId = errorId) }
            }
        }

        /**
         * Resets the game to a new initial state with the specified board size.
         *
         * @param size The dimension of the new board (default is 3).
         */
        @Suppress("CascadingCallWrapping")
        fun reset(size: Int = 3) {
            resetGame(size)
            saveState()
            // Reset with correct empty board of dynamic size
            val emptyBoard =
                persistentListOf<ImmutableList<Player?>>()
                    .builder()
                    .apply {
                        repeat(size) {
                            add(
                                persistentListOf<Player?>()
                                    .builder()
                                    .apply {
                                        repeat(size) { add(null) }
                                    }.build(),
                            )
                        }
                    }.build()
            _uiState.value = BoardUiState(boardSize = size, board = emptyBoard)
        }

        /**
         * Clears the current error message from the UI state.
         * Should be called after the error has been consumed (e.g., shown in a Snackbar).
         */
        fun clearError() {
            _uiState.update { it.copy(errorMessageId = null) }
        }

        private fun saveState() {
            savedStateHandle["game_state"] = getSnapshot()
        }

        private fun updateUiState() {
            val game = getGame()
            _uiState.update {
                it.copy(
                    boardSize = game.size,
                    board = getBoardSnapshot(game),
                    currentPlayer = game.currentPlayer,
                    winner = game.winner,
                    isDraw = game.isDraw,
                    errorMessageId = null,
                )
            }
        }

        @Suppress("CascadingCallWrapping")
        private fun getBoardSnapshot(game: Game): ImmutableList<ImmutableList<Player?>> =
            persistentListOf<ImmutableList<Player?>>()
                .builder()
                .apply {
                    repeat(game.size) { r ->
                        add(
                            persistentListOf<Player?>()
                                .builder()
                                .apply {
                                    repeat(game.size) { c ->
                                        add(game.getCell(r, c))
                                    }
                                }.build(),
                        )
                    }
                }.build()
    }
