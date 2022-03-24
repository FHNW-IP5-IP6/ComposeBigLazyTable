package bigLazyTable.view.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import composeForms.model.BaseModel
import composeForms.ui.theme.BackgroundColorHeader
import bigLazyTable.controller.AppState
import bigLazyTable.controller.LazyTableController
import bigLazyTable.view.table.HeaderCell
import bigLazyTable.view.theme.HorizontalPadding

@Composable
fun <T: BaseModel<*>> HeaderRow(
    horizontalScrollState: ScrollState,
    appState: AppState<T>,
    controller: LazyTableController<*>
) {
    Row(
        modifier = Modifier
            .background(BackgroundColorHeader)
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (attribute in appState.defaultTableModel.displayedAttributesInTable!!) {
            Column {
                FilterTextField(
                    attribute = attribute,
                    controller = controller
                )
                Box(contentAlignment = Alignment.Center) {
                    HeaderCell(attribute = attribute)
                    SortButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        attribute = attribute,
                        controller = controller
                    )
                }
            }
        }
    }
}