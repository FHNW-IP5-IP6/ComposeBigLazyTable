package bigLazyTable.paging

const val PAGE_SIZE = 30

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
interface IPagingService<T> {

    fun getPage(start: Int, pageSize: Int = PAGE_SIZE, filter: String = ""): List<T>

    fun getFilteredCount(filter: String): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String): Int

    //fun create(): EntityKey

}