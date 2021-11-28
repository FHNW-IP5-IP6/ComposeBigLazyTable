package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import demo.bigLazyTable.data.database.DBService
import java.util.*

object AppState {

    val uiList: MutableList<PlaylistFormModel?> = ArrayList(Collections.nCopies(DBService().getTotalCount(), null))

    val selectedPlaylist = mutableStateOf(PlaylistFormModel(Playlist()))

}