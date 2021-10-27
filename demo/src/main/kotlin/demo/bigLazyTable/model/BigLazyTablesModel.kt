package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import demo.bigLazyTable.data.CSVService
import model.BaseModel
import model.attributes.BooleanAttribute
import model.attributes.StringAttribute
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.HeaderGroup

class BigLazyTablesModel : BaseModel<ComposeFormsBigLazyTableLabels>(title = ComposeFormsBigLazyTableLabels.TITLE) {

    var dataChooserStatus = mutableStateOf(false)

    private val csvService: CSVService = CSVService()

    lateinit var playlists: List<Playlist>

    private var currentPlaylist = mutableStateOf(Playlist())
    var currentPlaylistIndex = mutableStateOf(0)

    fun loadTestData() {
        playlists = csvService.requestDataPage(1, 30)
        currentPlaylist.value = playlists[0]
        name.setValueAsText(currentPlaylist.value.name)
        collaborative.setValueAsText(currentPlaylist.value.collaborative.toString())
        modifiedAt.setValueAsText(currentPlaylist.value.modified_at)
    }

    fun loadProdData() {
        playlists = csvService.requestAllData()
        currentPlaylist.value = playlists[0]
        name.setValueAsText(currentPlaylist.value.name)
        collaborative.setValueAsText(currentPlaylist.value.collaborative.toString())
        modifiedAt.setValueAsText(currentPlaylist.value.modified_at)
    }

    fun loadCustomizedData(noOfData: Int) {
        playlists = csvService.requestDataPage(1, noOfData)
        currentPlaylist.value = playlists[0]
        name.setValueAsText(currentPlaylist.value.name)
        collaborative.setValueAsText(currentPlaylist.value.collaborative.toString())
        modifiedAt.setValueAsText(currentPlaylist.value.modified_at)
    }

    fun setCurrentPlaylist() {
        currentPlaylist.value = playlists[currentPlaylistIndex.value]
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
}