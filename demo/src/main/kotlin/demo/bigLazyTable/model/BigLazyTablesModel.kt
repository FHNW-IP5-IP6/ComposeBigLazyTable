package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import bigLazyTable.paging.PAGE_SIZE
import demo.bigLazyTable.data.DBService
import model.BaseModel
import model.attributes.BooleanAttribute
import model.attributes.StringAttribute
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.HeaderGroup
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import kotlin.math.ceil

// TODO: Is this the ViewModel?
class BigLazyTablesModel : BaseModel<ComposeFormsBigLazyTableLabels>(title = ComposeFormsBigLazyTableLabels.TITLE) {

    val firstPage = 0

    var previousPage: Int? = null
    var currentPage: Int = firstPage
    var nextPage: Int? = currentPage + 1

    // pageNr : all items of that Page
    private var cache: MutableMap<Int, List<Playlist>> = mutableMapOf()

    init {
        setupDatabase()
    }

    var dataChooserStatus = mutableStateOf(false)

    private val dbService = DBService()

    // rounds to the next int -> 10 / 3 = 4 -> because we would need 4 pages if pageSize=3
    private val lastPage = getNumberOfPages()

    lateinit var playlists: List<Playlist>

    private var currentPlaylist = mutableStateOf(Playlist())
    var currentPlaylistIndex = mutableStateOf(0)

    private fun getNumberOfPages() = ceil(dbService.getAll().size / PAGE_SIZE.toDouble()).toInt()

    fun loadTestData() {
        playlists = dbService.getPage(0)
        initPlaylist()
    }

    fun loadProdData() {
        // TODO: Check if its start, middle or bottom of list

        // load this, prev and next page
        previousPage?.let { cache[it] = dbService.getPage(it) } ?: println("previousPage $previousPage == null -> deshalb wurde der Service nicht aufgerufen")
        cache[currentPage] = dbService.getPage(currentPage)
        nextPage?.let { cache[it] = dbService.getPage(it) } ?: println("nextPage $nextPage == null -> deshalb wurde der Service nicht aufgerufen")

        // set playlist to current page
        playlists = cache[currentPage] ?: emptyList()

        initPlaylist()
    }

    fun goToNextPage() {
        previousPage = currentPage
        currentPage = nextPage ?: lastPage
        nextPage = if (nextPage == lastPage) null else nextPage!!.plus(1)
    }

    fun goToPreviousPage() {
        nextPage = currentPage
        currentPage = previousPage ?: firstPage
        previousPage = if (previousPage == firstPage) null else previousPage!!.minus(1)
    }

    fun loadCustomizedData(noOfData: Int) {
        //playlists = csvService.requestDataPage(1, noOfData)
        playlists = dbService.getPage(0, noOfData)
        initPlaylist()
    }

    fun setCurrentPlaylist() {
        currentPlaylist.value = playlists[currentPlaylistIndex.value]
        changeFormsContent()
    }

    // Helper Functions
    private fun initPlaylist() {
        currentPlaylist.value = playlists.first()
        changeFormsContent()
    }

    private fun changeFormsContent() {
        name.setValueAsText(currentPlaylist.value.name)
        collaborative.setValueAsText(currentPlaylist.value.collaborative.toString())
        modifiedAt.setValueAsText(currentPlaylist.value.modified_at)
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
        println("Anzahl gefilterte coutry: " + dbService.getFilteredCount("country"))
        //println(dbService.get(2).name)
        dbService.getPage(5, 10, "").forEach {
            println("${it.id} ${it.name}")
        }
        println()
        dbService.getPage(59, 5, "country").forEach {
            println("${it.id} ${it.name}")
        }
    }
}