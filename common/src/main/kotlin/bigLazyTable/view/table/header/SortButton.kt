package bigLazyTable.view.table.header

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import bigLazyTable.controller.LazyTableController
import bigLazyTable.controller.BLTSortOrder
import bigLazyTable.view.theme.ContentDescriptionSortIcon
import composeForms.model.attributes.Attribute

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun SortButton(
    modifier: Modifier,
    attribute: Attribute<*, *, *>,
    controller: LazyTableController<*>
) {
    val sortOrder = controller.attributeSort[attribute]
    IconButton(
        modifier = modifier,
        onClick = {
            controller.onSortChanged(
                attribute = attribute,
                newSortOrder = sortOrder?.nextSortState ?: BLTSortOrder.None
            )
        }
    ) {
        Icon(
            imageVector = sortOrder?.nextSortIcon ?: BLTSortOrder.None.icon,
            contentDescription = ContentDescriptionSortIcon,
            tint = Color.White
        )
    }
}