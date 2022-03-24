package bigLazyTable.view.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import composeForms.model.BaseModel
import bigLazyTable.controller.AppState
import bigLazyTable.controller.LazyTableController
import bigLazyTable.view.theme.CustomScrollbarStyle
import bigLazyTable.view.theme.ScrollbarThickness

@Composable
fun <T: BaseModel<*>> LazyTable(
    controller: LazyTableController<T>,
    horizontalScrollState: ScrollState,
    appState: AppState<*>
) {
    val verticalLazyListState = rememberLazyListState()

    with(controller) {
        val recomposeTrigger = recomposeStateChanger // must be here for recompose! TODO-Future: better solution

        if (isLoading) {
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 14.dp) // TODO@Marco: Auf ScrollbarMinimumHeight bezogen?
        ) {
            val firstVisibleItemIndex = verticalLazyListState.firstVisibleItemIndex
            if (isTimeToLoadPage(firstVisibleItemIndex)) {
                scheduler.scheduleTask { loadNewPages(firstVisibleItemIndex) }
            }

            // Go to the top when start/stop filtering
            LaunchedEffect(key1 = isFiltering, key2 = sort) {
                verticalLazyListState.scrollToItem(0)
            }

            LazyColumn(
                modifier = Modifier.padding(end = ScrollbarThickness),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = verticalLazyListState
            ) {
                val lazyListItems = if (isFiltering) appState.filteredTableModelList else appState.tableModelList
                items(items = lazyListItems) { tableModel ->
                    when (tableModel) {
                        null -> TableRowPlaceholder(
                            horizontalScrollState = horizontalScrollState,
                            appState = appState
                        )
                        else -> TableRow(
                            controller = controller,
                            tableModel = tableModel as T,
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
    }
}