package bigLazyTable.paging

import org.jetbrains.exposed.sql.Column

data class Filter(
    val filter: String,
    val dbField: Column<*>?,
    var caseSensitive: Boolean
)

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    fun getPage(startIndex: Int, pageSize: Int, filter: String = "", dbField: Column<*>?, caseSensitive: Boolean = false, sorted: String = ""): List<T>

    // TODO: does it make sense to add a Filter Object with filter: String & caseSensitive: Boolean as Parameters?
    fun getPageNew(startIndex: Int, pageSize: Int, filters: List<Filter>?, sorted: String = ""): List<T>

    fun getFilteredCount(filter: String, caseSensitive: Boolean = false): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}