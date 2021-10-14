package demo.composeLists

class ComposeListsModel {

    private val csvService: CSVService = CSVService()

    var playlists: List<Playlist> = csvService.loadData()

}