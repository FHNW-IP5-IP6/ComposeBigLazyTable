package demo.bigLazyTable.view.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import composeForms.model.BaseModel
import composeForms.ui.theme.*
import demo.bigLazyTable.controller.AppState
import demo.bigLazyTable.controller.LazyTableController
import demo.bigLazyTable.view.theme.HorizontalPadding

@Composable // TODO: Replace PlaylistModel with dynamic BaseModel
fun <T: BaseModel<*>> PlaylistRow(
    controller: LazyTableController<T>,
    playlistModel: T,
    horizontalScrollState: ScrollState,
    appState: AppState<*>
) {
    val isSelected = appState.selectedTableModel.id.getValue() == playlistModel.id.getValue()
    val backgroundColor = if (isSelected) BackgroundColorGroups else BackgroundColorLight

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding)
            .selectable(
                selected = isSelected,
                onClick = { controller.selectModel(playlistModel) }
            )
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        for (attribute in playlistModel.displayedAttributesInTable!!) {
            AttributeTableCell(
                attribute = attribute,
                backgroundColor = backgroundColor
            )
        }
    }
}

@Composable
fun PlaylistRowPlaceholder(
    backgroundColor: Color = BackgroundColorLight,
    horizontalScrollState: ScrollState,
    appState: AppState<*>
) {
    val lazyListAttributes = appState.defaultTableModel.displayedAttributesInTable

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (attribute in lazyListAttributes!!) {
            TableCell(
                attribute = attribute,
                text = "...",
                backgroundColor = backgroundColor
            )
        }
    }
}