package com.hatem.noureddine.tictactoe.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hatem.noureddine.tictactoe.R
import com.hatem.noureddine.tictactoe.app.ui.theme.PlayerOColor
import com.hatem.noureddine.tictactoe.app.ui.theme.PlayerXColor
import com.hatem.noureddine.tictactoe.domain.model.Player

private const val ICON_SIZE_RATIO = 0.6f
private val cellPadding = 4.dp
private val cellBorderWidth = 1.dp

/**
 * Renders a single cell within the game board.
 *
 * Displays the player's move (X or O) or responds to click events if empty.
 * Supports accessibility via content descriptions and haptic feedback on interaction.
 *
 * @param row The row index of this cell.
 * @param col The column index of this cell.
 * @param player The player occupying this cell, or null if empty.
 * @param onClick Callback invoked when the cell is clicked.
 * @param enabled True if the cell allows interaction.
 * @param size The size of the cell in dp.
 * @param modifier Modifier for styling.
 */
@Composable
fun Cell(
    row: Int,
    col: Int,
    player: Player?,
    onClick: () -> Unit,
    enabled: Boolean,
    size: Int,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val cellDescription = getCellDescription(player, row, col)
    val isClickable = enabled && player == null

    Card(
        modifier =
            modifier
                .size(size.dp)
                .padding(cellPadding)
                .semantics { contentDescription = cellDescription }
                .testTag("cell-$row-$col")
                .clickable(enabled = isClickable) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
        border = BorderStroke(cellBorderWidth, MaterialTheme.colorScheme.onSurface),
    ) {
        CellContent(
            player = player,
            size = size,
        )
    }
}

// =============================================================================
// Cell Content
// =============================================================================

@Composable
private fun CellContent(
    player: Player?,
    size: Int,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedPlayerIcon(
            player = player,
            size = size,
        )
    }
}

@Composable
private fun AnimatedPlayerIcon(
    player: Player?,
    size: Int,
) {
    AnimatedVisibility(
        visible = player != null,
        enter = scaleIn() + fadeIn(),
    ) {
        player?.let { currentPlayer ->
            PlayerIcon(
                player = currentPlayer,
                size = size,
            )
        }
    }
}

@Composable
private fun PlayerIcon(
    player: Player,
    size: Int,
) {
    val iconProperties = getPlayerIconProperties(player)

    Icon(
        imageVector = iconProperties.imageVector,
        contentDescription = null, // Decorative, cell has own description
        modifier = Modifier.size((size * ICON_SIZE_RATIO).dp),
        tint = iconProperties.tint,
    )
}

// =============================================================================
// Helper Functions
// =============================================================================

/**
 * Properties for rendering a player's icon.
 */
private data class PlayerIconProperties(
    val imageVector: ImageVector,
    val tint: Color,
)

private fun getPlayerIconProperties(player: Player): PlayerIconProperties =
    when (player) {
        Player.X -> {
            PlayerIconProperties(
                imageVector = Icons.Rounded.Close,
                tint = PlayerXColor,
            )
        }

        Player.O -> {
            PlayerIconProperties(
                imageVector = Icons.Rounded.RadioButtonUnchecked,
                tint = PlayerOColor,
            )
        }
    }

@Composable
private fun getCellDescription(
    player: Player?,
    row: Int,
    col: Int,
): String {
    val displayRow = row + 1
    val displayCol = col + 1

    return if (player == null) {
        stringResource(R.string.cd_cell_empty, displayRow, displayCol)
    } else {
        val playerName = getPlayerName(player)
        stringResource(R.string.cd_cell_filled, displayRow, displayCol, playerName)
    }
}

@Composable
private fun getPlayerName(player: Player): String =
    when (player) {
        Player.X -> stringResource(R.string.cd_player_x)
        Player.O -> stringResource(R.string.cd_player_o)
    }

// =============================================================================
// Preview
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun CellPreviewX() {
    MaterialTheme {
        Cell(
            row = 0,
            col = 0,
            player = Player.X,
            onClick = {},
            enabled = true,
            size = 100,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CellPreviewO() {
    MaterialTheme {
        Cell(
            row = 1,
            col = 1,
            player = Player.O,
            onClick = {},
            enabled = true,
            size = 100,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CellPreviewEmpty() {
    MaterialTheme {
        Cell(
            row = 2,
            col = 2,
            player = null,
            onClick = {},
            enabled = true,
            size = 100,
        )
    }
}
