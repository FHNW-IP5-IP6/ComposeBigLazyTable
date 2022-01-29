package demo.bigLazyTable.ui.table

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.table.header.HeaderRow
import demo.bigLazyTable.ui.table.header.PageInfoRow
import demo.bigLazyTable.ui.theme.*

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun RowScope.TableContainer(weight: Float, viewModel: LazyTableViewModel, appState: AppState) {
    val horizontalScrollState = rememberScrollState()

    Box(modifier = Modifier.weight(weight)) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.Top
        ) {
            PageInfoRow(viewModel = viewModel)
            HeaderRow(horizontalScrollState = horizontalScrollState, appState = appState)
            LazyTable(
                viewModel = viewModel,
                horizontalScrollState = horizontalScrollState,
                appState = appState
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