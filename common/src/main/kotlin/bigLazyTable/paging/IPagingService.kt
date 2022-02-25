package bigLazyTable.paging

import org.jetbrains.exposed.sql.Column

data class Filter(
    val filter: String, //Int/usw generisch
    val dbField: Column<*>?,
    var caseSensitive: Boolean
)

data class Sort(
    val dbField: Column<*>?,
    val sorted: String // ENUM/
)

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    fun getPage(startIndex: Int, pageSize: Int, filter: String = "", dbField: Column<*>?, caseSensitive: Boolean = false, sorted: String = ""): List<T>

    // TODO: 1 sort
    fun getPageNew(startIndex: Int, pageSize: Int, filters: List<Filter>, sorted: String = ""): List<T>

    fun getFilteredCount(filter: String, dbField: Column<*>?, caseSensitive: Boolean = false): Int

    fun getFilteredCountNew(filters: List<Filter>): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}