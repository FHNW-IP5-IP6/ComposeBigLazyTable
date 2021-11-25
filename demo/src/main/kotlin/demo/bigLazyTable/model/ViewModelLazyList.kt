package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.DBService
import org.junit.platform.commons.util.LruCache
import mu.KotlinLogging

private val Log = KotlinLogging.logger {}

/**
 * @author Marco Sprenger, Livio Näf
 */
class ViewModelLazyList(private val dbService: DBService) {

    val totalCount = dbService.getTotalCount()
    var firstIndex = 0
    private val pageSize = 30

    var nextPage = 0

    private val cache: LruCache<Int, List<PlaylistFormModel>> = LruCache(3)

    fun get(index: Int) {
        Log.info { "Info test" }
        Log.error { "$index" }
        val pageNr = index*nextPage / pageSize
        nextPage = pageNr + 1

        if (!cache.contains(pageNr)) {
            Log.error { "!cache.contains(pageNr=$pageNr)" }
            val startIndex = pageNr * pageSize
            Log.error { "startIndex=$startIndex" }
            val playlists = dbService.getPage(startIndex = startIndex, pageSize = pageSize)
            val playlistFormModels = playlists.map { PlaylistFormModel(it) }
            cache[pageNr] = playlistFormModels
            Log.error { cache.keys.toString() }
            AppState.uiList = cache[pageNr]!!
        }
    }


/*

    private val firstPage = 0

    var previousPage: Int? = null
    var currentPage: Int = firstPage
    var nextPage: Int? = currentPage + 1

    private var cacheOld: MutableMap<Int, List<Playlist>> = mutableMapOf()

    // rounds to the next int -> 10 / 3 = 4 -> because we would need 4 pages if pageSize=3
    private val lastPage = getNumberOfPages()

    lateinit var playlists: MutableList<Playlist>

    private var currentPlaylist = mutableStateOf(Playlist())
    var currentPlaylistIndex = mutableStateOf(0)

    private fun getNumberOfPages() = ceil(dbService.getTotalCount() / pageSize.toDouble()).toInt()

    fun initialLoad() {
        cacheOld[currentPage] = dbService.getPage(startIndex = currentPage, pageSize = pageSize)
        cacheOld[nextPage!!] = dbService.getPage(startIndex = nextPage!!*pageSize, pageSize = pageSize)
        playlists = buildPlaylists(cacheOld)
        initCurrentPlaylist()
    }

    fun loadNextPage() {
        goToNextPage()
        previousPage?.let { cacheOld.remove(it) }
        val startIndexOfNextPage = nextPage?.times(pageSize)!! // = nextPage * pageSize
        //val lastIndexOfCurrentPage = cache[currentPage]!!.lastIndex
        nextPage?.let { cacheOld[it] = dbService.getPage(startIndexOfNextPage, pageSize) }
        playlists = buildPlaylists(cacheOld)
    }

    private fun buildPlaylists(map: MutableMap<Int, List<Playlist>>): MutableList<Playlist> {
        val result: MutableList<Playlist> = mutableListOf()
        map.values.forEach { list ->
            result.addAll(list)
        }
        return result
    }

    private fun goToNextPage() {
        previousPage = currentPage
        currentPage = nextPage ?: lastPage
        nextPage = if (nextPage == lastPage) null else nextPage!!.plus(1)
    }

    private fun goToPreviousPage() {
        nextPage = currentPage
        currentPage = previousPage ?: firstPage
        previousPage = if (previousPage == firstPage) null else previousPage!!.minus(1)
    }

    fun setCurrentPlaylist() {
        currentPlaylist.value = playlists[currentPlaylistIndex.value]
    }

    private fun initCurrentPlaylist() {
        currentPlaylist.value = playlists.first()
    }
*/

}