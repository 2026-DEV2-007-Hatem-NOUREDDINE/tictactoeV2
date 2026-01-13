package com.hatem.noureddine.tictactoe.domain.model

import java.io.Serializable

/**
 * Serializable snapshot of the Game state.
 *
 * Used for persisting the game across process death or for undo/redo functionality.
 *
 * @property board Map of "row,col" keys to [Player] values.
 * @property currentPlayer The player whose turn it is.
 * @property winner The winner, if any.
 * @property isDraw True if the game is a draw.
 * @property size The size of the board.
 */
data class GameState(
    val board: Map<String, Player?> = emptyMap(),
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val size: Int = 3,
) : Serializable
