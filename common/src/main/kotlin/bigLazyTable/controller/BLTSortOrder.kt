package bigLazyTable.controller

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.vector.ImageVector
import bigLazyTable.data.paging.Sort
import composeForms.model.attributes.Attribute
import org.jetbrains.exposed.sql.SortOrder

/**
 * @author Marco Sprenger, Livio Näf
 */
sealed class BLTSortOrder(
    var sortOrder: SortOrder?,
    var isSorting: Boolean,
    var icon: ImageVector
) {
    abstract val nextSortState: BLTSortOrder
    abstract val nextSortIcon: ImageVector
    abstract fun sortAttribute(attribute: Attribute<*, *, *>): Sort?

    object Asc : BLTSortOrder(sortOrder = SortOrder.ASC, isSorting = true, icon = Icons.Default.KeyboardArrowUp) {
        override val nextSortState: BLTSortOrder = Desc
        override val nextSortIcon: ImageVector = nextSortState.icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort = Sort(
            dbField = attribute.databaseField,
            sortOrder = sortOrder!!
        )
    }

    object Desc : BLTSortOrder(sortOrder = SortOrder.DESC, isSorting = true, icon = Icons.Default.KeyboardArrowDown) {
        override val nextSortState: BLTSortOrder = None
        override val nextSortIcon: ImageVector = nextSortState.icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort = Sort(
            dbField = attribute.databaseField,
            sortOrder = sortOrder!!
        )
    }

    object None : BLTSortOrder(sortOrder = null, isSorting = false, icon = Icons.Default.Close) {
        override val nextSortState: BLTSortOrder = Asc
        override val nextSortIcon: ImageVector = nextSortState.icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort? = null
    }
}