package bigLazyTable.paging

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<Full, Short> {

    fun getPage(startIndex: Int, pageSize: Int, filter: String = ""): List<Full>

    fun getFilteredCount(filter: String): Int

    fun getTotalCount(): Int

    fun get(id: Long): Short

    fun indexOf(id: Long, filter: String): Int

    fun update(dao: Full): Boolean

    //fun create(): EntityKey

}