package demo.bigLazyTable.model

import androidx.compose.runtime.*
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.utils.MathUtils
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache

private val Log = KotlinLogging.logger {}

/**
 * TODO: Short description what this class is used for
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableViewModel(
    private val pagingService: IPagingService<*>,
    val pageSize: Int = 40,
    private val appState: AppState
) {
    val firstPageNr = 0 // TODO: Nr or Nbr?
    val firstPageIndex = 0

    val scheduler = Scheduler

    private val totalCount by lazy { pagingService.getTotalCount() }

    private var oldFirstVisibleItemIndex = 0
    var recomposeStateChanger by mutableStateOf(false)
    val nbrOfTotalPages = MathUtils.roundDivisionToNextBiggerInt(number = totalCount, dividedBy = pageSize)

    private val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    init {
        // Get first 4 pages on app initialization, to select one for the forms
        CoroutineScope(Dispatchers.Main).launch {
            for (pageNr in 0 until cacheSize) {
                val startIndexOfPage = pageNr * pageSize
                val models = loadPageAndMapToModels(startIndexOfPage = startIndexOfPage)
                addPageToCache(pageNr = pageNr, pageOfModels = models)
                addToAppStateList(startIndex = startIndexOfPage, newPageNr = pageNr)
            }
            selectPlaylist(appState.lazyModelList.first()!!)
        }
    }

    fun loadAllNeededPagesForIndex(firstVisibleItemIndex: Int) {
        // Calculate current page visible in UI
        val currPage = calculatePageNumberForListIndex(listIndex = firstVisibleItemIndex)

        // If firstVisibleItemIndex > oldFirstVisibleItemIndex --> scrolled down
        // If firstVisibleItemIndex < oldFirstVisibleItemIndex --> scrolled up
        val scrolledDown = firstVisibleItemIndex > oldFirstVisibleItemIndex

        // Update oldFirstVisibleItemIndex with the new value passed by the UI Table
        oldFirstVisibleItemIndex = firstVisibleItemIndex

        // Load cache size pages
        for (i in -1 until cacheSize - 1) {
            val pageToLoad = currPage + i
            if (pageToLoad in 0 until nbrOfTotalPages) {
                loadPage(pageNrToLoad = pageToLoad, scrolledDown = scrolledDown)
            }
        }
        forceRecompose()
    }

    internal fun loadPage(pageNrToLoad: Int, scrolledDown: Boolean) {
        if (!isPageNrInCache(pageNrToLoad)) {
            // Calculate start index for page to load
            val pageStartIndexToLoad = calculatePageStartIndexToLoad(pageNr = pageNrToLoad)

            //val playlistModels = requestDataAsync(scope = pagingScope, startIndexOfPage = pageStartIndexToLoad)
            val playlistModels = loadPageAndMapToModels(pageStartIndexToLoad)
            //addPageToCache(pageNr = pageNrToLoad, pageOfModels = playlistModels.await())
            addPageToCache(pageNr = pageNrToLoad, pageOfModels = playlistModels)

            updateAppStateList(
                pageStartIndexToLoad = pageStartIndexToLoad,
                pageToLoad = pageNrToLoad,
                isEnd = scrolledDown
            )
        }
    }

    internal fun loadPageAndMapToModels(startIndexOfPage: Int): List<PlaylistModel> {
        val page = pagingService.getPage(startIndex = startIndexOfPage, pageSize = pageSize, filter = "")
        return page.map { PlaylistModel(it as Playlist, appState) }
    }

    // TODO: Split up function -> too complicated
    internal fun addPageToCache(pageNr: Int, pageOfModels: List<PlaylistModel>) {
        val elements = pageOfModels.toMutableList()
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

    // TODO: Add below function
    internal fun mergeModels() {}

    internal fun updateAppStateList(pageStartIndexToLoad: Int, pageToLoad: Int, isEnd: Boolean) {
        addToAppStateList(startIndex = pageStartIndexToLoad, newPageNr = pageToLoad)
        removeFromAppStateList(index = pageStartIndexToLoad, isEnd = isEnd)
    }

    internal fun addToAppStateList(startIndex: Int, newPageNr: Int) {
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

    // Calculates the start index of an "old" page, which has to be removed from the AppStateList
    // TODO: isEnd? index? previousOrNextPage? index +- cacheSize?
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

    // TODO: selectPlaylistWithLanguageSet, selectModel, setLanguageAndSelectModel
    fun selectPlaylist(playlistModel: PlaylistModel) {
        println("selectPlaylist ${playlistModel.id}")
        setCurrentLanguage(playlistModel = playlistModel)
        appState.selectedPlaylistModel = playlistModel
    }

    private fun setCurrentLanguage(playlistModel: PlaylistModel) {
        val currentLanguage = playlistModel.getCurrentLanguage()
        playlistModel.setCurrentLanguage(currentLanguage)
    }

    // Checks with the passed firstVisibleItemIndex from the UI, if it's time to load new pages
    fun isTimeToLoadPage(firstVisibleItemIndex: Int): Boolean {
        if (firstVisibleItemIndex < 0) throw IllegalArgumentException("firstVisibleItemIndex should be positive")
        if (firstVisibleItemIndex >= totalCount) throw IllegalArgumentException("firstVisibleItemIndex should be smaller than total count")

        val pageNumberForVisibleIndex = calculatePageNumberForListIndex(firstVisibleItemIndex)

        // Catch all edge cases, to load only data if necessarily
        if (pageNumberForVisibleIndex == 0) {
            return !isPageNrInCache(pageNumberForVisibleIndex)
                    || !isPageNrInCache(1)
                    || !isPageNrInCache(2)
        } else if (pageNumberForVisibleIndex > nbrOfTotalPages) {
            return !isPageNrInCache(pageNumberForVisibleIndex)
                    || !isPageNrInCache(pageNumberForVisibleIndex - 1)
                    || !isPageNrInCache(pageNumberForVisibleIndex + 1)
        } else if (pageNumberForVisibleIndex > nbrOfTotalPages - 1) {
            return !isPageNrInCache(pageNumberForVisibleIndex)
                    || !isPageNrInCache(pageNumberForVisibleIndex - 1)
        } else {
            return !isPageNrInCache(pageNumberForVisibleIndex)
                    || !isPageNrInCache(pageNumberForVisibleIndex - 1)
                    || !isPageNrInCache(pageNumberForVisibleIndex + 1)
                    || !isPageNrInCache(pageNumberForVisibleIndex + 2)
        }
    }

    internal fun calculatePageNumberForListIndex(listIndex: Int): Int = listIndex / pageSize

    internal fun calculatePageStartIndexToLoad(pageNr: Int): Int = pageNr * pageSize

    internal fun isPageNrInCache(pageNr: Int): Boolean = cache.containsKey(pageNr)

    private fun forceRecompose() {
        recomposeStateChanger = !recomposeStateChanger
    }

}