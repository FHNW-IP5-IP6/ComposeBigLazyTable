package demo.bigLazyTable.model

import androidx.compose.runtime.*
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.utils.MathUtils
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache

/**
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableViewModel(
    private val pagingService: IPagingService<*>,
    val pageSize: Int = 40,
    private val appState: AppState
) {

    private val totalCount by lazy { pagingService.getTotalCount() }

    private var oldFirstVisibleItemIndex = 0
    var currentPage by mutableStateOf(0)
    val nbrOfTotalPages = MathUtils.roundDivisionToNextBiggerInt(number = totalCount, dividedBy = pageSize)

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

            selectPlaylist(appState.lazyModelList.first()!!)
        }
    }

    internal suspend fun loadPageAndMapToPlaylistModels(startIndexOfPage: Int): List<PlaylistModel> {
        val page = pagingService.getPage(startIndex = startIndexOfPage, pageSize = pageSize, filter = "")
        return page.map { PlaylistModel(it as Playlist, appState) }
    }

    // TODO: Split up function -> too complicated
    internal fun addPageToCache(pageNr: Int, pageOfPlaylistModels: List<PlaylistModel>) {
        val elements = pageOfPlaylistModels.toMutableList()
        if (appState.changedPlaylistModels.size > 0) {
            for (i in 0 until pageSize) {
                if (appState.changedPlaylistModels.find { playlistModel -> playlistModel.id.getValue() == elements[i].id.getValue() } != null) {
                    elements[i] =
                        appState.changedPlaylistModels.find { playlistModel -> playlistModel.id.getValue() == elements[i].id.getValue() }!!
                    appState.changedPlaylistModels.remove(elements[i])
                }
            }
        }
        cache[pageNr] = elements
    }

    fun loadAllNeededPagesForIndex(firstVisibleItemIndex: Int) {
        if (firstVisibleItemIndex < 0) throw IllegalArgumentException("firstVisibleItemIndex should be positive")
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
            if (pageToLoad in 0..nbrOfTotalPages) {
                loadPage(pageNrToLoad = pageToLoad, scrolledDown = scrolledDown)
            }
        }
    }

    internal fun loadPage(pageNrToLoad: Int, scrolledDown: Boolean) {
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

    internal fun updateAppStateList(pageStartIndexToLoad: Int, pageToLoad: Int, isEnd: Boolean) {
        addToAppStateList(startIndex = pageStartIndexToLoad, newPageNr = pageToLoad)
        removeFromAppStateList(index = pageStartIndexToLoad, isEnd = isEnd)
    }

    internal fun addToAppStateList(startIndex: Int, newPageNr: Int) {
        println(newPageNr)
        // Add new page to list
        for (i in startIndex until startIndex + pageSize) {
            if (i in 0 until totalCount) {
                appState.lazyModelList.set(index = i, element = cache[newPageNr]!![i % pageSize])
            }
        }
    }

    internal fun removeFromAppStateList(index: Int, isEnd: Boolean) {
        val startIndexOldPage = calculateStartIndexOfOldPage(index, isEnd)
        removeOldPageFromList(startIndexOldPage)
    }

    // TODO: Better namings! What is going on here?
    internal fun calculateStartIndexOfOldPage(index: Int, isEnd: Boolean): Int {
        val previousOrNextPage = if (isEnd) index - cacheSize else index + cacheSize
        return previousOrNextPage * pageSize
    }

    internal fun removeOldPageFromList(startIndexOldPage: Int) {
        assert(startIndexOldPage >= 0)
        for (i in startIndexOldPage until startIndexOldPage + pageSize) {
            if (i in 0 until totalCount) {
                appState.lazyModelList.set(index = i, element = null)
            }
        }
    }

    fun selectPlaylist(playlistModel: PlaylistModel) {
        playlistModel.setCurrentLanguage(appState.defaultPlaylistModel.getCurrentLanguage())
        appState.selectedPlaylistModel = playlistModel
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
    internal fun calculatePageNumberForListIndex(listIndex: Int): Int {
        return listIndex / pageSize
    }

    // Calculate first index from page to load
    internal fun calculatePageStartIndexToLoad(pageNr: Int): Int {
        return pageNr * pageSize
    }

    // Check if a given page is in cache
    internal fun isPageInCache(pageNr: Int): Boolean {
        return cache.containsKey(pageNr)
    }

}