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
import java.sql.Connection

class BigLazyTablesModel : BaseModel<ComposeFormsBigLazyTableLabels>(title = ComposeFormsBigLazyTableLabels.TITLE) {

    val ITEMS_FOR_SCREEN_SIZE = 30

    init {
        setupDatabase()
    }

    var dataChooserStatus = mutableStateOf(false)

    private val dbService = DBService()

    lateinit var playlists: List<Playlist>

    private var currentPlaylist = mutableStateOf(Playlist())
    var currentPlaylistIndex = mutableStateOf(0)

    fun loadTestData() {
        playlists = dbService.getPage(0, 30)
        initPlaylist()
    }

    fun loadProdData() {
        //playlists = csvService.requestAllData()
        playlists = dbService.getAll()
        initPlaylist()
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
        currentPlaylist.value = playlists[0]
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