package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
import demo.bigLazyTable.data.database.DBService
import java.util.*

/**
 * @author Marco Sprenger, Livio Näf
 */
object AppState {

    val lazyModelList: MutableList<PlaylistFormModel?> = ArrayList(Collections.nCopies(DBService().getTotalCount(), null))

    val selectedPlaylist = mutableStateOf(PlaylistFormModel(Playlist()))

}