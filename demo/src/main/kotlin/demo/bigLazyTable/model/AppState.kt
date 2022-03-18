package demo.bigLazyTable.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import bigLazyTable.paging.IPagingService
import composeForms.model.BaseModel
import composeForms.model.IModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * TODO: Add description
 * @param pagingService TODO: Add description
 *
 * @author Marco Sprenger, Livio Näf
 */
// TODO: Generisch machen
// TableState
class AppState<T: BaseModel<*>>(pagingService: IPagingService<*>, defaultModel: T) {

    /**
     * Default model used in form and table to store global data
     * Stores:
     * - current Language
     * - default data when table data is loading
     */
//    val defaultTableModel by mutableStateOf(PlaylistModel(Playlist(), this))
    // TODO: Kein PlaylistModel(Playlist()
    val defaultTableModel: T by mutableStateOf(defaultModel)

    /**
     * Current selected model in table.
     * This is the current model behind the form.
     */
    var selectedTableModel: T by mutableStateOf(defaultTableModel)

    /**
     * List of models. Size is the totalCount of the provided data.
     * All elements currently in the table cache are stored in this list. The rest is filled with null.
     */
    var tableModelList: MutableList<T?> = ArrayList(Collections.nCopies(pagingService.getTotalCount(), null))
//    var tableModelList: MutableList<T?> = mutableStateListOf()

    /**
     * List of filtered models. Size is the filteredCount of the provided data.
     * All elements currently in the table cache are stored in this list. The rest is filled with null.
     */
    // TODO: Make 40 dynamic!
    var filteredTableModelList: MutableList<T?> = ArrayList(Collections.nCopies(40, null))

    /**
     * List of all models containing changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedTableModels: MutableList<T> = mutableListOf()
}