package demo.bigLazyTable.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.exposed.sql.SortOrder

abstract class BLTSort {
    abstract var sortOrder: SortOrder?
    abstract var isSorting: Boolean
    abstract var icon: ImageVector
    abstract fun nextSortState(): BLTSort
    abstract fun nextSortIcon(): ImageVector
}

data class Asc(
    override var sortOrder: SortOrder? = SortOrder.ASC,
    override var isSorting: Boolean = true,
    override var icon: ImageVector = Icons.Default.KeyboardArrowUp
) : BLTSort() {
    override fun nextSortState(): BLTSort = Desc()
    override fun nextSortIcon(): ImageVector = nextSortState().icon
}

data class Desc(
    override var sortOrder: SortOrder? = SortOrder.DESC,
    override var isSorting: Boolean = true,
    override var icon: ImageVector = Icons.Default.KeyboardArrowDown
) : BLTSort() {
    override fun nextSortState(): BLTSort = None()
    override fun nextSortIcon(): ImageVector = nextSortState().icon
}

data class None(
    override var sortOrder: SortOrder? = null,
    override var isSorting: Boolean = false,
    override var icon: ImageVector = Icons.Default.Close
) : BLTSort() {
    override fun nextSortState(): BLTSort = Asc()
    override fun nextSortIcon(): ImageVector = nextSortState().icon
}