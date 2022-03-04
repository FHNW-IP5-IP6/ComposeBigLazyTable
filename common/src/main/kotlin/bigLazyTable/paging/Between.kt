package bigLazyTable.paging

data class Between<T>(
    val fromFilter: Filter,
    val toFilter: Filter,
    val includeFrom: Boolean,
    val includeTo: Boolean
)