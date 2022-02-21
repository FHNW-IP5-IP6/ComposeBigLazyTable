package bigLazyTable.paging

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    // TODO: Do we even need pageSize as a parameter if it is always the same value which will be set at the beginning?
    //  Because now the Service has a reference to the pageSize and it will make it duplicated
    // TODO: does it make sense to add a Filter Object with filter: String & caseSensitive: Boolean as Parameters?
    fun getPage(startIndex: Int, pageSize: Int, filter: String = "", caseSensitive: Boolean = false, sorted: String = ""): List<T>

    fun getFilteredCount(filter: String, caseSensitive: Boolean = false): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}