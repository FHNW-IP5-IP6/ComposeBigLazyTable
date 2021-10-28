package demo.bigLazyTable.data

import demo.bigLazyTable.model.Playlist

sealed class LoadResult {

    data class Page(
        val data: PagingData<Playlist>,
        val nextKey: Int?,
        val prevKey: Int?
    )

    data class Error(val throwable: Throwable)

}