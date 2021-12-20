package demo.bigLazyTable.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.model.PlaylistModel
import demo.bigLazyTable.ui.theme.BackgroundColorGroups
import demo.bigLazyTable.ui.theme.BackgroundColorHeader
import demo.bigLazyTable.ui.theme.BackgroundColorLight

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun RowScope.TableContainer(weight: Float, viewModel: LazyTableViewModel) {
    Box(modifier = Modifier.weight(weight)) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.Top
        ) {
            PageInfoRow(viewModel)
            HeaderRow()
            LazyTable(viewModel)
        }
    }
}

@Composable
fun PageInfoRow(viewModel: LazyTableViewModel) = LazyRow(
    modifier = Modifier.background(get(FormColors.BACKGROUND_COLOR_HEADER)).fillMaxWidth().padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.Start
) {
    item {
        Text(
            text = "Page: ${viewModel.currentPage.value}/${viewModel.maxPages}",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .background(BackgroundColorHeader)
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (attribute in AppState.defaultPlaylistModel.lazyListAttributes) {
            TableCell(
                text = attribute.getLabel(),
                color = Color.White,
                backgroundColor = BackgroundColorHeader,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float = 1f,
    color: Color = Color.Black,
    backgroundColor: Color,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        color = color,
        fontWeight = fontWeight,
        modifier = Modifier
            .background(backgroundColor)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun LazyTable(viewModel: LazyTableViewModel) {
    val lazyListItems = AppState.lazyModelList
    val listState = rememberLazyListState()
    val scrollbarStyle = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 12.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 1000,
        unhoverColor = Color.Red.copy(alpha = 0.3f),
        hoverColor = Color.Red.copy(alpha = 0.8f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val firstVisibleItemIndex = listState.firstVisibleItemIndex
        if (viewModel.isTimeToLoadPage(firstVisibleItemIndex)) {
            viewModel.loadAllNeededPagesFor(firstVisibleItemIndex = firstVisibleItemIndex)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = listState
        ) {
            items(lazyListItems) { playlistModel ->
                when (playlistModel) {
                    null -> PlaylistRowPlaceholder()
                    else -> PlaylistRow(viewModel, playlistModel)
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(listState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            style = scrollbarStyle
        )
    }
}

@Composable
private fun PlaylistRow(viewModel: LazyTableViewModel, playlistModel: PlaylistModel) {
    val isSelected = AppState.selectedPlaylistModel.id.getValue() == playlistModel.id.getValue()
    val backgroundColor = if (isSelected) BackgroundColorGroups else BackgroundColorLight

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .selectable(
                selected = isSelected,
                onClick = { viewModel.selectPlaylist(playlistModel) }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (attribute in playlistModel.lazyListAttributes) {
            TableCell(
                text = attribute.getValueAsText(),
                backgroundColor = backgroundColor
            )
        }
    }
}

@Composable
fun PlaylistRowPlaceholder(
    backgroundColor: Color = BackgroundColorLight
) {
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TableCell(
            text = "...",
            backgroundColor = backgroundColor
        )
    }
}