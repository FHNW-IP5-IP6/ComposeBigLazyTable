package demo.bigLazyTable.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.model.PlaylistModel
import demo.bigLazyTable.ui.theme.BackgroundColorGroups
import demo.bigLazyTable.ui.theme.BackgroundColorHeader
import demo.bigLazyTable.ui.theme.BackgroundColorLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            text = "Page: ${viewModel.currentPage}/${viewModel.maxPages}",
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
//    attribute: Attribute<*,*,*>,
    text: String,
    weight: Float = 1f,
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
//            .weight(weight)
            .padding(8.dp),
    )
}

@Composable
fun LazyTable(viewModel: LazyTableViewModel) {
    val lazyListItems = AppState.lazyModelList
    val listState = rememberLazyListState()

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        val firstVisibleItemIndex = listState.firstVisibleItemIndex

        // FIXME@JetBrains: listState.isScrollInProgress is always false
        // TODO: Eigene Scrollbar funktion
        if (!viewModel.isScrolling && viewModel.isTimeToLoadPage(firstVisibleItemIndex)) {

//            viewModel.scheduler.forEach { job ->
//                job.cancel()
//                println("job $job canceled")
//            }

//            viewModel.scheduler.removeAll { true }

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.loadAllNeededPagesFor(firstVisibleItemIndex = firstVisibleItemIndex)
            }
//            viewModel.scheduler.add(y).also {
//                println("job added")
//            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = listState
        ) {
            // TODO: Experiment mit key
            items(lazyListItems/*, key = id*/) { playlistModel ->
                when (playlistModel) {
                    null -> PlaylistRowPlaceholder()
                    else -> PlaylistRow(viewModel, playlistModel)
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(listState),
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
            style = ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 12.dp,
                shape = RoundedCornerShape(4.dp),
                hoverDurationMillis = 1000,
                unhoverColor = Color.Red.copy(alpha = 0.3f),
                hoverColor = Color.Red.copy(alpha = 0.8f)
            )
        )

        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            style = ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 12.dp,
                shape = RoundedCornerShape(4.dp),
                hoverDurationMillis = 1000,
                unhoverColor = Color.Red.copy(alpha = 0.3f),
                hoverColor = Color.Red.copy(alpha = 0.8f)
            )
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
                backgroundColor = backgroundColor,
                hasError = !attribute.isValid()
            )
        }
    }
}

@Composable
fun PlaylistRowPlaceholder(backgroundColor: Color = BackgroundColorLight) {
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