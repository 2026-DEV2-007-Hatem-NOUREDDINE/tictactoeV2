package com.hatem.noureddine.tictactoe.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hatem.noureddine.tictactoe.R
import com.hatem.noureddine.tictactoe.app.ui.theme.PlayerOColor
import com.hatem.noureddine.tictactoe.app.ui.theme.PlayerXColor
import com.hatem.noureddine.tictactoe.domain.model.Player

/**
 * Displays the current status of the game (turn, winner, or draw).
 *
 * Uses explicit iconography and text to convey state.
 *
 * @param winner The winning player, or null if the game is ongoing.
 * @param isDraw True if the game ended in a draw.
 * @param currentPlayer The player whose turn it is.
 * @param modifier Modifier for styling.
 */
@Composable
fun GameStatus(
    winner: Player?,
    isDraw: Boolean,
    currentPlayer: Player,
    modifier: Modifier = Modifier,
) {
    when {
        winner != null -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(bottom = 24.dp),
            ) {
                Text(
                    text = stringResource(R.string.winning_player),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector =
                        if (winner == Player.X) {
                            Icons.Rounded.Close
                        } else {
                            Icons.Rounded.RadioButtonUnchecked
                        },
                    contentDescription =
                        stringResource(
                            R.string.winner_message,
                            winner.name,
                        ),
                    modifier = Modifier.size(40.dp),
                    tint = if (winner == Player.X) PlayerXColor else PlayerOColor,
                )
            }
        }

        isDraw -> {
            Text(
                text = stringResource(R.string.draw_message),
                style = MaterialTheme.typography.headlineMedium,
                modifier = modifier.padding(bottom = 24.dp),
            )
        }

        else -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(bottom = 24.dp),
            ) {
                Text(
                    text = stringResource(R.string.current_player_label),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector =
                        if (currentPlayer == Player.X) {
                            Icons.Rounded.Close
                        } else {
                            Icons.Rounded.RadioButtonUnchecked
                        },
                    contentDescription =
                        stringResource(
                            R.string.current_player,
                            currentPlayer.name,
                        ),
                    modifier = Modifier.size(40.dp),
                    tint = if (currentPlayer == Player.X) PlayerXColor else PlayerOColor,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameStatusPreview() {
    MaterialTheme {
        GameStatus(
            winner = null,
            isDraw = false,
            currentPlayer = Player.X,
        )
    }
}
