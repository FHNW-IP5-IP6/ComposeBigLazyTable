package demo.bigLazyTable.model

import androidx.compose.runtime.*
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.data.database.DBService
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.junit.platform.commons.util.LruCache
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil

private val Log = KotlinLogging.logger {}

/**
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableViewModel(pagingService: IPagingService<*>) {

    // TODO: Bad Design?! Service should be added as a parameter not directly called here - testing is impossible like this
    private val totalCount = pagingService.getTotalCount()
    private val pageSize = 40

    var lastVisibleIndex = 0
    var currentPage by mutableStateOf(0)
    val maxPages = ceil(totalCount.toDouble() / pageSize).toInt() // Example: 10 / 3 = 4

    private val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    val scheduler: MutableList<Job> = mutableListOf()
    val schedulerCache: LruCache<Int, Job> = LruCache(2)

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
        val page = DBService.getPage(startIndex = startIndexOfPage, pageSize = pageSize, filter = "")
        return page.map { PlaylistModel(it) }
    }

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

    /**
     * If firstVisibleItemIndex > lastVisibleIndex --> scrolled down
     * If firstVisibleItemIndex < lastVisibleIndex --> scrolled up
     * launch here
     */
    fun loadAllNeededPagesForIndex(firstVisibleItemIndex: Int) {
        currentPage = firstVisibleItemIndex / pageSize
        val scrolledDown = firstVisibleItemIndex > lastVisibleIndex

        CoroutineScope(Dispatchers.IO).launch {
            // load cacheSize pages
            for (i in -1 until cacheSize - 1) {
                val indexToLoad = calculateIndexToLoad(scrolledDown, firstVisibleItemIndex, i)
                if (indexToLoad <= totalCount - pageSize) {
                    loadPage(firstVisibleItemIndex, indexToLoad, scrolledDown)
                }
            }
        }
    }

    private fun calculateIndexToLoad(scrolledDown: Boolean, firstVisibleItemIndex: Int, i: Int): Int {
        return if (scrolledDown) firstVisibleItemIndex + (i * pageSize) else firstVisibleItemIndex - (i * pageSize)
    }

    private suspend fun loadPage(firstVisibleItemIndex: Int, indexToLoad: Int, scrolledDown: Boolean) {
        // Set firstIndex to new value
        lastVisibleIndex = firstVisibleItemIndex

        val pageNrToLoad =
            (indexToLoad / pageSize) - 1 // Example: (1 Mio / 40) = 24.9k because service starts from 0 = total 25k

        if (isPageNotInCache(pageNrToLoad)) {
            val pageStartIndexToLoad = calculatePageStartIndexToLoad(indexToLoad)
            val playlistModels = loadPageAndMapToPlaylistModels(startIndexOfPage = pageStartIndexToLoad)

            addPageToCache(pageNr = pageNrToLoad, pageOfPlaylistModels = playlistModels)

            updateAppStateList(
                pageStartIndexToLoad = pageStartIndexToLoad,
                pageToLoad = pageNrToLoad,
                isEnd = scrolledDown
            )
        }
    }

    private fun isPageNotInCache(pageNrToLoad: Int): Boolean {
        return pageNrToLoad >= 0 && !cache.contains(pageNrToLoad)
    }

    private fun calculatePageStartIndexToLoad(indexToLoad: Int): Int {
        return indexToLoad - (indexToLoad % pageSize)
    }

    private fun updateAppStateList(pageStartIndexToLoad: Int, pageToLoad: Int, isEnd: Boolean) {
        addToAppStateList(startIndex = pageStartIndexToLoad, newPageNr = pageToLoad)
        removeFromAppStateList(index = pageStartIndexToLoad, isEnd = isEnd)
    }

    private fun addToAppStateList(startIndex: Int, newPageNr: Int) {
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

    private fun calculateStartIndexOfOldPage(index: Int, isEnd: Boolean): Int {
        return if (isEnd) {
            index - cacheSize * pageSize
        } else {
            index + cacheSize * pageSize
        }
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
        return isTimeToLoadNextPage(firstVisibleItemIndex) || isTimeToLoadPreviousPage(firstVisibleItemIndex)
    }

    private fun isTimeToLoadNextPage(firstVisibleItemIndex: Int): Boolean {
        val endOfPage = lastVisibleIndex
        return firstVisibleItemIndex > endOfPage
    }

    private fun isTimeToLoadPreviousPage(firstVisibleItemIndex: Int): Boolean {
        val startOfPage = lastVisibleIndex
        return firstVisibleItemIndex < startOfPage
    }

}