package demo.bigLazyTable.data

import demo.bigLazyTable.model.Playlist

class BltRepository(private val service: DBService) {

    val START_PAGE = 0
    val PAGE_SIZE = 10 // calculate how much elements are visible in the UI
    val initialLoad = 3 * PAGE_SIZE

    private val inMemoryCache = mutableListOf<Playlist>()

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = START_PAGE

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    fun loadFirstPage(): List<Playlist> {
        isRequestInProgress = true
        val playlists = service.getPage(start = START_PAGE, pageSize = PAGE_SIZE)
        isRequestInProgress = false
        return playlists
    }

    fun loadNextPage() {}

    fun loadPreviousPage() {}
}