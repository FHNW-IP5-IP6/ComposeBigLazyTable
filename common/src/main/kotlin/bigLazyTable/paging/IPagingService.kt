package bigLazyTable.paging

//import composeForms.model.attributes.Attribute
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

data class Sort(
    val dbField: Column<*>?,
    val sortOrder: SortOrder
)

/**
 * // TODO: Add description?
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    fun <FT> getPage(startIndex: Int, pageSize: Int, filters: List<Filter<FT>> = emptyList(), sort: Sort? = null): List<T>

    fun <FT> getFilteredCount(filters: List<Filter<FT>>): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

}