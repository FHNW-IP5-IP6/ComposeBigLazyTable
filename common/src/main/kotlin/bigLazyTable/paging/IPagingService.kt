package bigLazyTable.paging

/**
 * @author Marco Sprenger, Livio Näf
 */
interface IPagingService<T> {

    // TODO: Only val is allowed here
    //  Thought about adding this here to set/change the pageSize only at the beginning
    val pageSize: Int
        get() = 40 // TODO: Or should we give it a default value?

    // TODO: Does it make sense to add a default pageSize=40?
    //  What happens if the user of our API varies the pageSize -> wouldnt that blow up our Table & how it is structured?
    //  Is there a way to pass the pageSize as soon as possible & after that it will stay the same?

    // TODO: Do we even need pageSize as a parameter if it is always the same value which will be set at the beginning?
    //  Because now the Service has a reference to the pageSize and it will make it duplicated
    // TODO: does it make sense to add a Filter Object with filter: String & caseSensitive: Boolean as Parameters?
    suspend fun getPage(startIndex: Int, pageSize: Int = this.pageSize, filter: String = "", caseSensitive: Boolean = false): List<T>

    fun getFilteredCount(filter: String, caseSensitive: Boolean = false): Int

    fun getTotalCount(): Int

    fun get(id: Long): T

    fun indexOf(id: Long, filter: String = ""): Int

    //fun update(dao: T): Boolean

    //fun create(): EntityKey

}