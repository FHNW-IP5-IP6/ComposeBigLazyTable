package bigLazyTable.data.paging

data class Between<T>(
    val fromFilter: Filter,
    val toFilter: Filter
)