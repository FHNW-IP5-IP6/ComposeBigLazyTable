package demo.bigLazyTable.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bigLazyTable.paging.IPagingService
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * @author Marco Sprenger, Livio Näf
 */
class AppState(pagingService: IPagingService<*>) {

    /**
     * Default PlaylistModel to store global data for Form and LazyList
     * Stores:
     * - current Language
     * - default data when LazyList is loading
     */
    val defaultPlaylistModel by mutableStateOf(PlaylistModel(Playlist(), this))
    val testDefaultPlaylistModel = PlaylistModel(Playlist(), this) // TODO: Check this

    /**
     * Current selected Playlist in LazyList.
     * This is the current Model behind the Forms
     */
    var selectedPlaylistModel by mutableStateOf(defaultPlaylistModel)

    /**
     * List of Models. Size is the totalCount of the provided data.
     * All elements in the LazyList cache are stored in this list. The rest is filled with the defaultPlaylistModel to
     * provide the default loading data.
     */
    var lazyModelList: MutableList<PlaylistModel?> = ArrayList(Collections.nCopies(pagingService.getTotalCount(), null))

    /**
     * List of filtered Models. Size is the filteredCount of the provided data.
     * All elements in the filteredList cache are stored in this list. The rest is filled with the defaultPlaylistModel
     * to provide the default loading data.
     */
    lateinit var filteredList: MutableList<PlaylistModel?>

    /**
     * List of all Models with changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedPlaylistModels: MutableList<PlaylistModel> = mutableListOf()
}