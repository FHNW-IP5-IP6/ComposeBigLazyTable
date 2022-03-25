package bigLazyTable.view.table

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bigLazyTable.controller.AppState
import bigLazyTable.controller.LazyTableController
import bigLazyTable.view.table.header.HeaderRow
import bigLazyTable.view.theme.CustomScrollbarStyle
import bigLazyTable.view.theme.HorizontalPadding

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun RowScope.TableContainer(
    weight: Float,
    controller: LazyTableController<*>,
    appState: AppState<*>
) {
    val horizontalScrollState = rememberScrollState()

    Box(modifier = Modifier.weight(weight)) {
        Column(
            modifier = Modifier.padding(horizontal = HorizontalPadding),
            verticalArrangement = Arrangement.Top
        ) {
            HeaderRow(
                horizontalScrollState = horizontalScrollState,
                appState = appState,
                controller = controller
            )
            LazyTable(
                controller = controller,
                horizontalScrollState = horizontalScrollState,
                appState = appState
            )
        }

        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(end= 25.dp),
            style = CustomScrollbarStyle
        )
    }
}