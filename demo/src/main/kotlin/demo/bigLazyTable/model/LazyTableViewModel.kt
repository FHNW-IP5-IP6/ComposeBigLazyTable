package demo.bigLazyTable.model

import androidx.compose.runtime.*
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.utils.MathUtils
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache

private val Log = KotlinLogging.logger {}

/**
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableViewModel(private val pagingService: IPagingService<*>, val pageSize: Int = 40) {

    private val totalCount by lazy { pagingService.getTotalCount() }

    private var oldFirstVisibleItemIndex = 0
    var currentPage by mutableStateOf(0)
    val maxPages = MathUtils.roundDivisionToNextBiggerInt(number = totalCount, dividedBy = pageSize)

    private val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    var isScrolling by mutableStateOf(false)

    init {
        val startIndexFirstPage = 0
        val startIndexSecondPage = pageSize

        CoroutineScope(Dispatchers.Main).launch {
            val firstPagePlaylistModels = loadPageAndMapToPlaylistModels(startIndexOfPage = startIndexFirstPage)
            val secondPagePlaylistModels = loadPageAndMapToPlaylistModels(startIndexOfPage = startIndexSecondPage)

            addPageToCache(pageNr = 0, pageOfPlaylistModels = firstPagePlaylistModels)
            addPageToCache(pageNr = 1, pageOfPlaylistModels = secondPagePlaylistModels)

            addToAppStateList(startIndex = startIndexFirstPage, 0)
            addToAppStateList(startIndex = startIndexSecondPage, 1)

            selectPlaylist(AppState.lazyModelList.first()!!)
        }
    }

    private suspend fun loadPageAndMapToPlaylistModels(startIndexOfPage: Int): List<PlaylistModel> {
        val page = pagingService.getPage(startIndex = startIndexOfPage, pageSize = pageSize, filter = "")
        return page.map { PlaylistModel(it as Playlist) }
    }

    // TODO: Split up function -> too complicated
    private fun addPageToCache(pageNr: Int, pageOfPlaylistModels: List<PlaylistModel>) {
        val elements = pageOfPlaylistModels.toMutableList()
        if (AppState.changedPlaylistModels.size > 0) {
            for (i in 0 until pageSize) {
                if (AppState.changedPlaylistModels.find { playlistModel -> playlistModel.id.getValue() == elements[i].id.getValue() } != null) {
                    elements[i] =
                        AppState.changedPlaylistModels.find { playlistModel -> playlistModel.id.getValue() == elements[i].id.getValue() }!!
                    AppState.changedPlaylistModels.remove(elements[i])
                }
            }
        }
        cache[pageNr] = elements
    }

    fun loadAllNeededPagesForIndex(firstVisibleItemIndex: Int) {
//        if (firstVisibleItemIndex < 0) throw IllegalArgumentException("firstVisibleItemIndex should be positive")
//        if (firstVisibleItemIndex > totalCount - 1) throw IllegalArgumentException("firstVisibleItemIndex should be smaller than total count - 1")

        // Calculate current page visible in UI
        currentPage = calculatePageNumberForListIndex(listIndex = firstVisibleItemIndex)

        // If firstVisibleItemIndex > oldFirstVisibleItemIndex --> scrolled down
        // If firstVisibleItemIndex < oldFirstVisibleItemIndex --> scrolled up
        val scrolledDown = firstVisibleItemIndex > oldFirstVisibleItemIndex

        // Update first visible item index with the new value passed by the UI Table
        oldFirstVisibleItemIndex = firstVisibleItemIndex

        // Load cache size pages
        for (i in -1 until cacheSize - 1) {
            val pageToLoad = currentPage + i
            if (pageToLoad in 0..maxPages) {
                loadPage(pageNrToLoad = pageToLoad, scrolledDown = scrolledDown)
            }
        }
    }

    private fun loadPage(pageNrToLoad: Int, scrolledDown: Boolean) {
        if (!isPageInCache(pageNrToLoad)) {
            // Calculate start index for page to load
            val pageStartIndexToLoad = calculatePageStartIndexToLoad(pageNr = pageNrToLoad)

            CoroutineScope(Dispatchers.IO).launch {
                val playlistModels = loadPageAndMapToPlaylistModels(startIndexOfPage = pageStartIndexToLoad)

                addPageToCache(pageNr = pageNrToLoad, pageOfPlaylistModels = playlistModels)

                updateAppStateList(
                    pageStartIndexToLoad = pageStartIndexToLoad,
                    pageToLoad = pageNrToLoad,
                    isEnd = scrolledDown
                )
            }
        }
    }

    private fun updateAppStateList(pageStartIndexToLoad: Int, pageToLoad: Int, isEnd: Boolean) {
        addToAppStateList(startIndex = pageStartIndexToLoad, newPageNr = pageToLoad)
        removeFromAppStateList(index = pageStartIndexToLoad, isEnd = isEnd)
    }

    private fun addToAppStateList(startIndex: Int, newPageNr: Int) {
        println(newPageNr)
        // Add new page to list
        for (i in startIndex until startIndex + pageSize) {
            if (i in 0 until totalCount) {
                AppState.lazyModelList.set(index = i, element = cache[newPageNr]!![i % pageSize])
            }
        }
    }

    private fun removeFromAppStateList(index: Int, isEnd: Boolean) {
        val startIndexOldPage = calculateStartIndexOfOldPage(index, isEnd)
        removeOldPageFromList(startIndexOldPage)
    }

    // TODO: Better namings! What is going on here?
    private fun calculateStartIndexOfOldPage(index: Int, isEnd: Boolean): Int {
        val previousOrNextPage = if (isEnd) index - cacheSize else index + cacheSize
        return previousOrNextPage * pageSize
    }

    private fun removeOldPageFromList(startIndexOldPage: Int) {
        for (i in startIndexOldPage until startIndexOldPage + pageSize) {
            if (i in 0 until totalCount) {
                AppState.lazyModelList.set(index = i, element = null)
            }
        }
    }

    fun selectPlaylist(playlistModel: PlaylistModel) {
        playlistModel.setCurrentLanguage(AppState.defaultPlaylistModel.getCurrentLanguage())
        AppState.selectedPlaylistModel = playlistModel
    }

    fun isTimeToLoadPage(firstVisibleItemIndex: Int): Boolean {
        if (firstVisibleItemIndex < 0) throw IllegalArgumentException("firstVisibleItemIndex should be positive")
        if (firstVisibleItemIndex > totalCount - 1) throw IllegalArgumentException("firstVisibleItemIndex should be smaller than total count - 1")

        val pageNumberForVisibleIndex = calculatePageNumberForListIndex(firstVisibleItemIndex)
        return !isPageInCache(pageNumberForVisibleIndex)
                || !isPageInCache(pageNumberForVisibleIndex - 1)
                || !isPageInCache(pageNumberForVisibleIndex + 1)
                || !isPageInCache(pageNumberForVisibleIndex + 2)
    }

    // Calculate the page number for a given list index
    private fun calculatePageNumberForListIndex(listIndex: Int): Int {
        return listIndex / pageSize
    }

    // Calculate first index from page to load
    private fun calculatePageStartIndexToLoad(pageNr: Int): Int {
        return pageNr * pageSize
    }

    // Check if a given page is in cache
    private fun isPageInCache(pageNr: Int): Boolean {
        return cache.containsKey(pageNr)
    }

}