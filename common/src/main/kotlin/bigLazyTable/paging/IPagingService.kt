package bigLazyTable.paging

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

data class Filter(
    val filter: String, //Int/usw generisch
    val dbField: Column<String>?,
    var caseSensitive: Boolean,
)

data class Sort(
    val dbField: Column<String>?,
    val sortOrder: SortOrder // ENUM/?
)

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

//    fun getPage(startIndex: Int, pageSize: Int, filter: String = "", dbField: Column<String>?, caseSensitive: Boolean = false, sorted: String = ""): List<T>

    // TODO: 1 sort
    fun getPageNew(startIndex: Int, pageSize: Int, filters: List<Filter>, sort: Sort? = null): List<T>

//    fun getFilteredCount(filter: String, dbField: Column<*>?, caseSensitive: Boolean = false): Int

    fun getFilteredCountNew(filters: List<Filter>): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}