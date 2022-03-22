package bigLazyTable.data.paging

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

data class Sort(
    val dbField: Column<*>?,
    val sortOrder: SortOrder
)