package demo.bigLazyTable.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import demo.bigLazyTable.data.database.DBService
import java.util.*

/**
 * @author Marco Sprenger, Livio Näf
 */
object AppState {

    /**
     * Default PlaylistFormModel to store global data for Form and LazyList
     * Stores:
     * - current Language
     * - default data when LazyList is loading
     */
    val defaultPlaylistModel by mutableStateOf(PlaylistModel(Playlist()))

    /**
     * Current selected Playlist in LazyList.
     * This is the current FormModel behind the Forms
     */
    var selectedPlaylistModel by mutableStateOf(defaultPlaylistModel)

    /**
     * List of FormModels. Size is the totalCount of the provided data.
     * All elements in the LazyList cache are stored in this list. The rest is filled with the defaultFormModel to provide the default loading data.
     */
    val lazyModelList: MutableList<PlaylistModel> = ArrayList(Collections.nCopies(DBService.getTotalCount(), defaultPlaylistModel))
//    val lazyModelList: MutableList<PlaylistModel> = ArrayList(Collections.nCopies(DBService.getTotalCount(), null))

    /**
     * List of all FormModels with changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedFormModels: MutableList<PlaylistModel> = mutableListOf()
}