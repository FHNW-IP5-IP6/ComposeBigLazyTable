package bigLazyTable.paging

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    suspend fun getPage(startIndex: Int, pageSize: Int, filter: String = ""): List<T>

    fun getFilteredCount(filter: String): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}