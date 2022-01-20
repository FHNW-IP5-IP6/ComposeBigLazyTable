package bigLazyTable.paging

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    // TODO: does it make sense to add a Filter Object with filter: String & caseSensitive: Boolean as Parameters?
    suspend fun getPage(startIndex: Int, pageSize: Int, filter: String = "", caseSensitive: Boolean = false): List<T>

    fun getFilteredCount(filter: String, caseSensitive: Boolean = false): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}