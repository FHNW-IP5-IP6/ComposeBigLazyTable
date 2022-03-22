package bigLazyTable.data.paging

/**
 * // TODO: Add description?
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    fun getPage(startIndex: Int, pageSize: Int, filters: List<Filter> = emptyList(), sort: Sort? = null): List<T>

    fun getFilteredCount(filters: List<Filter>): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

}