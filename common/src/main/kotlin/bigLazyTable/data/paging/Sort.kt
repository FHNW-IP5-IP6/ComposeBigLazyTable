package bigLazyTable.data.paging

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

/**
 * @author Marco Sprenger, Livio Näf
 */
data class Sort(
    val dbField: Column<*>?,
    val sortOrder: SortOrder
)