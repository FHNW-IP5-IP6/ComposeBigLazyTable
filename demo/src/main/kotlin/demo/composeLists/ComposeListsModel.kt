package demo.composeLists

class ComposeListsModel {

    private val csvService: CSVService = CSVService()

    //var playlists: List<Playlist> = csvService.requestAllData()
    var playlists: List<Playlist> = csvService.requestDataPage(10, 10)

}