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
    val maxPages = ceil(totalCount.toDouble() / pageSize).toInt()

    private val cache: LruCache<Int, List<PlaylistFormModel>> = LruCache(4)

    init {
        val playlists1 = DBService.getPage(startIndex = 0, pageSize = pageSize)
        val playlists2 = DBService.getPage(startIndex = pageSize, pageSize = pageSize)
        val playlistFormModels1 = playlists1.map { PlaylistFormModel(it) }
        val playlistFormModels2 = playlists2.map { PlaylistFormModel(it) }
        addElementsToCache(0, playlistFormModels1)
        addElementsToCache(1, playlistFormModels2)
        addToAppStateList(0, 0)
        addToAppStateList(pageSize, 1)
        selectPlaylist(AppState.lazyModelList[0])
    }

    private fun addElementsToCache(page: Int, elements: List<PlaylistFormModel>) {
        val elems = elements.toMutableList()
        if (AppState.changedFormModels.size > 0) {
            for (i in 0 until pageSize) {
                if (AppState.changedFormModels.find { playlistFormModel -> playlistFormModel.id.getValue() == elems[i].id.getValue() } != null) {
                    elems[i] = AppState.changedFormModels.find { playlistFormModel -> playlistFormModel.id.getValue() == elems[i].id.getValue() }!!
                    AppState.changedFormModels.remove(elems[i])
                }
            }
        }
        cache[page] = elems
    }

    /*
        If firstVisibleItemIndex > lastVisibleIndex --> scrolled down
        If firstVisibleItemIndex < lastVisibleIndex --> scrolled up
    */
    fun get(firstVisibleItemIndex: Int) {
        currentPage.value = firstVisibleItemIndex / pageSize
        val scrolledDown = firstVisibleItemIndex > lastVisibleIndex
        // load 4 pages
        for (i in -1 until 3) {
            val indexToLoad = if (scrolledDown) firstVisibleItemIndex + (i * pageSize) else firstVisibleItemIndex - (i * pageSize)
            loadPage(firstVisibleItemIndex, indexToLoad, scrolledDown)
        }
    }

    private fun loadPage(firstVisibleItemIndex: Int, indexToLoad: Int, scrolledDown: Boolean) {
        // Set firstIndex to new value
        lastVisibleIndex = firstVisibleItemIndex
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
            addElementsToCache(pageToLoad, playlistFormModels)
            // Update AppState List
            addToAppStateList(index = pageStartIndexToLoad, newPage = pageToLoad)
            removeFromAppStateList(index = pageStartIndexToLoad, end = scrolledDown)
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
        playlistFormModel.setCurrentLanguage(AppState.defaultPlaylistFormModel.value.getCurrentLanguage())
        AppState.selectedPlaylist.value = playlistFormModel
    }

    fun getLazyListAttributes(playlistFormModel: PlaylistFormModel) = playlistFormModel.lazyListAttributes
}