package demo.bigLazyTable.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.exposed.sql.SortOrder

sealed class BLTSort(
    // TODO: Add sort
    var sortOrder: SortOrder?,
    var isSorting: Boolean,
    var icon: ImageVector
) {
    abstract fun nextSortState(): BLTSort
    abstract fun nextSortIcon(): ImageVector

    object Asc : BLTSort(sortOrder = SortOrder.ASC, isSorting = true, icon = Icons.Default.KeyboardArrowUp) {
        override fun nextSortState(): BLTSort = Desc
        override fun nextSortIcon(): ImageVector = nextSortState().icon
    }

    object Desc : BLTSort(sortOrder = SortOrder.DESC, isSorting = true, icon = Icons.Default.KeyboardArrowDown) {
        override fun nextSortState(): BLTSort = None
        override fun nextSortIcon(): ImageVector = nextSortState().icon
    }

    object None : BLTSort(sortOrder = null, isSorting = false, icon = Icons.Default.Close) {
        override fun nextSortState(): BLTSort = Asc
        override fun nextSortIcon(): ImageVector = nextSortState().icon
    }
}