package demo.bigLazyTable.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bigLazyTable.paging.IPagingService
import java.util.*
import kotlin.collections.ArrayList

/**
 * TODO: Add description
 * @param pagingService TODO: Add description
 *
 * @author Marco Sprenger, Livio Näf
 */
class AppState(pagingService: IPagingService<*>) {

    /**
     * Default model used in form and table to store global data
     * Stores:
     * - current Language
     * - default data when table data is loading
     */
    val defaultTableModel by mutableStateOf(PlaylistModel(Playlist(), this))

    /**
     * Current selected model in table.
     * This is the current model behind the form.
     */
    var selectedTableModel by mutableStateOf(defaultTableModel)

    /**
     * List of models. Size is the totalCount of the provided data.
     * All elements currently in the table cache are stored in this list. The rest is filled with null.
     */
    var tableModelList: MutableList<PlaylistModel?> = ArrayList(Collections.nCopies(pagingService.getTotalCount(), null))

    /**
     * List of filtered models. Size is the filteredCount of the provided data.
     * All elements currently in the table cache are stored in this list. The rest is filled with null.
     */
    var filteredTableModelList: MutableList<PlaylistModel?> = ArrayList(Collections.nCopies(40, null))

    /**
     * List of all models containing changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedTableModels: MutableList<PlaylistModel> = mutableListOf()
}