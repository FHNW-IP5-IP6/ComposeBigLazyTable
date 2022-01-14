package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.bigLazyTable.data.database.DBService
import demo.bigLazyTable.data.database.Db
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.BigLazyTableUI
import demo.bigLazyTable.ui.theme.initializeWindowSize

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeLists"
    ) {
        initializeWindowSize()

        Db.initializeConnection()

        val viewModel = remember { LazyTableViewModel(DBService) } // side effect: init loads first data to display
        BigLazyTableUI(viewModel = viewModel)
    }
}