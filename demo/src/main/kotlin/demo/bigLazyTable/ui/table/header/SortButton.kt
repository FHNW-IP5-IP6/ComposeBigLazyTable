package demo.bigLazyTable.ui.table.header

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.BLTSortOrder
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.ui.theme.ContentDescriptionSortIcon

@Composable
fun SortButton(
    modifier: Modifier,
    attribute: Attribute<*, *, *>,
    controller: LazyTableController<*>
) {
    // TODO: Decide if we remove nextSortState() or nextSortState
    //  Decide for function or member approach
    val sortOrder = controller.attributeSort[attribute]
    IconButton(
        modifier = modifier,
        onClick = {
            controller.onSortChanged(
                attribute = attribute,
                newSortOrder = sortOrder?.nextSortState/*nextSortState()*/ ?: BLTSortOrder.None
            )
        }
    ) {
        Icon(
            imageVector = sortOrder?.nextSortIcon/*nextSortIcon()*/ ?: BLTSortOrder.None.icon,
            contentDescription = ContentDescriptionSortIcon,
            tint = Color.White
        )
    }
}