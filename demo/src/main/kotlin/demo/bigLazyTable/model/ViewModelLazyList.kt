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
    var firstIndex = 0
    private const val pageSize = 40
    var currentPage = mutableStateOf(0)
    val maxPages = ceil(totalCount.toDouble() / pageSize).toInt()

    val cache: LruCache<Int, List<PlaylistFormModel>> = LruCache(4)

    fun init() {
        val playlists1 = DBService.getPage(startIndex = 0, pageSize = pageSize)
        val playlists2 = DBService.getPage(startIndex = pageSize, pageSize = pageSize)
        val playlistFormModels1 = playlists1.map { PlaylistFormModel(it) }
        val playlistFormModels2 = playlists2.map { PlaylistFormModel(it) }
        cache[0] = playlistFormModels1
        cache[1] = playlistFormModels2
        addToAppStateList(0, 0)
        addToAppStateList(pageSize, 1)
        selectPlaylist(AppState.lazyModelList[0])
    }

    fun get(index: Int) {
        /*
        If index > firstIndex --> scrolled down
        If index < firstIndex --> scrolled up
         */
        currentPage.value = index / pageSize
        val scrolledDown = index > firstIndex
        // load 4 pages
        for (i in -1 until 3) {
            val indexToLoad = if (scrolledDown) index + (i * pageSize) else index - (i * pageSize)
            loadPage(index, indexToLoad)
        }
    }

    private fun loadPage(index: Int, indexToLoad: Int) {
        // Boolean if page is added at the end of the list
        val end = index > firstIndex
        // Set firstIndex to new value
        firstIndex = index
        // Calculate page of indexToLoad
        val pageToLoad = indexToLoad / pageSize
        // check if pageToLoad is not loaded in cache
        if (pageToLoad >= 0 && !cache.contains(pageToLoad)) {
            // Calculate pageStartIndexToLoad
            val pageStartIndexToLoad = indexToLoad - (indexToLoad % pageSize)
            // Load page from service
            val playlists = DBService.getPage(startIndex = pageStartIndexToLoad, pageSize = pageSize)
            val playlistFormModels = playlists.map { PlaylistFormModel(it) }
            // Save new page to cache
            cache[pageToLoad] = playlistFormModels
            // Update AppState List
            addToAppStateList(index = pageStartIndexToLoad, newPage = pageToLoad)
            removeFromAppStateList(index = pageStartIndexToLoad, end = end)
        }
    }

    private fun addToAppStateList(index: Int, newPage: Int) {
        // Add new page to list
        for (i in index until index + pageSize) {
            if (i in 0 until totalCount) {
                AppState.lazyModelList.set(index = i, element = cache[newPage]!![i % pageSize])
            }
        }
    }

    private fun removeFromAppStateList(index: Int, end: Boolean) {
        // Remove old page from list
        val startIndexOldPage = if (end) {
            index - 4 * pageSize
        } else {
            index + 4 * pageSize
        }
        for (i in startIndexOldPage until startIndexOldPage + pageSize) {
            if (i in 0 until totalCount) {
                AppState.lazyModelList.set(index = i, element = AppState.defaultPlaylistFormModel.value)
            }
        }
    }

    fun selectPlaylist(playlistFormModel: PlaylistFormModel) {
        AppState.selectedPlaylist.value = playlistFormModel
    }

    fun getLazyListAttributes(playlistFormModel: PlaylistFormModel) = playlistFormModel.lazyListAttributes
}