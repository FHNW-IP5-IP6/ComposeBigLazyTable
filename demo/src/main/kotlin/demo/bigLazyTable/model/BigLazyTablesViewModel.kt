package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import demo.bigLazyTable.data.DBService
import model.BaseModel
import model.attributes.BooleanAttribute
import model.attributes.StringAttribute
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.HeaderGroup
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.platform.commons.util.LruCache
import java.sql.Connection
import kotlin.math.ceil

class BigLazyTablesViewModel : BaseModel<ComposeFormsBigLazyTableLabels>(title = ComposeFormsBigLazyTableLabels.TITLE) {

    init {
        setupDatabase()
    }

    private val pageSize = 30

    private val firstPage = 0

    var previousPage: Int? = null
    var currentPage: Int = firstPage
    var nextPage: Int? = currentPage + 1

    // pageNr : all items of that Page
    private var cache: MutableMap<Int, List<Playlist>> = mutableMapOf()
    private var lruCache: LruCache<Int, List<Playlist>> = LruCache(3)

    private val dbService = DBService()

    // rounds to the next int -> 10 / 3 = 4 -> because we would need 4 pages if pageSize=3
    private val lastPage = getNumberOfPages()

    lateinit var playlists: MutableList<Playlist>

    private var currentPlaylist = mutableStateOf(Playlist())
    var currentPlaylistIndex = mutableStateOf(0)

    private fun getNumberOfPages() = ceil(dbService.getTotalCount() / pageSize.toDouble()).toInt()

    fun initialLoad() {
        cache[currentPage] = dbService.getPage(start = currentPage, pageSize = pageSize)
        cache[nextPage!!] = dbService.getPage(start = nextPage!!, pageSize = pageSize)
        playlists = buildPlaylists(cache)
        initCurrentPlaylist()
    }

    fun loadNextPage() {
        goToNextPage()
        previousPage?.let { cache.remove(it) }
        val startIndexOfNextPage = nextPage?.times(pageSize)!! -1 // = nextPage * pageSize
        val lastIndexOfCurrentPage = cache[currentPage]!!.lastIndex
        nextPage?.let { cache[it] = dbService.getPage(it, pageSize) }
        playlists = buildPlaylists(cache)
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

    // Compose Forms
    private val name = StringAttribute(
        model = this,
        label = ComposeFormsBigLazyTableLabels.NAME,
        value = currentPlaylist.value.name
    )

    private val collaborative = BooleanAttribute(
        model = this,
        label = ComposeFormsBigLazyTableLabels.COLLABORATIVE,
        trueText = ComposeFormsBigLazyTableLabels.SELECTIONYES,
        falseText = ComposeFormsBigLazyTableLabels.SELECTIONNO,
        value = currentPlaylist.value.collaborative
    )

    private val modifiedAt = StringAttribute(
        model = this,
        label = ComposeFormsBigLazyTableLabels.MODIFIED_AT,
        required = true,
        value = currentPlaylist.value.modified_at
    )

    val headerGroup = HeaderGroup(
        model = this, title = ComposeFormsBigLazyTableLabels.HEADERGROUP,
        Field(name, FieldSize.BIG),
        Field(collaborative, FieldSize.SMALL),
        Field(modifiedAt, FieldSize.NORMAL)
    )

    // Database
    private fun setupDatabase() {
        Database.connect("jdbc:sqlite:./demo/src/main/resources/spotify_playlist_dataset.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        val dbService = DBService()
        println("Anzahl db Einträge: " + dbService.getTotalCount())
    }
}