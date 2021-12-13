package demo.bigLazyTable.model

import androidx.compose.runtime.mutableStateOf
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
    val defaultPlaylistFormModel = mutableStateOf(PlaylistFormModel(Playlist()))

    /**
     * Current selected Playlist in LazyList.
     * This is the current FormModel behind the Forms
     */
    val selectedPlaylist = mutableStateOf(defaultPlaylistFormModel.value)

    /**
     * List of FormModels. Size is the totalCount of the provided data.
     * All elements in the LazyList cache are stored in this list. The rest is filled with the defaultFormModel to provide the default loading data.
     */
    val lazyModelList: MutableList<PlaylistFormModel> = ArrayList(Collections.nCopies(DBService.getTotalCount(), defaultPlaylistFormModel.value))

    /**
     * List of all FormModels with changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedFormModels: MutableList<PlaylistFormModel> = mutableListOf()
}