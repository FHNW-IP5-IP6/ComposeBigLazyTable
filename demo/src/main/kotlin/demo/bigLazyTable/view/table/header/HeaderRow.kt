package demo.bigLazyTable.view.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import composeForms.model.BaseModel
import composeForms.ui.theme.BackgroundColorHeader
import demo.bigLazyTable.controller.AppState
import demo.bigLazyTable.controller.LazyTableController
import demo.bigLazyTable.view.table.AttributeTableCell
import demo.bigLazyTable.view.theme.HorizontalPadding

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
                    AttributeTableCell(
                        isTitle = true,
                        attribute = attribute,
                        color = Color.White,
                        backgroundColor = BackgroundColorHeader
                    )
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