package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.DBService
import java.util.*

object AppState {

    var uiList: List<PlaylistFormModel> = listOf()
    val testList: MutableList<PlaylistFormModel?> = ArrayList(Collections.nCopies(DBService().getTotalCount(), null))

    //var selectedPlaylist: PlaylistFormModel = testList[0]!!

}