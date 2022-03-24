package bigLazyTable.data.paging

/**
 * // TODO: Guet so?
 * This interface must be implemented to use a Service with Compose BigLazyTable.
 *
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    // TODO: Comment needed? Delete?
    /**
     * Load a Page beginning from [startIndex] with size of [pageSize] and given [filters] and [sort] objects.
     */
    fun getPage(startIndex: Int, pageSize: Int, filters: List<Filter> = emptyList(), sort: Sort? = null): List<T>

    fun getFilteredCount(filters: List<Filter>): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

}