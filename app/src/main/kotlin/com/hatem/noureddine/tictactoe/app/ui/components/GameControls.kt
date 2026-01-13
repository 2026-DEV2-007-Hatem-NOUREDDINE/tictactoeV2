@file:Suppress("TooManyFunctions")

package com.hatem.noureddine.tictactoe.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hatem.noureddine.tictactoe.R

private val buttonCornerRadius = 12.dp
private val iconSize = 18.dp
private val availableSizes = listOf(3, 4, 5)

/**
 * Provides controls for game settings and actions.
 *
 * Includes a dropdown for selecting board size and a reset button.
 *
 * @param boardSize The current size of the board.
 * @param onBoardSizeChange Callback to update the board size.
 * @param onReset Callback to restart the game.
 * @param modifier Modifier for styling.
 */
@Composable
fun GameControls(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GridSizeSelector(
            boardSize = boardSize,
            onBoardSizeChange = onBoardSizeChange,
        )

        Spacer(modifier = Modifier.width(16.dp))

        ResetButton(onClick = onReset)
    }
}

// =============================================================================
// Grid Size Selector Component
// =============================================================================

@Composable
private fun GridSizeSelector(
    boardSize: Int,
    onBoardSizeChange: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var buttonWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box {
        GridSelectorButton(
            boardSize = boardSize,
            expanded = expanded,
            onExpandClick = { expanded = true },
            onWidthMeasured = { buttonWidth = with(density) { it.toDp() } },
        )

        GridSizeDropdown(
            expanded = expanded,
            buttonWidth = buttonWidth,
            currentSize = boardSize,
            onDismiss = { expanded = false },
            onSizeSelected = { size ->
                onBoardSizeChange(size)
                expanded = false
            },
        )
    }
}

@Composable
private fun GridSelectorButton(
    boardSize: Int,
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onWidthMeasured: (Int) -> Unit,
) {
    val borderColor =
        if (expanded) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        }

    OutlinedButton(
        onClick = onExpandClick,
        shape = RoundedCornerShape(buttonCornerRadius),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.onSizeChanged { onWidthMeasured(it.width) },
    ) {
        GridSelectorButtonContent(
            boardSize = boardSize,
            expanded = expanded,
        )
    }
}

@Composable
private fun GridSelectorButtonContent(
    boardSize: Int,
    expanded: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.GridView,
            contentDescription = null, // Decorative
            modifier = Modifier.size(iconSize),
        )
        Text("Grid: ${boardSize}x$boardSize")
        ExpandCollapseIcon(expanded = expanded)
    }
}

@Composable
private fun ExpandCollapseIcon(expanded: Boolean) {
    Icon(
        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
        contentDescription = if (expanded) "Collapse grid selector" else "Expand grid selector",
        modifier = Modifier.size(iconSize),
    )
}

// =============================================================================
// Dropdown Menu Component
// =============================================================================

@Composable
private fun GridSizeDropdown(
    expanded: Boolean,
    buttonWidth: Dp,
    currentSize: Int,
    onDismiss: () -> Unit,
    onSizeSelected: (Int) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier =
            Modifier
                .width(buttonWidth)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(buttonCornerRadius),
    ) {
        availableSizes.forEachIndexed { index, size ->
            GridSizeMenuItem(
                size = size,
                isSelected = size == currentSize,
                onClick = { onSizeSelected(size) },
            )
            if (index < availableSizes.lastIndex) {
                MenuDivider()
            }
        }
    }
}

@Composable
private fun GridSizeMenuItem(
    size: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = "${size}x$size",
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        },
        onClick = onClick,
        leadingIcon = {
            if (isSelected) {
                SelectedCheckIcon()
            }
        },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun SelectedCheckIcon() {
    Icon(
        imageVector = Icons.Rounded.Check,
        contentDescription = "Selected",
        tint = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    )
}

// =============================================================================
// Reset Button Component
// =============================================================================

@Composable
private fun ResetButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(buttonCornerRadius),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.restart_game))
    }
}

// =============================================================================
// Preview
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun GameControlsPreview() {
    MaterialTheme {
        GameControls(
            boardSize = 3,
            onBoardSizeChange = {},
            onReset = {},
        )
    }
}
