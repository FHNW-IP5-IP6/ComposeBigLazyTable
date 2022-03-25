package bigLazyTable.data.paging

/**
 * @author Marco Sprenger, Livio Näf
 */
data class Between<T>(
    val fromFilter: Filter,
    val toFilter: Filter
)