package bigLazyTable.paging

//import composeForms.model.attributes.Attribute
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

data class Sort(
    val dbField: Column<String>?,
    val sortOrder: SortOrder
)

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    fun getPage(startIndex: Int, pageSize: Int, filters: List<Filter>, sort: Sort? = null): List<T>

    fun getFilteredCount(filters: List<Filter>): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}