package demo.bigLazyTable.model

import androidx.compose.runtime.*
import demo.bigLazyTable.data.database.DBService
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache
import kotlin.math.ceil

private val Log = KotlinLogging.logger {}

/**
 * @author Marco Sprenger, Livio Näf
 */
object LazyTableViewModel {

    private val totalCount = DBService.getTotalCount()
    private const val pageSize = 40

    var lastVisibleIndex = 0
    var currentPage by mutableStateOf(0)
    val maxPages = ceil(totalCount.toDouble() / pageSize).toInt() // Example: 10 / 3 = 4

    private const val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    var hasError by mutableStateOf(false)

    init {
        val startIndexFirstPage = 0
        val startIndexSecondPage = pageSize

        val firstPagePlaylistModels = loadPageAndMapToPlaylistModels(startIndexOfPage = startIndexFirstPage)
        val secondPagePlaylistModels = loadPageAndMapToPlaylistModels(startIndexOfPage = startIndexSecondPage)

        addPageToCache(pageNr = 0, pageOfPlaylistModels = firstPagePlaylistModels)
        addPageToCache(pageNr = 1, pageOfPlaylistModels = secondPagePlaylistModels)

        addToAppStateList(startIndex = startIndexFirstPage, 0)
        addToAppStateList(startIndex = startIndexSecondPage, 1)

        selectPlaylist(AppState.lazyModelList.first()!!)
    }

    private fun loadPageAndMapToPlaylistModels(startIndexOfPage: Int): List<PlaylistModel> {
        val page = DBService.getPage(startIndex = startIndexOfPage, pageSize = pageSize)
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
     */
    fun loadAllNeededPagesFor(firstVisibleItemIndex: Int) {
        currentPage = firstVisibleItemIndex / pageSize
        val scrolledDown = firstVisibleItemIndex > lastVisibleIndex

        // load cacheSize pages
        for (i in -1 until cacheSize - 1) {
            val indexToLoad = calculateIndexToLoad(scrolledDown, firstVisibleItemIndex, i)
            loadPage(firstVisibleItemIndex, indexToLoad, scrolledDown)
        }
    }

    private fun calculateIndexToLoad(scrolledDown: Boolean, firstVisibleItemIndex: Int, i: Int): Int {
        return if (scrolledDown) firstVisibleItemIndex + (i * pageSize) else firstVisibleItemIndex - (i * pageSize)
    }

    private fun loadPage(firstVisibleItemIndex: Int, indexToLoad: Int, scrolledDown: Boolean) {
        // Set firstIndex to new value
        lastVisibleIndex = firstVisibleItemIndex

        val pageNrToLoad = indexToLoad / pageSize // Example: 53 / 40 = 1

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
                AppState.lazyModelList.set(index = i, element = AppState.defaultPlaylistModel)
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
        val endOfPage = lastVisibleIndex + pageSize
        return firstVisibleItemIndex > endOfPage
    }

    private fun isTimeToLoadPreviousPage(firstVisibleItemIndex: Int): Boolean {
        val startOfPage = lastVisibleIndex - pageSize
        return firstVisibleItemIndex < startOfPage
    }

}