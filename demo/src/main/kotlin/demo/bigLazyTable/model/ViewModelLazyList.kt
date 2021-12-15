package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import demo.bigLazyTable.data.database.DBService
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache
import kotlin.math.ceil

private val Log = KotlinLogging.logger {}

/**
 * @author Marco Sprenger, Livio Näf
 */
object ViewModelLazyList {

    private val totalCount = DBService.getTotalCount()
    const val pageSize = 40

    var lastVisibleIndex = 0
    var currentPage = mutableStateOf(0)
    val maxPages = ceil(totalCount.toDouble() / pageSize).toInt() // Example: 10 / 3 = 4

    private const val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistFormModel>> = LruCache(cacheSize)

    init {
        val startIndexFirstPage = 0
        val startIndexSecondPage = pageSize

        val firstPageFormModels = loadPageAndMapToFormModels(startIndexOfPage = startIndexFirstPage)
        val secondPageFormModels = loadPageAndMapToFormModels(startIndexOfPage = startIndexSecondPage)

        addPageToCache(pageNr = 0, pageOfFormModels = firstPageFormModels)
        addPageToCache(pageNr = 1, pageOfFormModels = secondPageFormModels)

        addToAppStateList(startIndex = startIndexFirstPage, 0)
        addToAppStateList(startIndex = startIndexSecondPage, 1)

        selectPlaylist(AppState.lazyModelList.first())
    }

    private fun loadPageAndMapToFormModels(startIndexOfPage: Int): List<PlaylistFormModel> {
        val page = DBService.getPage(startIndex = startIndexOfPage, pageSize = pageSize)
        return page.map { PlaylistFormModel(it) }
    }

    private fun addPageToCache(pageNr: Int, pageOfFormModels: List<PlaylistFormModel>) {
        val elements = pageOfFormModels.toMutableList()
        if (AppState.changedFormModels.size > 0) {
            for (i in 0 until pageSize) {
                if (AppState.changedFormModels.find { playlistFormModel -> playlistFormModel.id.getValue() == elements[i].id.getValue() } != null) {
                    elements[i] =
                        AppState.changedFormModels.find { playlistFormModel -> playlistFormModel.id.getValue() == elements[i].id.getValue() }!!
                    AppState.changedFormModels.remove(elements[i])
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
        currentPage.value = firstVisibleItemIndex / pageSize
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

            val playlistFormModels = loadPageAndMapToFormModels(startIndexOfPage = pageStartIndexToLoad)

            addPageToCache(pageNr = pageNrToLoad, pageOfFormModels = playlistFormModels)

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
                AppState.lazyModelList.set(index = i, element = AppState.defaultPlaylistFormModel)
            }
        }
    }

    fun selectPlaylist(playlistFormModel: PlaylistFormModel) {
        playlistFormModel.setCurrentLanguage(AppState.defaultPlaylistFormModel.getCurrentLanguage())
        AppState.selectedPlaylist = playlistFormModel
    }

}