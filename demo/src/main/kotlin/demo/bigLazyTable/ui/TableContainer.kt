package demo.bigLazyTable.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.model.PlaylistModel
import demo.bigLazyTable.ui.theme.*

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun RowScope.TableContainer(weight: Float, viewModel: LazyTableViewModel) {
    val horizontalScrollState = rememberScrollState()

    Box(modifier = Modifier.weight(weight)) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.Top
        ) {
            PageInfoRow(viewModel = viewModel)
            HeaderRow(horizontalScrollState = horizontalScrollState)
            LazyTable(
                viewModel = viewModel,
                horizontalScrollState = horizontalScrollState
            )
        }

        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            style = CustomScrollbarStyle
        )
    }
}

@Composable
fun PageInfoRow(viewModel: LazyTableViewModel) = LazyRow(
    modifier = Modifier
        .background(BackgroundColorHeader)
        .fillMaxWidth()
        .padding(horizontal = 5.dp),
    horizontalArrangement = Arrangement.Start
) {
    item {
        Text(
            text = "Page: ${viewModel.currentPage}/${viewModel.maxPages}",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HeaderRow(horizontalScrollState: ScrollState) {
    Row(
        modifier = Modifier
            .background(BackgroundColorHeader)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .horizontalScroll(horizontalScrollState),
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

// TODO: Make Example Application to play around with the TableCell and its attribute
@Composable
fun RowScope.AttributeTableCell(
    attribute: Attribute<*, *, *>,
    backgroundColor: Color
) = TableCell(
    text = attribute.getValueAsText(),
    backgroundColor = backgroundColor,
    hasError = !attribute.isValid()
)

@Composable
fun TableCell(
    text: String,
    width: Dp = 150.dp, // TODO: Make it dynamically adjust when there is more or less text
    color: Color = Color.Black,
    backgroundColor: Color,
    fontWeight: FontWeight = FontWeight.Normal,
    hasError: Boolean = false
) {
    Text(
        text = text,
        color = if (hasError) Color.Red else color,
        fontWeight = if (hasError) FontWeight.Bold else fontWeight,
        modifier = Modifier
            .background(backgroundColor)
            .width(width)
            .padding(8.dp),
    )
}

@Composable
fun LazyTable(
    viewModel: LazyTableViewModel,
    horizontalScrollState: ScrollState
) {
    val lazyListItems = AppState.lazyModelList
    val verticalLazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        val firstVisibleItemIndex = verticalLazyListState.firstVisibleItemIndex

        // FIXME@JetBrains: listState.isScrollInProgress is always false
        // TODO: Eigene Scrollbar funktion
        if (!viewModel.isScrolling && viewModel.isTimeToLoadPage(firstVisibleItemIndex)) {
            viewModel.loadAllNeededPagesForIndex(firstVisibleItemIndex)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = verticalLazyListState
        ) {

            // TODO: Experiment mit key
            items(lazyListItems/*, key = id*/) { playlistModel ->
                when (playlistModel) {
                    null -> PlaylistRowPlaceholder(horizontalScrollState = horizontalScrollState)
                    else -> PlaylistRow(
                        viewModel = viewModel,
                        playlistModel = playlistModel,
                        horizontalScrollState = horizontalScrollState
                    )
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(verticalLazyListState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .pointerMoveFilter(
                    onEnter = {
                        viewModel.isScrolling = true
                        println("On Mouse(pointer) Enter")
                        false
                    },
                    onExit = {
                        viewModel.isScrolling = false
                        println("on Mouse(pointer) Exit")
                        false
                    }),
            style = CustomScrollbarStyle
        )
    }
}

@Composable
private fun PlaylistRow(
    viewModel: LazyTableViewModel,
    playlistModel: PlaylistModel,
    horizontalScrollState: ScrollState
) {
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
    lazyListAttributes: List<Attribute<*, *, *>> = AppState.defaultPlaylistModel.lazyListAttributes
) {
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