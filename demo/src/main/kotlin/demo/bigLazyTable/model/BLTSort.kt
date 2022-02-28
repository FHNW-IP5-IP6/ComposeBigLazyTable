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

sealed class BLTSort(
    var sortOrder: SortOrder?,
    var isSorting: Boolean,
    var icon: ImageVector
) {
    abstract fun nextSortState(): BLTSort
    abstract fun nextSortIcon(): ImageVector
    abstract fun sortAttribute(attribute: Attribute<*, *, *>): Sort?

    object Asc : BLTSort(sortOrder = SortOrder.ASC, isSorting = true, icon = Icons.Default.KeyboardArrowUp) {
        override fun nextSortState(): BLTSort = Desc
        override fun nextSortIcon(): ImageVector = nextSortState().icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort = Sort(
            dbField = attribute.databaseField as Column<String>,
            sortOrder = sortOrder!!
        )
    }

    object Desc : BLTSort(sortOrder = SortOrder.DESC, isSorting = true, icon = Icons.Default.KeyboardArrowDown) {
        override fun nextSortState(): BLTSort = None
        override fun nextSortIcon(): ImageVector = nextSortState().icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort = Sort(
            dbField = attribute.databaseField as Column<String>,
            sortOrder = sortOrder!!
        )
    }

    object None : BLTSort(sortOrder = null, isSorting = false, icon = Icons.Default.Close) {
        override fun nextSortState(): BLTSort = Asc
        override fun nextSortIcon(): ImageVector = nextSortState().icon
        override fun sortAttribute(attribute: Attribute<*, *, *>): Sort? = null
    }
}