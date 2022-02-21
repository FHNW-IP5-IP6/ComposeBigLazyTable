package demo.bigLazyTable.ui.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.ui.theme.CustomScrollbarStyle

@Composable
fun LazyTable(
    viewModel: LazyTableController,
    horizontalScrollState: ScrollState,
    appState: AppState
) {
    val lazyListItems = if (viewModel.isFiltering) appState.filteredList else appState.lazyModelList
    val verticalLazyListState = rememberLazyListState()

    with(viewModel) {
        // TODO: Check if LaunchedEffect works with recomposeStateChanger as key
//    LaunchedEffect(recomposeStateChanger) {
        val recomposeTrigger = recomposeStateChanger // must be here for recompose! TODO: better solution

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            val firstVisibleItemIndex = verticalLazyListState.firstVisibleItemIndex
            if (isTimeToLoadPage(firstVisibleItemIndex)) {
                // TODO: Code feels confusing to read! instead of set -> scheduleTask, ???
                scheduler.set { loadAllNeededPagesForIndex(firstVisibleItemIndex) }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = verticalLazyListState
            ) {
                items(items = lazyListItems) { playlistModel ->
                    when (playlistModel) {
                        null -> PlaylistRowPlaceholder(
                            horizontalScrollState = horizontalScrollState,
                            appState = appState
                        )
                        else -> PlaylistRow(
                            viewModel = viewModel,
                            playlistModel = playlistModel,
                            horizontalScrollState = horizontalScrollState,
                            appState = appState
                        )
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(verticalLazyListState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                style = CustomScrollbarStyle
            )
        }

//    }
    }
}