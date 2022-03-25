package bigLazyTable.data.paging

/**
 * This interface must be implemented to use a Service with Compose BigLazyTable.
 *
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    /**
     * Load a Page beginning from [startIndex] with size of [pageSize] and given [filters] and [sort] objects.
     */
    fun getPage(startIndex: Int, pageSize: Int, filters: List<Filter> = emptyList(), sort: Sort? = null): List<T>

    /**
     * Get number of elements with given [filters].
     */
    fun getFilteredCount(filters: List<Filter>): Int

    /**
     * Get total number of elements.
     */
    fun getTotalCount(): Int

    /**
     * Get element by [id].
     */
    fun get(id: Long): T

    /**
     * Get index of element with given [id] and [filters].
     */
    fun indexOf(id: Long, filters: List<Filter> = emptyList()): Int

}