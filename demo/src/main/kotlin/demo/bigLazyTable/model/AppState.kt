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
     * Default PlaylistModel to store global data for Form and LazyList
     * Stores:
     * - current Language
     * - default data when LazyList is loading
     */
    val defaultPlaylistModel by mutableStateOf(PlaylistModel(Playlist()))
    val testDefaultPlaylistModel = PlaylistModel(Playlist())

    /**
     * Current selected Playlist in LazyList.
     * This is the current Model behind the Forms
     */
    var selectedPlaylistModel by mutableStateOf(defaultPlaylistModel)

    /**
     * List of Models. Size is the totalCount of the provided data.
     * All elements in the LazyList cache are stored in this list. The rest is filled with the defaultPlaylistModel to provide the default loading data.
     */
    val lazyModelList: MutableList<PlaylistModel?> = ArrayList(Collections.nCopies(DBService.getTotalCount(), null))
//    val lazyModelList: MutableList<PlaylistModel> = ArrayList(Collections.nCopies(DBService.getTotalCount(), null))

    /**
     * List of all Models with changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedPlaylistModels: MutableList<PlaylistModel> = mutableListOf()
}