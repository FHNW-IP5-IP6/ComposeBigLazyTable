package demo.bigLazyTable.ui.table.header

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.BLTSortOrder
import demo.bigLazyTable.model.LazyTableController

@Composable
fun SortButton(
    modifier: Modifier,
    attribute: Attribute<*, *, *>,
    controller: LazyTableController
) {
    val sortOrder = controller.attributeSort[attribute]
    IconButton(
        modifier = modifier,
        onClick = {
            controller.onSortChanged(
                attribute = attribute,
                newSortOrder = sortOrder?.nextSortState() ?: BLTSortOrder.None
            )
        }
    ) {
        Icon(
            imageVector = sortOrder?.nextSortIcon() ?: BLTSortOrder.None.icon,
            contentDescription = "Sortieren",
            tint = Color.White
        )
    }
}