package com.hatem.noureddine.tictactoe.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hatem.noureddine.tictactoe.domain.model.Player
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Displays the game board grid.
 *
 * Renders a dynamic square grid of cells based on the [boardSize].
 *
 * @param boardSize The dimension of the board (e.g., 3).
 * @param board The state of each cell in the grid.
 * @param onCellClick Callback invoked when a cell is clicked, passing row and column.
 * @param isDraw True if the game is a draw.
 * @param modifier Modifier for styling.
 */
@Composable
fun Board(
    boardSize: Int,
    board: ImmutableList<ImmutableList<Player?>>,
    onCellClick: (Int, Int) -> Unit,
    isDraw: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            for (row in 0 until boardSize) {
                Row {
                    for (col in 0 until boardSize) {
                        Cell(
                            row = row,
                            col = col,
                            player = board[row][col],
                            onClick = { onCellClick(row, col) },
                            enabled = !isDraw,
                            size = 300 / boardSize,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BoardPreview() {
    MaterialTheme {
        Board(
            boardSize = 3,
            board =
                persistentListOf(
                    persistentListOf(null, null, null),
                    persistentListOf(null, null, null),
                    persistentListOf(null, null, null),
                ),
            onCellClick = { _, _ -> },
            isDraw = false,
        )
    }
}
