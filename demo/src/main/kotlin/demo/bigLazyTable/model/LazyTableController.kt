package demo.bigLazyTable.model // TODO: move to package controler

import androidx.compose.runtime.*
import bigLazyTable.paging.*
import composeForms.model.BaseModel
import composeForms.model.attributes.*
import demo.bigLazyTable.utils.PageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.junit.platform.commons.util.LruCache // TODO: Other LruCache (android.util.LruCache<K, V>)
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

private val Log = KotlinLogging.logger {} // TODO: Why not below logger
val log2 = Logger.getLogger("classname")

/**
 * TODO: Add class documentation
 * @param pagingService TODO: Add description for param
 * @param pageSize TODO: Add description for param
 * @param appState TODO: Add description for param
 *
 * @author Marco Sprenger, Livio Näf
 */

// TODO: Add log
class LRUCache<key, value> (val maxSize: Int) : LinkedHashMap<key, value>(maxSize, 0.75f, true){
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<key, value>?): Boolean {
        return size > maxSize
    }
}

class LazyTableController<T: BaseModel<*>>(
    private val pagingService: IPagingService<*>,
    val pageSize: Int = 40, // TODO: make dynamic
//    private val appState: AppState // TODO: Remove as param & create in class
) {
    // Variables for different calculations
    private val totalCount by lazy { pagingService.getTotalCount() }
    private var filteredCount by Delegates.notNull<Int>()
    var totalPages = PageUtils.getTotalPages(totalCount = totalCount, pageSize = pageSize)
    private val firstPageNr = 0


    val appState = AppState<PlaylistModel>(pagingService, Playlist().toPlaylistModel(null))

    // Sorting variables
    var isSorting by mutableStateOf(false)
    var lastSortedAttribute: Attribute<*, *, *>? = null
    var attributeSort = mutableStateMapOf<Attribute<*, *, *>, BLTSortOrder>()
    var sort: Sort? by mutableStateOf(null)

    // Filtering variables
    var filters = listOf<Filter>()
    var displayedFilterStrings: MutableMap<Attribute<*, *, *>, String> = mutableStateMapOf()
    var attributeFilterNew: MutableMap<Attribute<*, *, *>, Filter?> = mutableStateMapOf()
    var attributeCaseSensitive = mutableStateMapOf<Attribute<*, *, *>, Boolean>()
    var isFiltering by mutableStateOf(false)
        private set

    // Schedulers
    val scheduler = Scheduler()
    private val filterScheduler = Scheduler(100)

    // Cache variables
    private val cacheSize = 5
    private val cache: LruCache<Int, List<PlaylistModel>> = LruCache(cacheSize)

    // Needed to force a recomposition
    var recomposeStateChanger by mutableStateOf(false)

    /**
     * Initializer for first start up.
     * - Sets up filters and sorting
     * - Loads the initial data
     * - Selects the first model in the table for the forms
     */
    init {
        appState.defaultTableModel.displayedAttributesInTable.forEach { attribute ->
            if (attribute.canBeFiltered) {
                displayedFilterStrings[attribute] = ""
            }
            attributeSort[attribute] = BLTSortOrder.None
            attributeCaseSensitive[attribute] = false
        }

        CoroutineScope(Dispatchers.Main).launch {
            initialDataLoading()
            selectFirstModel()
        }
    }

    /**
     * Load the initial data for first start. Loads the first cache size pages.
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
     * Select to first model from the table list and set in app state.
     */
    private fun selectFirstModel() {
        val fullList = if (isFiltering) appState.filteredTableModelList else appState.tableModelList
        fullList.first()?.let { firstModel -> selectModel(firstModel as T) }
    }

    /**
     * Loads new pages from the service for a given index. The cache is filled with the current visible page and two before and after.
     * If all data is loaded, a recomposition in the table is forced.
     * @param firstVisibleItemIndex Index of the first visible item in the UI table
     */
    fun loadNewPages(firstVisibleItemIndex: Int) {
        // Calculate page number for the given index
        val currentVisiblePageNr = getVisiblePageNr(firstVisibleItemIndex = firstVisibleItemIndex)

        // Calculate start and end index for loop
        val indexes = calculateLoopIndexes(currentVisiblePageNr)

        // Load cache size pages and add them to the cache
        for (i in indexes.first until cacheSize - indexes.second) {
            val pageNrToLoad = currentVisiblePageNr + i

            // Only load page if page number is a valid number and page is not already in cache
            if (pageNrToLoad in firstPageNr until totalPages && !isPageInCache(pageNr = pageNrToLoad)) {
                loadSinglePage(pageNrToLoad = pageNrToLoad)
            }
        }

        // Add new data to the app state and remove data no longer needed
        updateAppStateList(currentVisiblePageNr = currentVisiblePageNr)

        // Force a recomposition in the table
        forceRecompose()
    }

    /**
     * Calculates the start and end index for the loop to load data
     * @param pageNr Current visible page number
     * @return Pair of start and end index for loop
     */
    internal fun calculateLoopIndexes(pageNr: Int): Pair<Int, Int> {
        // Default loop settings to get two previous, one current and two next pages -> cache size pages
        var loopStartIndex = -2
        var loopEndIndex = 2

        if (pageNr >= totalPages - 1) { // If current visible page is the last one -> get the current and the four previous pages
            loopStartIndex = -4
            loopEndIndex = 4
        } else if (pageNr >= totalPages - 2) { // If current visible page is the second last one -> get the current, next and the three previous pages
            loopStartIndex = -3
            loopEndIndex = 3
        } else if (pageNr <= firstPageNr) { // If current visible page is the first one -> get the current and the four next pages
            loopStartIndex = 0
            loopEndIndex = 0
        } else if (pageNr <= firstPageNr + 1) { // If current visible page is the second one -> get the current, previous and the three next pages
            loopStartIndex = -1
            loopEndIndex = 1
        }

        return Pair(first = loopStartIndex, second = loopEndIndex)
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
     * Adds a give list of models to the cache. If the app stat containing models with changes, a merge mechanism is used to merge these models together.
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
     * Merges an incoming list of models with the list in app state containing models with changes.
     * @param pageOfModels List of models to merge
     * @return List of models, where all models with changes are included
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
     * Removes unused models from app state and adds all models currently in cache
     * @param currentVisiblePageNr Page number of current visible page in table
     */
    internal fun updateAppStateList(currentVisiblePageNr: Int) {
        removeOldModelsFromAppState(pageNr = currentVisiblePageNr)
        addNewModelsToAppState()
    }

    /**
     * Removes unused models from app state
     * @param pageNr Page number for start index to remove calculation
     */
    internal fun removeOldModelsFromAppState(pageNr: Int) {
        // Calculate the start index for removing old models
        val startIndexToRemove = calculateStartIndexToRemove(pageNr = pageNr)
        removeOldPagesFromList(startIndexToRemove = startIndexToRemove)
    }

    /**
     * Calculates the index from where to start removing old models.
     * @param pageNr Page number for start index to remove calculation
     */
    internal fun calculateStartIndexToRemove(pageNr: Int): Int {
        // Subtract two from the current visible page, because there are always two previous pages loaded
        var calcPageNr = pageNr - 2

        // Handle edge cases if the current visible page is near to zero or total pages
        if (calcPageNr < 0) {
            calcPageNr = 0
        } else if (calcPageNr >= totalPages) {
            calcPageNr = totalPages - 1
        }

        return getFirstIndexOfPage(calcPageNr)
    }

    /**
     * Removes old models from the app state. Starts with the passed index and removes the next cache size pages. Removing means to set a list entry to null.
     * @param startIndexToRemove Index in list to start removing models
     */
    internal fun removeOldPagesFromList(startIndexToRemove: Int) {
        // Loop from given start index until cache size pages
        for (i in startIndexToRemove until startIndexToRemove + (cacheSize * pageSize)) {

            // Check if filtering is active, to differentiate between app state filtered list or full list
            if (isFiltering) {
                if (i in firstPageNr until filteredCount) {
                    appState.filteredTableModelList.set(index = i, element = null)
                }
            } else {
                if (i in firstPageNr until totalCount) {
                    appState.tableModelList.set(index = i, element = null)
                }
            }
        }
    }

    /**
     * Adds all models currently in cache to the app state list.
     */
    internal fun addNewModelsToAppState() {
        // Find the smallest page number in cache
        val smallestPageNrInCache = cache.keys.first()

        // Loop over all keys currently in the cache
        for (key in smallestPageNrInCache until smallestPageNrInCache + cacheSize) {
            // Check if the key is a valid page number
            if (key < totalPages) {
                // Calculate the start index of the page with the current key
                val startIndexOfKey = getFirstIndexOfPage(key)
                // Loop over the whole page
                for (i in startIndexOfKey until startIndexOfKey + pageSize) {
                    // Check if a specific element existing for the given key in the cache
                    if (cache[key]?.get(i % pageSize) != null) {
                        // Check if filtering is active, to differentiate between app state filtered list or full list
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

//        fun onSortChanged(attribute: Attribute<*, *, *>, newSortOrder: BLTSortOrder) {
//            resetPreviousSortedAttribute(newAttribute = attribute)
//
//            lastSortedAttribute = attribute
//            attributeSort[attribute] = newSortOrder
//            sort = newSortOrder.sortAttribute(attribute)
//
//            scheduler.scheduleTask { loadFirstPagesToFillCacheAndAddToAppStateList() }
//        }
//
//        private fun resetPreviousSortedAttribute(newAttribute: Attribute<*, *, *>) {
//            if (lastSortedAttribute != null && lastSortedAttribute != newAttribute) {
//                attributeSort[lastSortedAttribute!!] = BLTSortOrder.None
//            }
//        }

    /**
     * TODO: Add description
     * @param attribute TODO: Add description
     * @param newSortOrder TODO: Add description
     */
    fun onSortChanged(attribute: Attribute<*, *, *>, newSortOrder: BLTSortOrder) {
        resetPreviousSortedAttribute(newAttribute = attribute)

        lastSortedAttribute = attribute
        attributeSort[attribute] = newSortOrder
        isSorting = newSortOrder.isSorting
        sort = newSortOrder.sortAttribute(attribute)

        scheduler.scheduleTask { initialDataLoading() }
    }

    /**
     * TODO: Add description
     * @param newAttribute TODO: Add description
     */
    private fun resetPreviousSortedAttribute(newAttribute: Attribute<*,*,*>) {
        if (lastSortedAttribute != null && lastSortedAttribute != newAttribute) {
            attributeSort[lastSortedAttribute!!] = BLTSortOrder.None
        }
    }

    // TODO: Spinner?
    fun onFilterChanged() {
        println("Inside onFiltersChanged")
        val start = System.currentTimeMillis()

        filters = attributeFilterNew.values.filterNotNull()
        println("Filters in onNumberFilterChanged: $filters")

        isFiltering = filters.isNotEmpty()
        if (isFiltering) {
            filterScheduler.scheduleTask {
                println("Before getFilteredCountNew")
                val start1 = System.currentTimeMillis()
                // getFilteredCountNew needed 617 ms
                filteredCount = pagingService.getFilteredCount(filters = filters)
                println("filtered count = $filteredCount")
                val end1 = System.currentTimeMillis()
                println("getFilteredCountNew needed ${end1 - start1} ms")

                println("filtered Count = $filteredCount")
                appState.filteredTableModelList = ArrayList(Collections.nCopies(filteredCount, null))

                initialDataLoading()
            }
        } else {
            // Make sure that after last filter removed sort order stays
            scheduler.scheduleTask { initialDataLoading() }
        }

        val end = System.currentTimeMillis()
        println("onFiltersChanged needed ${end - start} ms")
    }

//    /**
//     * TODO: Add description
//     * @param attribute TODO: Add description
//     * @param newFilter TODO: Add description
//     */
//    fun onFiltersChanged(attribute: Attribute<*, *, *>, newFilter: String) {
//        attributeFilter[attribute] = newFilter
//
//        if (newFilter == "") filteredAttributes.remove(attribute)
//        else filteredAttributes.add(attribute)
//
//        filters = filteredAttributes.map { a ->
//            Filter(
//                filter = attributeFilter[a] ?: "",
//                dbField = a.databaseField as Column<String>,
//                caseSensitive = false
//            )
//        }
//
//        isFiltering = filters.isNotEmpty()
//        if (isFiltering) {
//            filterScheduler.scheduleTask {
//                filteredCount = pagingService.getFilteredCount(filters = filters)
//                totalPages = PageUtils.getTotalPages(totalCount = filteredCount, pageSize = pageSize)
//                appState.filteredTableModelList = ArrayList(Collections.nCopies(filteredCount, null))
//
//                initialDataLoading()
//            }
//        } else {
//            totalPages = PageUtils.getTotalPages(totalCount = totalCount, pageSize = pageSize)
//        }
//    }

    /**
     * Select a model from the table.
     * Side effects: Sets the global language on the passed model.
     * @param tableModel model to select
     */
    fun selectModel(tableModel: T) {
        setCurrentLanguage(tableModel = tableModel)
        appState.selectedTableModel = tableModel as PlaylistModel
    }

    /**
     * Sets the global language saved in app state to a model
     * @param tableModel model to set the global language
     */
    private fun setCurrentLanguage(tableModel: T) {
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