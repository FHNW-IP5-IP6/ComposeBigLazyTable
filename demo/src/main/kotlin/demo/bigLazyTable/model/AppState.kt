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
    val testDefaultPlaylistModel = PlaylistModel(Playlist()) // TODO: Check this

    /**
     * Current selected Playlist in LazyList.
     * This is the current Model behind the Forms
     */
    var selectedPlaylistModel by mutableStateOf(defaultPlaylistModel)

    /**
     * List of Models. Size is the totalCount of the provided data.
     * All elements in the LazyList cache are stored in this list. The rest is filled with the defaultPlaylistModel to provide the default loading data.
     */
    // TODO: Problem! Is it clean to access getTotalCount() on the object DBService?
    //  What if a service is no object and thus cannot be called like that?
    //  Now if we need to pass the pageSize here it makes things weird...
    //  lazyModelList must exactly be the amount of items the service provides -> a fix value will maybe hide some more data if its bigger than that value
    val lazyModelList: MutableList<PlaylistModel?> = ArrayList(Collections.nCopies(/*DBService.getTotalCount()*//*DBService(pageSize = 40).getTotalCount()*/1_000_000, null))

    /**
     * List of all Models with changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedPlaylistModels: MutableList<PlaylistModel> = mutableListOf()
}