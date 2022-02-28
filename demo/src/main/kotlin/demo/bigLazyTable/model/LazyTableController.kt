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
    val pageSize: Int = 40, // TODO: If enough time - make dynamic
    private val appState: AppState
) {
    private val firstPageNr = 0 // TODO: Use Nr everywhere!
    private val firstPageIndex = 0

    var lastSortedAttribute: Attribute<*, *, *>? = null
    var attributeSort = mutableMapOf<Attribute<*, *, *>, BLTSortOrder>()
    var sort: Sort? by mutableStateOf(null)
//    private var isSorting by mutableStateOf(false)

    fun onSortChanged(newSortAttribute: Attribute<*, *, *>, newSortOrder: BLTSortOrder) {
        resetPreviousSortedAttribute(newSortAttribute = newSortAttribute)

        lastSortedAttribute = newSortAttribute
//        isSorting = newSort.isSorting
        sort = newSortOrder.sortAttribute(newSortAttribute)
        attributeSort[newSortAttribute] = newSortOrder

        loadFirstPagesToFillCacheAndAddToAppStateList()
    }

    private fun resetPreviousSortedAttribute(newSortAttribute: Attribute<*,*,*>) {
        if (lastSortedAttribute != null && lastSortedAttribute != newSortAttribute) {
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
                filteredCount = pagingService.getFilteredCountNew(filters = filters)
                val end1 = System.currentTimeMillis()
                println("getFilteredCountNew needed ${end1 - start1} ms")

                println("filtered Count = $filteredCount")
                appState.filteredList = ArrayList(Collections.nCopies(filteredCount, null))

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
        appState.defaultPlaylistModel.displayedAttributesInTable.forEach { attribute ->
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
        val fullList = if (isFiltering) appState.filteredList else appState.lazyModelList
        println("loadFirstPagesAndFillCacheAndSelectFirstPlaylist: appState.list size = ${fullList.size}")
        fullList.first()?.let { firstPlaylist -> selectPlaylistModel(firstPlaylist) }
    }

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
        // getPageNew needed 745 ms (normal max with filters)
        // getPageNew needed 24837 ms (max with sort)
        val page = pagingService.getPageNew(
            startIndex = startIndexOfPage,
            pageSize = pageSize,
            filters = filters,
            sort = sort
        )
        val end1 = System.currentTimeMillis()
        println("getPageNew needed ${end1 - start1} ms")

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
                    appState.filteredList.set(index = i, element = cache[newPageNr]!![i % pageSize])
                }
            }
        } else {
            // Add new page to list
            for (i in startIndex until startIndex + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.lazyModelList.set(index = i, element = cache[newPageNr]!![i % pageSize])
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
        println("Inside removeOldPageFromList")
        val start = System.currentTimeMillis()
        assert(startIndexOldPage >= firstPageIndex)

        if (isFiltering) {
            for (i in startIndexOldPage until startIndexOldPage + pageSize) {
                if (i in firstPageIndex until filteredCount) {
                    appState.filteredList.set(index = i, element = null)
                }
            }
        } else {
            for (i in startIndexOldPage until startIndexOldPage + pageSize) {
                if (i in firstPageIndex until totalCount) {
                    appState.lazyModelList.set(index = i, element = null)
                }
            }
        }

        val end = System.currentTimeMillis()
        println("removeOldPageFromList needed ${end - start} ms")
    }

    fun selectPlaylistModel(playlistModel: PlaylistModel) {
        println("Inside selectPlaylistModel")
        val start = System.currentTimeMillis()
        setCurrentLanguage(playlistModel = playlistModel)
        appState.selectedPlaylistModel = playlistModel
        val end = System.currentTimeMillis()
        println("selectPlaylistModel needed ${end - start} ms")
    }

    private fun setCurrentLanguage(playlistModel: PlaylistModel) {
        val currentLanguage = appState.defaultPlaylistModel.getCurrentLanguage()
        playlistModel.setCurrentLanguage(currentLanguage)
    }

    // Checks with the passed firstVisibleItemIndex from the UI, if it's time to load new pages
    fun isTimeToLoadPage(firstVisibleItemIndex: Int): Boolean {
        println("Inside isTimeToLoadPage")
        val start = System.currentTimeMillis()
        if (firstVisibleItemIndex < firstPageIndex) throw IllegalArgumentException("firstVisibleItemIndex should be positive")
        if (firstVisibleItemIndex >= totalCount) throw IllegalArgumentException("firstVisibleItemIndex should be smaller than total count")

        val visiblePageNr = getVisiblePageNr(firstVisibleItemIndex)

        // Catch all edge cases, to load only data if necessarily
        var rv = false
        /*return */if (visiblePageNr == firstPageIndex) {
            rv = (!isPageInCache(visiblePageNr)
                    || !isPageInCache(1)
                    || !isPageInCache(2))
        } else if (visiblePageNr > totalPages) {
            rv = (!isPageInCache(visiblePageNr)
                    || !isPageInCache(visiblePageNr - 1)
                    || !isPageInCache(visiblePageNr + 1))
        } else if (visiblePageNr > totalPages - 1) {
            rv = (!isPageInCache(visiblePageNr)
                    || !isPageInCache(visiblePageNr - 1))
        } else rv = areNextAndPreviousPagesNotInCache(visiblePageNr)

        val end = System.currentTimeMillis()
        println("selectPlaylistModel needed ${end - start} ms")

        return rv
    }

    private fun areNextAndPreviousPagesNotInCache(visiblePageNr: Int): Boolean {
        return !isPageInCache(visiblePageNr)
                || !isPageInCache(visiblePageNr - 1)
                || !isPageInCache(visiblePageNr + 1)
                || !isPageInCache(visiblePageNr + 2)
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