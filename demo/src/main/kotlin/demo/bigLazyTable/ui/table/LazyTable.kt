package demo.bigLazyTable.ui.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.theme.CustomScrollbarStyle

@Composable
fun LazyTable(
    viewModel: LazyTableViewModel,
    horizontalScrollState: ScrollState,
    appState: AppState
) {
    val lazyListItems = appState.lazyModelList
    val verticalLazyListState = rememberLazyListState()
    // TODO: Used for recompose, other solution for unused variable?
    val currentPage = viewModel.currentPage

    Box(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        val firstVisibleItemIndex = verticalLazyListState.firstVisibleItemIndex

        if (viewModel.isTimeToLoadPage(firstVisibleItemIndex)) {
            viewModel.scheduler.set{viewModel.loadAllNeededPagesForIndex(firstVisibleItemIndex)}
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = verticalLazyListState
        ) {
            items(
                items = lazyListItems
            ) { playlistModel ->
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
                .fillMaxHeight()
            ,
            style = CustomScrollbarStyle
        )
    }
}