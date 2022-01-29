package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.bigLazyTable.data.database.DBService
import demo.bigLazyTable.data.database.SqliteDb
import demo.bigLazyTable.model.AppState
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

        SqliteDb(
            pathToDb = "./demo/src/main/resources/spotify_playlist_dataset.db",
            caseSensitiveFiltering = true
        ).initializeConnection()

        val service = DBService
        val appState = AppState(pagingService = service)
        val viewModel = remember {
            LazyTableViewModel(
                pagingService = DBService,
                appState = appState
            ) // side effect: init loads first data to display
        }
        BigLazyTableUI(
            viewModel = viewModel,
            appState = appState
        )
    }
}