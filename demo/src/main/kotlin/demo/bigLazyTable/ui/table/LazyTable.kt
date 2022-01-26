package demo.bigLazyTable.ui.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.theme.CustomScrollbarStyle

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
        // TODO: Should we move this isTimeToLoadPage check to our viewmodel and do it at the begin of the loadAllNeededPagesForIndex function?
        if (/*!viewModel.isScrolling &&*/ viewModel.isTimeToLoadPage(firstVisibleItemIndex)) {
            viewModel.loadAllNeededPagesForIndex(firstVisibleItemIndex)
            // TODO If scheduler issue #32 fixed --> Remove this debbuging print statement
            println("LazyTable.kt: isTimeToLoadPage returned true")
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
                        false
                    },
                    onExit = {
                        viewModel.isScrolling = false
                        false
                    })
            ,
            style = CustomScrollbarStyle
        )
    }
}