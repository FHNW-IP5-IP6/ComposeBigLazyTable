package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.DBService
import org.junit.platform.commons.util.LruCache
import mu.KotlinLogging

private val Log = KotlinLogging.logger {}

/**
 * @author Marco Sprenger, Livio Näf
 */
class ViewModelLazyList(private val dbService: DBService) {

    private val totalCount = dbService.getTotalCount()
    var firstIndex = 0
    private val pageSize = 30

    private val cache: LruCache<Int, List<PlaylistFormModel>> = LruCache(4)

    fun init() {
        val playlists1 = dbService.getPage(startIndex = 0, pageSize = pageSize)
        val playlists2 = dbService.getPage(startIndex = pageSize, pageSize = pageSize)
        val playlistFormModels1 = playlists1.map { PlaylistFormModel(it) }
        val playlistFormModels2 = playlists2.map { PlaylistFormModel(it) }
        cache[0] = playlistFormModels1
        cache[1] = playlistFormModels2
        addToAppStateList(0, 0)
        addToAppStateList(pageSize, 1)
    }

    fun get(index: Int) {
        Log.info { "Index passed by UI List: $index" }
        /*
        If index > firstIndex --> scrolled down
        If index < firstIndex --> scrolled up
         */
        if (index != firstIndex) {
            // Calculate next index to load
            val indexToLoad = if (index > firstIndex) { index + (2 * pageSize) } else { index - (2 * pageSize) }
            // Boolean if page is added at the end of the list
            val end = index > firstIndex
            // Set firstIndex to new value
            firstIndex = index
            // Calculate page of indexToLoad
            val pageToLoad = indexToLoad / pageSize
            // check if pageToLoad is not loaded in cache
            if (pageToLoad >= 0 && !cache.contains(pageToLoad)) {
                // Calculate pageStartIndexToLoad
                val pageStartIndexToLoad = indexToLoad - (indexToLoad%30)
                // Load page from service
                val playlists = dbService.getPage(startIndex = pageStartIndexToLoad, pageSize = pageSize)
                val playlistFormModels = playlists.map { PlaylistFormModel(it) }
                // Save new page to cache
                cache[pageToLoad] = playlistFormModels
                // Update AppState List
                addToAppStateList(index = pageStartIndexToLoad, newPage = pageToLoad)
                removeFromAppStateList(index = pageStartIndexToLoad, end = end)
            }
        }
    }

    private fun addToAppStateList(index: Int, newPage: Int) {
        // Add new page to list
        for (i in index until index+pageSize) {
            if (i in 0 until totalCount) {
                AppState.uiList.set(index = i, element = cache[newPage]!![i%pageSize])
            }
        }
    }

    private fun removeFromAppStateList(index: Int, end: Boolean) {
        // Remove old page from list
        val startIndexOldPage = if (end) { index - 4*pageSize } else { index + 4*pageSize }
        for (i in startIndexOldPage until startIndexOldPage+pageSize) {
            if (i in 0 until totalCount) {
                AppState.uiList.set(index = i, element = null)
            }
        }
    }

    fun selectPlaylist(playlistFormModel: PlaylistFormModel) {
        AppState.selectedPlaylist.value = playlistFormModel
    }

}