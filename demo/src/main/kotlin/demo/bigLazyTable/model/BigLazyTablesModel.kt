package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import demo.bigLazyTable.data.CSVService

class BigLazyTablesModel {

    var dataChooserStatus = mutableStateOf(false)

    private val csvService: CSVService = CSVService()

    lateinit var playlists: List<Playlist>

    fun loadTestData() {
        playlists = csvService.requestDataPage(1, 30)
    }

    fun loadProdData() {
        playlists = csvService.requestAllData()
    }

    fun loadCustomizedData(noOfData: Int) {
        playlists = csvService.requestDataPage(1, noOfData)
    }

}