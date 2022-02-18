package demo.bigLazyTable.model

import androidx.compose.runtime.*
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.utils.PageUtils
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache
import java.util.*

private val Log = KotlinLogging.logger {}

/**
 * TODO: Short description what this class is used for
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableController(
    private val pagingService: IPagingService<*>,
    val pageSize: Int = 40,
    private val appState: AppState
) {
    private val firstPageNr = 0 // TODO: Use Nr everywhere!
    private val firstPageIndex = 0

    var isFiltering by mutableStateOf(false)
        private set

    var nameFilter by mutableStateOf("")
        private set

    fun onNameFilterChanged(newFilter: String) {
        nameFilter = newFilter

        isFiltering = newFilter != ""

        if (isFiltering) {
            val filteredCount = pagingService.getFilteredCount(newFilter)
//            appState.displayedItemsCount = filteredCount
            appState.filteredList = Collections.nCopies(filteredCount, null)

            // TODO: Add Scheduler call
//            scheduler.set {  }
            scheduler.set { loadAllNeededPagesForIndex(0) }

            loadFirstPagesToFillCacheAndAddToAppStateList()
            selectFirstPlaylist()
            // TODO: Check also loadAllNeededPagesForIndex(0) instead of upper 2 calls

            forceRecompose()
        }
    }

    val scheduler = Scheduler()

    private val totalCount by lazy { pagingService.getTotalCount() }
    val totalPages = PageUtils.getTotalPages(totalCount = totalCount, pageSize = pageSize)
//    private var filteredCount = { filter: String -> pagingService.getFilteredCount(filter = filter) }
//    var totalDisplayedItems = if (nameFilter == "") totalCount else filteredCount(nameFilter)

    private var oldFirstVisibleItemIndex = firstPageIndex

    private val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    var recomposeStateChanger by mutableStateOf(false)

    init {
        // Get first cacheSize=4 pages on app initialization, to select one for the forms
        CoroutineScope(Dispatchers.Main).launch {
//            appState.displayedItemsCount = totalCount
            loadFirstPagesToFillCacheAndAddToAppStateList()
            selectFirstPlaylist()
        }
    }

    private fun loadFirstPagesToFillCacheAndAddToAppStateList() {
        for (pageNr in firstPageNr until cacheSize) {
            val startIndexOfPage = pageNr * pageSize
            val models = loadPageOfPlaylistModels(startIndexOfPage = startIndexOfPage)
            addPageToCache(pageNr = pageNr, pageOfModels = models)
            addToAppStateList(startIndex = startIndexOfPage, newPageNr = pageNr)
        }
    }

    private fun selectFirstPlaylist() {
        val fullList = if (isFiltering) appState.filteredList else appState.lazyModelList
        println("loadFirstPagesAndFillCacheAndSelectFirstPlaylist: appState.list size = ${fullList.size}")
        // TODO: NullPointerException
        selectPlaylistModel(fullList.first()!!)
    }

    fun loadAllNeededPagesForIndex(firstVisibleItemIndex: Int) {
        // Calculate current page visible in UI
        val currPage = getPageNr(firstVisibleItemIndex = firstVisibleItemIndex)

        // If firstVisibleItemIndex > oldFirstVisibleItemIndex --> scrolled down
        // If firstVisibleItemIndex < oldFirstVisibleItemIndex --> scrolled up
        val scrolledDown = firstVisibleItemIndex > oldFirstVisibleItemIndex

        // Update oldFirstVisibleItemIndex with the new value passed by the UI Table
        oldFirstVisibleItemIndex = firstVisibleItemIndex

        // Load cache size pages
        for (i in -1 until cacheSize - 1) {
            val pageToLoad = currPage + i
            if (pageToLoad in firstPageNr until totalPages) {
                loadPage(pageNrToLoad = pageToLoad, scrolledDown = scrolledDown)
            }
        }
        forceRecompose()
    }

    internal fun loadPage(pageNrToLoad: Int, scrolledDown: Boolean) {
        if (!isPageInCache(pageNrToLoad)) {
            // Calculate start index for page to load
            val pageStartIndexToLoad = getStartIndexOfPage(pageNr = pageNrToLoad)

            //val playlistModels = requestDataAsync(scope = pagingScope, startIndexOfPage = pageStartIndexToLoad)
            val playlistModels = loadPageOfPlaylistModels(pageStartIndexToLoad)
            //addPageToCache(pageNr = pageNrToLoad, pageOfModels = playlistModels.await())
            addPageToCache(pageNr = pageNrToLoad, pageOfModels = playlistModels)

            updateAppStateList(
                pageStartIndexToLoad = pageStartIndexToLoad,
                pageToLoad = pageNrToLoad,
                isEnd = scrolledDown
            )
        }
    }

    internal fun loadPageOfPlaylistModels(startIndexOfPage: Int): List<PlaylistModel> {
        println("loadPageAndMapToModels index=$startIndexOfPage filter=$nameFilter")
        val page = pagingService.getPage(startIndex = startIndexOfPage, pageSize = pageSize, filter = nameFilter)
        return page.toPlaylistModels()
    }

    private fun List<Any?>.toPlaylistModels(): List<PlaylistModel> {
        return map { PlaylistModel(it as Playlist, appState) }
    }

    // TODO: Split up function -> too complicated
    internal fun addPageToCache(pageNr: Int, pageOfModels: List<PlaylistModel>) {
        val elements = pageOfModels.toMutableList()
        if (appState.changedPlaylistModels.size > 0) {
            for (i in firstPageIndex until pageSize) {
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
        if (isFiltering) {
            for (i in startIndex until startIndex + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.filteredList.set(index = i, element = cache[newPageNr]!![i % pageSize])
                }
            }
        }
        else {
            // Add new page to list
            for (i in startIndex until startIndex + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.lazyModelList.set(index = i, element = cache[newPageNr]!![i % pageSize])
                }
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
        return previousOrNextPage * pagingService.pageSize
    }

    internal fun removeOldPageFromList(startIndexOldPage: Int) {
        assert(startIndexOldPage >= firstPageIndex)

        if (isFiltering) {
            for (i in startIndexOldPage until startIndexOldPage + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.filteredList.set(index = i, element = null)
                }
            }
        }
        else {
            for (i in startIndexOldPage until startIndexOldPage + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.lazyModelList.set(index = i, element = null)
                }
            }
        }

    }

    fun selectPlaylistModel(playlistModel: PlaylistModel) {
        setCurrentLanguage(playlistModel = playlistModel)
        appState.selectedPlaylistModel = playlistModel
    }

    private fun setCurrentLanguage(playlistModel: PlaylistModel) {
        val currentLanguage = appState.defaultPlaylistModel.getCurrentLanguage()
        playlistModel.setCurrentLanguage(currentLanguage)
    }

    // Checks with the passed firstVisibleItemIndex from the UI, if it's time to load new pages
    fun isTimeToLoadPage(firstVisibleItemIndex: Int): Boolean {
        if (firstVisibleItemIndex < firstPageIndex) throw IllegalArgumentException("firstVisibleItemIndex should be positive")
        if (firstVisibleItemIndex >= totalCount) throw IllegalArgumentException("firstVisibleItemIndex should be smaller than total count")

        val pageNumberForVisibleIndex = getPageNr(firstVisibleItemIndex)

        // Catch all edge cases, to load only data if necessarily
        if (pageNumberForVisibleIndex == firstPageIndex) {
            return !isPageInCache(pageNumberForVisibleIndex)
                    || !isPageInCache(1)
                    || !isPageInCache(2)
        } else if (pageNumberForVisibleIndex > totalPages) {
            return !isPageInCache(pageNumberForVisibleIndex)
                    || !isPageInCache(pageNumberForVisibleIndex - 1)
                    || !isPageInCache(pageNumberForVisibleIndex + 1)
        } else if (pageNumberForVisibleIndex > totalPages - 1) {
            return !isPageInCache(pageNumberForVisibleIndex)
                    || !isPageInCache(pageNumberForVisibleIndex - 1)
        } else {
            return !isPageInCache(pageNumberForVisibleIndex)
                    || !isPageInCache(pageNumberForVisibleIndex - 1)
                    || !isPageInCache(pageNumberForVisibleIndex + 1)
                    || !isPageInCache(pageNumberForVisibleIndex + 2)
        }
    }

    internal fun getPageNr(firstVisibleItemIndex: Int): Int = firstVisibleItemIndex / pageSize

    internal fun getStartIndexOfPage(pageNr: Int): Int = pageNr * pageSize

    internal fun isPageInCache(pageNr: Int): Boolean = cache.containsKey(pageNr)

    private fun forceRecompose() {
        recomposeStateChanger = !recomposeStateChanger
    }

}