package demo.bigLazyTable.data

abstract class PagingSource<Key, Datatype, DataService, Query> {

    abstract suspend fun load(loadParams: LoadParams<Key>): LoadResult<Key, Datatype>

    abstract fun getRefreshKey(pagingState: PagingState<Key, Datatype>): Key?

}