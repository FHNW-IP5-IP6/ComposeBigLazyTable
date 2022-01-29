package demo.bigLazyTable.ui.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.model.PlaylistModel
import demo.bigLazyTable.ui.theme.BackgroundColorGroups
import demo.bigLazyTable.ui.theme.BackgroundColorLight

@Composable
fun PlaylistRow(
    viewModel: LazyTableViewModel,
    playlistModel: PlaylistModel,
    horizontalScrollState: ScrollState,
    appState: AppState
) {
    val isSelected = appState.selectedPlaylistModel.id.getValue() == playlistModel.id.getValue()
    val backgroundColor = if (isSelected) BackgroundColorGroups else BackgroundColorLight

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .selectable(
                selected = isSelected,
                onClick = { viewModel.selectPlaylist(playlistModel) }
            )
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        for (attribute in playlistModel.lazyListAttributes) {
            TableCell(
                text = attribute.getValueAsText(),
                backgroundColor = backgroundColor,
                hasError = !attribute.isValid()
            )
        }
    }
}

@Composable
fun PlaylistRowPlaceholder(
    backgroundColor: Color = BackgroundColorLight,
    horizontalScrollState: ScrollState,
    appState: AppState
) {
    val lazyListAttributes = appState.defaultPlaylistModel.lazyListAttributes

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (attribute in lazyListAttributes) {
            TableCell(
                text = "...",
                backgroundColor = backgroundColor
            )
        }
    }
}