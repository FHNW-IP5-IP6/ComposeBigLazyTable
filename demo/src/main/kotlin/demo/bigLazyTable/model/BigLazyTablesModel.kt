package demo.bigLazyTable.model

import demo.bigLazyTable.data.CSVService

class BigLazyTablesModel {

    private val csvService: CSVService = CSVService()

    //var playlists: List<Playlist> = csvService.requestAllData()
    var playlists: List<Playlist> = csvService.requestDataPage(10, 10)

}