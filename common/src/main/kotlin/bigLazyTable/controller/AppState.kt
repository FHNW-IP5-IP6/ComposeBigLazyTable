package bigLazyTable.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import bigLazyTable.data.paging.IPagingService
import composeForms.model.BaseModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * TODO: Guet so?
 * This class saves the state of the Table and keeps track of all changed table models.
 *
 * @param pagingService sets initial size of [tableModelList]
 * @param defaultModel is used for [defaultTableModel] to observe global data (language, default data)
 * @param pageSize sets initial size of [filteredTableModelList]
 *
 * @author Marco Sprenger, Livio Näf
 */
// TODO-Future: Could be renamed to TableState
class AppState<T: BaseModel<*>>(pagingService: IPagingService<*>, defaultModel: T, pageSize: Int) {

    /**
     * Default model used in form and table to store global data
     * Stores:
     * - current Language
     * - default data when table data is loading
     */
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

    /**
     * List of filtered models. Size is the filteredCount of the provided data.
     * All elements currently in the table cache are stored in this list. The rest is filled with null.
     */
    var filteredTableModelList: MutableList<T?> = ArrayList(Collections.nCopies(pageSize, null))

    /**
     * List of all models containing changes. Used to prevent loosing changed data if new data is loaded from the service.
     */
    val changedTableModels: MutableList<T> = mutableListOf()
}