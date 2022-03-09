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
 * TODO: Add class documentation
 * @param pagingService TODO: Add description for param
 * @param pageSize TODO: Add description for param
 * @param appState TODO: Add description for param
 *
 * @author Marco Sprenger, Livio Näf
 */
class LazyTableController(
    private val pagingService: IPagingService<*>,
    val pageSize: Int = 40, // TODO: make dynamic
    private val appState: AppState
) {
    private val firstPageNr = 0

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

        scheduler.scheduleTask { initialDataLoading() }
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
                totalPages = PageUtils.getTotalPages(totalCount = filteredCount, pageSize = pageSize)
                println("filtered Count = $filteredCount")
                appState.filteredTableModelList = ArrayList(Collections.nCopies(filteredCount, null))

                initialDataLoading()
            }
        } else {
            totalPages = PageUtils.getTotalPages(totalCount = totalCount, pageSize = pageSize)
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
    var totalPages = PageUtils.getTotalPages(totalCount = totalCount, pageSize = pageSize)

    private val cacheSize = 5
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    var recomposeStateChanger by mutableStateOf(false)

    /**
     * TODO: Add description
     */
    init {
        appState.defaultTableModel.displayedAttributesInTable.forEach { attribute ->
            if (attribute.canBeFiltered) {
                attributeFilter[attribute] = ""
            }
            attributeSort[attribute] = BLTSortOrder.None
        }

        CoroutineScope(Dispatchers.Main).launch {
            initialDataLoading()
            selectFirstModel()
        }
    }

    /**
     * TODO: Add description
     */
    private fun initialDataLoading() {
        for (pageNr in firstPageNr until cacheSize) {
            val startIndexOfPage = pageNr * pageSize
            val models = getPageFromService(startIndex = startIndexOfPage)
            addPageToCache(pageNr = pageNr, pageOfModels = models)
        }

        addNewModelsToAppState()

        forceRecompose()
    }

    /**
     * TODO: Add description
     */
    private fun selectFirstModel() {
        val fullList = if (isFiltering) appState.filteredTableModelList else appState.tableModelList
        fullList.first()?.let { firstModel -> selectModel(firstModel) }
    }


    /**
     * Loads new pages from the service for a given index. The cache is filled with the current visible page and two before and after.
     * If all data is loaded, a recomposition in the table is forced.
     * @param firstVisibleItemIndex Index of the first visible item in the UI table
     */
    fun loadNewPages(firstVisibleItemIndex: Int) {
        // Calculate page number for the given index
        val currentVisiblePageNr = getVisiblePageNr(firstVisibleItemIndex = firstVisibleItemIndex)

        var loopStartIndex = -2
        var loopEndIndex = 2

        if (currentVisiblePageNr >= totalPages - 1) {
            loopStartIndex = -4
            loopEndIndex = 4
        } else if (currentVisiblePageNr >= totalPages - 2) {
            loopStartIndex = -3
            loopEndIndex = 3
        } else if (currentVisiblePageNr <= firstPageNr) {
            loopStartIndex = 0
            loopEndIndex = 0
        } else if (currentVisiblePageNr <= firstPageNr + 1) {
            loopStartIndex = -1
            loopEndIndex = 1
        }

        // Load cache size pages and add them to the cache
        for (i in loopStartIndex until cacheSize - loopEndIndex) {
            val pageNrToLoad = currentVisiblePageNr + i

            // Only load page if page number is a valid number and page is not already in cache
            if (pageNrToLoad in firstPageNr until totalPages && !isPageInCache(pageNr = pageNrToLoad)) {
                loadSinglePage(pageNrToLoad = pageNrToLoad)
            }
        }

        println(cache.keys.toString())

        // Add new data to the app state and remove data no longer needed
        updateAppStateList(currentVisiblePageNr = currentVisiblePageNr)

        // Force a recomposition in the table
        forceRecompose()
    }

    /**
     * Loads a single page of models.
     * - The page is requested from the paging service.
     * - The response data is added to the cache.
     * @param pageNrToLoad Number of the page to load
     */
    internal fun loadSinglePage(pageNrToLoad: Int) {
        // Calculate first index of page to load
        val firstIndexOfPageToLoad = getFirstIndexOfPage(pageNr = pageNrToLoad)

        // Get page of models from paging service
        val tableModels = getPageFromService(startIndex = firstIndexOfPageToLoad)

        // Add page of models to cache
        addPageToCache(pageNr = pageNrToLoad, pageOfModels = tableModels)
    }

    /**
     * Requests one page from the paging service, starting with the given index.
     * - Does log filters and sort settings set before request.
     * - Does log the time in milliseconds, the request took.
     * @param startIndex Index to start requesting [pageSize] page
     */
    internal fun getPageFromService(startIndex: Int): List<PlaylistModel> {
        Log.info("Requesting data from paging service with startIndex $startIndex")
        Log.info("Filter set for request: $filters")
        Log.info("Sorting set for request: $sort")

        val start = System.currentTimeMillis()

        val page = pagingService.getPage(
            startIndex = startIndex,
            pageSize = pageSize,
            filters = filters,
            sort = sort
        )

        val end = System.currentTimeMillis()
        Log.info("The request took ${end - start} milliseconds.")

        return page.toPlaylistModels()
    }

    // Helper function to map a list of Any to a list of playlistModels
    private fun List<Any?>.toPlaylistModels(): List<PlaylistModel> {
        return map { PlaylistModel(it as Playlist, appState) }
    }

    /**
     * TODO: Add description
     * @param pageNr Number of the given page
     * @param pageOfModels List of models
     */
    internal fun addPageToCache(pageNr: Int, pageOfModels: List<PlaylistModel>) {
        // Create a mutable list from the given models
        var modelListForCache = pageOfModels.toMutableList()

        // Check if app state containing changed models
        if (appState.changedTableModels.size > 0) {
            modelListForCache = mergeModels(pageOfModels = modelListForCache)
        }

        // Add list of models to cache at given page number
        cache[pageNr] = modelListForCache
    }

    /**
     * TODO: Add description
     * @param pageOfModels TODO: ADD param description
     * @return TODO: Add return
     */
    internal fun mergeModels(pageOfModels: MutableList<PlaylistModel>): MutableList<PlaylistModel> {
        for (i in firstPageNr until pageSize) {
            // Check if a changed model is existing for the current iteration model
            val tableModel = appState.changedTableModels.find { tableModel -> tableModel.id.getValue() == pageOfModels[i].id.getValue() }

            // If tableModel is not null, the same model with changes exists
            if (tableModel != null) {
                // Replace the current model with the model containing changes
                pageOfModels[i] = tableModel
                // Remove the model with changes from the changedModels list
                appState.changedTableModels.remove(tableModel)
            }
        }

        return pageOfModels
    }


    /**
     * TODO: Add description
     * @param currentVisiblePageNr Page number of current visible page in table
     */
    internal fun updateAppStateList(currentVisiblePageNr: Int) {
        removeOldModelsFromAppState(pageNr = currentVisiblePageNr)
        addNewModelsToAppState()
    }

    /**
     * TODO: Add description
     * @param pageNr Page number for start index to remove calculation
     */
    internal fun removeOldModelsFromAppState(pageNr: Int) {
        val startIndexToRemove = calculateStartIndexToRemove(pageNr = pageNr)
        removeOldPagesFromList(startIndexToRemove = startIndexToRemove)
    }

    /**
     * TODO: Add description
     * @param pageNr Page number for start index to remove calculation
     */
    internal fun calculateStartIndexToRemove(pageNr: Int): Int {
        var calcPageNr = pageNr - 2

        if (calcPageNr < 0) {
            calcPageNr = 0
        } else if (calcPageNr >= totalPages) {
            calcPageNr = totalPages - 1
        }

        return getFirstIndexOfPage(calcPageNr)
    }

    /**
     * TODO: Add description
     * @param startIndexToRemove Index in list to start removing models
     */
    internal fun removeOldPagesFromList(startIndexToRemove: Int) {
        if (isFiltering) {
            for (i in startIndexToRemove until startIndexToRemove + (cacheSize * pageSize)) {
                if (i in firstPageNr until filteredCount) {
                    appState.filteredTableModelList.set(index = i, element = null)
                }
            }
        } else {
            for (i in startIndexToRemove until startIndexToRemove + (cacheSize * pageSize)) {
                if (i in firstPageNr until totalCount) {
                    appState.tableModelList.set(index = i, element = null)
                }
            }
        }
    }

    /**
     * TODO: Add description
     */
    internal fun addNewModelsToAppState() {
        val smallestPageNrInCache = cache.keys.first()

        for (key in smallestPageNrInCache until smallestPageNrInCache + cacheSize) {
            if (key < totalPages) {
                val startIndexOfKey = getFirstIndexOfPage(key)
                for (i in startIndexOfKey until startIndexOfKey + pageSize) {
                    if (cache[key]?.get(i % pageSize) != null) {
                        if (isFiltering) {
                            if (i in firstPageNr until filteredCount) {
                                appState.filteredTableModelList.set(index = i, element = cache[key]!![i % pageSize])
                            }
                        } else {
                            if (i in firstPageNr until totalCount) {
                                appState.tableModelList.set(index = i, element = cache[key]!![i % pageSize])
                            }
                        }
                    }
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
        if (firstVisibleItemIndex < firstPageNr) throw IllegalArgumentException("param firstVisibleItemIndex should be positive")
        if (firstVisibleItemIndex >= totalCount) throw IllegalArgumentException("param firstVisibleItemIndex should be smaller than total count")

        // Calculate page number for the given index
        val visiblePageNr = getVisiblePageNr(firstVisibleItemIndex)

        // Catch all edge cases, to load only data if necessarily
        return if (visiblePageNr <= firstPageNr) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = 1)
                    || !isPageInCache(pageNr = 2)
                    || !isPageInCache(pageNr = 3)
                    || !isPageInCache(pageNr = 4))
        } else if (visiblePageNr <= firstPageNr + 1) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = visiblePageNr - 1)
                    || !isPageInCache(pageNr = 1)
                    || !isPageInCache(pageNr = 2)
                    || !isPageInCache(pageNr = 3))
        } else if (visiblePageNr >= totalPages - 1) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = visiblePageNr - 1)
                    || !isPageInCache(pageNr = visiblePageNr - 2)
                    || !isPageInCache(pageNr = visiblePageNr - 3)
                    || !isPageInCache(pageNr = visiblePageNr - 4))
        } else if (visiblePageNr >= totalPages - 2) {
            (!isPageInCache(pageNr = visiblePageNr)
                    || !isPageInCache(pageNr = visiblePageNr + 1)
                    || !isPageInCache(pageNr = visiblePageNr - 1)
                    || !isPageInCache(pageNr = visiblePageNr - 2)
                    || !isPageInCache(pageNr = visiblePageNr - 3))
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
                || !isPageInCache(pageNr = visiblePageNr - 2)
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