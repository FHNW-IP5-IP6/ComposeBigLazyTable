package demo.bigLazyTable.model

import androidx.compose.runtime.*
import bigLazyTable.paging.Filter
import bigLazyTable.paging.IPagingService
import bigLazyTable.paging.Sort
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.utils.PageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Column
import org.junit.platform.commons.util.LruCache
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

private val Log = KotlinLogging.logger {}

/**
 * TODO: Short description what this class is used for
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableController(
    private val pagingService: IPagingService<*>,
    val pageSize: Int = 40, // TODO: make dynamic
    private val appState: AppState
) {
    private val firstPageNr = 0 // TODO: Use Nr everywhere!
    private val firstPageIndex = 0

    var lastSortedAttribute: Attribute<*, *, *>? = null
    var attributeSort = mutableStateMapOf<Attribute<*, *, *>, BLTSortOrder>()
    var sort: Sort? by mutableStateOf(null)
    var isSorting by mutableStateOf(false)

    fun onSortChanged(attribute: Attribute<*, *, *>, newSortOrder: BLTSortOrder) {
        resetPreviousSortedAttribute(newAttribute = attribute)

        lastSortedAttribute = attribute
        attributeSort[attribute] = newSortOrder

        isSorting = newSortOrder.isSorting
        sort = newSortOrder.sortAttribute(attribute)

        scheduler.scheduleTask { loadFirstPagesToFillCacheAndAddToAppStateList() }
    }

    private fun resetPreviousSortedAttribute(newAttribute: Attribute<*,*,*>) {
        if (lastSortedAttribute != null && lastSortedAttribute != newAttribute) {
            attributeSort[lastSortedAttribute!!] = BLTSortOrder.None
        }
    }

    var filters = listOf<Filter>()
    private var filteredAttributes = mutableSetOf<Attribute<*, *, *>>()
    var attributeFilter: MutableMap<Attribute<*, *, *>, String> = mutableStateMapOf()

    // TODO: Spinner instead of empty ... Rows when filtering?
    fun onFiltersChanged(attribute: Attribute<*, *, *>, newFilter: String) {
        println("Inside onFiltersChanged with attribute=${attribute.databaseField}, newFilter=$newFilter")
        val start = System.currentTimeMillis()
        attributeFilter[attribute] = newFilter

        if (newFilter == "") filteredAttributes.remove(attribute)
        else filteredAttributes.add(attribute)

        filters = filteredAttributes.map { a ->
            Filter(
                filter = attributeFilter[a] ?: "",
                dbField = a.databaseField as Column<String>,
                caseSensitive = false
            )
        }
        println("Filters in onFiltersChanged: $filters")

        isFiltering = filters.isNotEmpty()
        if (isFiltering) {
            filterScheduler.scheduleTask {
                println("Before getFilteredCountNew")
                val start1 = System.currentTimeMillis()
                // getFilteredCountNew needed 617 ms
                filteredCount = pagingService.getFilteredCount(filters = filters)
                val end1 = System.currentTimeMillis()
                println("getFilteredCountNew needed ${end1 - start1} ms")

                println("filtered Count = $filteredCount")
                appState.filteredTableModelList = ArrayList(Collections.nCopies(filteredCount, null))

                loadFirstPagesToFillCacheAndAddToAppStateList()
            }
        }
        val end = System.currentTimeMillis()
        println("onFiltersChanged needed ${end - start} ms")
    }

    var isFiltering by mutableStateOf(false)
        private set

    val scheduler = Scheduler()
    private val filterScheduler = Scheduler(100)

    private val totalCount by lazy { pagingService.getTotalCount() }
    private var filteredCount by Delegates.notNull<Int>()
    val totalPages = PageUtils.getTotalPages(totalCount = totalCount, pageSize = pageSize)

    private var oldFirstVisibleItemIndex = firstPageIndex

    private val cacheSize = 4
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    var recomposeStateChanger by mutableStateOf(false)

    init {
        appState.defaultTableModel.displayedAttributesInTable.forEach { attribute ->
            if (attribute.canBeFiltered) {
                attributeFilter[attribute] = ""
            }
            attributeSort[attribute] = BLTSortOrder.None
        }
        println("attributeFilter: ${attributeFilter.size}")
        println("attributeSort: ${attributeSort.size}")
        // Get first cacheSize=4 pages on app initialization, to select one for the forms
        CoroutineScope(Dispatchers.Main).launch {
            loadFirstPagesToFillCacheAndAddToAppStateList()
            selectFirstPlaylist()
        }
    }

    // loadFirstPagesToFillCacheAndAddToAppStateList needed 220 ms
    private fun loadFirstPagesToFillCacheAndAddToAppStateList() {
        println("Inside loadFirstPagesToFillCacheAndAddToAppStateList")
        val start = System.currentTimeMillis()
        for (pageNr in firstPageNr until cacheSize) {
            val startIndexOfPage = pageNr * pageSize
            val models = loadPageOfPlaylistModels(startIndexOfPage = startIndexOfPage)
            addPageToCache(pageNr = pageNr, pageOfModels = models)
            addToAppStateList(startIndex = startIndexOfPage, newPageNr = pageNr)
        }
        forceRecompose()
        val end = System.currentTimeMillis()
        println("loadFirstPagesToFillCacheAndAddToAppStateList needed ${end - start} ms")
    }

    private fun selectFirstPlaylist() {
        val fullList = if (isFiltering) appState.filteredTableModelList else appState.tableModelList
        println("loadFirstPagesAndFillCacheAndSelectFirstPlaylist: appState.list size = ${fullList.size}")
        fullList.first()?.let { firstPlaylist -> selectModel(firstPlaylist) }
    }

    // sort: loadAllNeededPagesForIndex needed 23552 ms
    fun loadAllNeededPagesForIndex(firstVisibleItemIndex: Int) {
        println("Inside loadAllNeededPagesForIndex with firstVisibleItemIndex=$firstVisibleItemIndex")
        val start = System.currentTimeMillis()
        val currentPageNr = getVisiblePageNr(firstVisibleItemIndex = firstVisibleItemIndex)

        // If firstVisibleItemIndex > oldFirstVisibleItemIndex --> scrolled down
        // If firstVisibleItemIndex < oldFirstVisibleItemIndex --> scrolled up
        val scrolledDown = firstVisibleItemIndex > oldFirstVisibleItemIndex

        // Update oldFirstVisibleItemIndex with the new value passed by the UI Table
        oldFirstVisibleItemIndex = firstVisibleItemIndex

        // Load cache size pages
        for (i in -1 until cacheSize - 1) {
            val pageNr = currentPageNr + i

            // TODO: TotalPages is always with total count
            //  Must this be set again but with filteredCount like below?
//              totalPages = PageUtils.getTotalPages(totalCount = filteredCount, pageSize = pageSize)
            if (pageNr in firstPageNr until totalPages) {
                loadPage(pageNr = pageNr, scrolledDown = scrolledDown)
            }
        }
        forceRecompose()
        val end = System.currentTimeMillis()
        println("loadAllNeededPagesForIndex needed ${end - start} ms")
    }

    // sort: loadPage needed 6112 ms
    internal fun loadPage(pageNr: Int, scrolledDown: Boolean) {
        println("Inside loadPage with pageNr=$pageNr, scrolledDown=$scrolledDown")
        val start = System.currentTimeMillis()
        if (!isPageInCache(pageNr)) {
            // Calculate start index for page to load
            val pageStartIndexToLoad = getFirstIndexOfPage(pageNr = pageNr)

            //val playlistModels = requestDataAsync(scope = pagingScope, startIndexOfPage = pageStartIndexToLoad)
            val playlistModels = loadPageOfPlaylistModels(pageStartIndexToLoad)
            //addPageToCache(pageNr = pageNrToLoad, pageOfModels = playlistModels.await())
            addPageToCache(pageNr = pageNr, pageOfModels = playlistModels)

            updateAppStateList(
                pageStartIndexToLoad = pageStartIndexToLoad,
                pageToLoad = pageNr,
                isEnd = scrolledDown
            )
        }
        val end = System.currentTimeMillis()
        println("loadPage needed ${end - start} ms")
    }

    // TODO: Instead of string more generic
    // takes as much time as getPageNew call
    internal fun loadPageOfPlaylistModels(startIndexOfPage: Int): List<PlaylistModel> {
        println("Inside loadPageOfPlaylistModels")
        val start = System.currentTimeMillis()
        println("Filters in loadPageOfPlaylistModels: $filters")

        println("Sort = $sort")

        println("Before getPageNew")
        val start1 = System.currentTimeMillis()
        // getPage needed 745 ms (normal max with filters)
        // getPage needed 24837 ms (max with sort)
        // getPage needed 75882 ms (sort modified_at)
        val page = pagingService.getPage(
            startIndex = startIndexOfPage,
            pageSize = pageSize,
            filters = filters,
            sort = sort
        )
        val end1 = System.currentTimeMillis()
        println("getPage needed ${end1 - start1} ms")

        val end = System.currentTimeMillis()
        println("loadPageOfPlaylistModels needed ${end - start} ms")
        return page.toPlaylistModels()
    }

    private fun List<Any?>.toPlaylistModels(): List<PlaylistModel> {
        return map { PlaylistModel(it as Playlist, appState) }
    }

    // TODO: Split up function -> too complicated
    internal fun addPageToCache(pageNr: Int, pageOfModels: List<PlaylistModel>) {
        println("Inside addPageToCache with pageNr=$pageNr")
        val start = System.currentTimeMillis()
        val elements = pageOfModels.toMutableList()
        if (appState.changedTableModels.size > 0) {
            for (i in firstPageIndex until pageSize) {
                if (appState.changedTableModels.find { playlistModel -> playlistModel.id.getValue() == elements[i].id.getValue() } != null) {
                    elements[i] =
                        appState.changedTableModels.find { playlistModel -> playlistModel.id.getValue() == elements[i].id.getValue() }!!
                    appState.changedTableModels.remove(elements[i])
                }
            }
        }
        cache[pageNr] = elements
        val end = System.currentTimeMillis()
        println("addPageToCache needed ${end - start} ms")
    }

    // TODO: Add below function
    internal fun mergeModels() {}

    internal fun updateAppStateList(pageStartIndexToLoad: Int, pageToLoad: Int, isEnd: Boolean) {
        println("Inside updateAppStateList with pageStartIndexToLoad=$pageStartIndexToLoad, pageToLoad=$pageToLoad, isEnd=$isEnd")
        val start = System.currentTimeMillis()
        addToAppStateList(startIndex = pageStartIndexToLoad, newPageNr = pageToLoad)
        removeFromAppStateList(index = pageStartIndexToLoad, isEnd = isEnd)
        val end = System.currentTimeMillis()
        println("updateAppStateList needed ${end - start} ms")
    }

    internal fun addToAppStateList(startIndex: Int, newPageNr: Int) {
        println("Inside addToAppStateList")
        val start = System.currentTimeMillis()
        if (isFiltering) {
            for (i in startIndex until startIndex + pageSize) {
                if (i in firstPageIndex until filteredCount) {
                    // TODO: Index 21 out of bounds for length 21 with id=100 & name=new
                    //  Index 30 out of bounds for length 30 with id=200, name=new
                    appState.filteredTableModelList.set(index = i, element = cache[newPageNr]!![i % pageSize])
                }
            }
        } else {
            // Add new page to list
            for (i in startIndex until startIndex + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.tableModelList.set(index = i, element = cache[newPageNr]!![i % pageSize])
                }
            }
        }
        val end = System.currentTimeMillis()
        println("addToAppStateList needed ${end - start} ms")
    }

    internal fun removeFromAppStateList(index: Int, isEnd: Boolean) {
        println("Inside removeFromAppStateList")
        val start = System.currentTimeMillis()
        val startIndexOldPage = calculateStartIndexOfOldPage(index, isEnd)
        removeOldPageFromList(startIndexOldPage)
        val end = System.currentTimeMillis()
        println("removeFromAppStateList needed ${end - start} ms")
    }

    // Calculates the start index of an "old" page, which has to be removed from the AppStateList
    // TODO: isEnd? index? previousOrNextPage? index +- cacheSize?
    internal fun calculateStartIndexOfOldPage(index: Int, isEnd: Boolean): Int {
        val previousOrNextPage = if (isEnd) index - cacheSize else index + cacheSize
        return previousOrNextPage * pageSize
    }

    internal fun removeOldPageFromList(startIndexOldPage: Int) {
        assert(startIndexOldPage >= firstPageIndex)

        if (isFiltering) {
            for (i in startIndexOldPage until startIndexOldPage + pageSize) {
                if (i in firstPageIndex until filteredCount) {
                    appState.filteredTableModelList.set(index = i, element = null)
                }
            }
        } else {
            for (i in startIndexOldPage until startIndexOldPage + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.tableModelList.set(index = i, element = null)
                }
            }
        }
    }

    /**
     * Select a model from the table.
     * Side effects: Sets the global language on the passed model.
     * @param tableModel model to select
     */
    fun selectModel(tableModel: PlaylistModel) {
        setCurrentLanguage(tableModel = tableModel)
        appState.selectedTableModel = tableModel
    }

    /**
     * Sets the global language saved in app state to a model
     * @param tableModel model to set the global language
     */
    private fun setCurrentLanguage(tableModel: PlaylistModel) {
        val currentLanguage = appState.defaultTableModel.getCurrentLanguage()
        tableModel.setCurrentLanguage(lang = currentLanguage)
    }

    /**
     * Checks with the passed index, if it's time to load new pages.
     * @param firstVisibleItemIndex Index of the first visible item in the UI table
     * @return It's time to load new pages
     * @throws IllegalArgumentException If the passed index is negative or bigger than total count
     */
    fun isTimeToLoadPage(firstVisibleItemIndex: Int): Boolean {
        if (firstVisibleItemIndex < firstPageIndex) throw IllegalArgumentException("param firstVisibleItemIndex should be positive")
        if (firstVisibleItemIndex >= totalCount) throw IllegalArgumentException("param firstVisibleItemIndex should be smaller than total count")

        // Calculate page number for the given index
        val visiblePageNr = getVisiblePageNr(firstVisibleItemIndex)

        // Catch all edge cases, to load only data if necessarily
        return if (visiblePageNr == firstPageIndex) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = 1)
                    || !isPageInCache(pageNr = 2))
        } else if (visiblePageNr > totalPages) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = visiblePageNr - 1)
                    || !isPageInCache(pageNr = visiblePageNr + 1))
        } else if (visiblePageNr > totalPages - 1) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = visiblePageNr - 1))
        } else areNextAndPreviousPagesNotInCache(visiblePageNr = visiblePageNr)
    }

    /**
     * Checks if a given page number and his previous/next pages are in the cache
     * @param visiblePageNr page number to check
     * @return Previous, current or next pages are not in cache
     */
    private fun areNextAndPreviousPagesNotInCache(visiblePageNr: Int): Boolean {
        return !isPageInCache(visiblePageNr)
                || !isPageInCache(pageNr = visiblePageNr - 1)
                || !isPageInCache(pageNr = visiblePageNr + 1)
                || !isPageInCache(pageNr = visiblePageNr + 2)
    }

    internal fun getVisiblePageNr(firstVisibleItemIndex: Int): Int =
        firstVisibleItemIndex / pageSize

    internal fun getFirstIndexOfPage(pageNr: Int): Int =
        pageNr * pageSize

    internal fun isPageInCache(pageNr: Int): Boolean =
        cache.containsKey(pageNr)

    // TODO: Instead add member pagesLoaded: Boolean & Make a LaunchedEffect in LazyTable
    private fun forceRecompose() {
        recomposeStateChanger = !recomposeStateChanger
    }

}