package demo.bigLazyTable.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.vector.ImageVector
import bigLazyTable.paging.Sort
import composeForms.model.attributes.Attribute
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

sealed class BLTSortOrder(
    var sortOrder: SortOrder?,
    var isSorting: Boolean,
    var icon: ImageVector
) {
    abstract fun nextSortState(): BLTSortOrder
    abstract fun nextSortIcon(): ImageVector
    abstract fun sortAttribute(attribute: Attribute<*, *, *>): Sort?

    object Asc : BLTSortOrder(sortOrder = SortOrder.ASC, isSorting = true, icon = Icons.Default.KeyboardArrowUp) {
        override fun nextSortState(): BLTSortOrder = Desc
        override fun nextSortIcon(): ImageVector = nextSortState().icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort = Sort(
            dbField = attribute.databaseField as Column<String>,
            sortOrder = sortOrder!!
        )
    }

    object Desc : BLTSortOrder(sortOrder = SortOrder.DESC, isSorting = true, icon = Icons.Default.KeyboardArrowDown) {
        override fun nextSortState(): BLTSortOrder = None
        override fun nextSortIcon(): ImageVector = nextSortState().icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort = Sort(
            dbField = attribute.databaseField as Column<String>,
            sortOrder = sortOrder!!
        )
    }

    object None : BLTSortOrder(sortOrder = null, isSorting = false, icon = Icons.Default.Close) {
        override fun nextSortState(): BLTSortOrder = Asc
        override fun nextSortIcon(): ImageVector = nextSortState().icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort? = null
    }
}