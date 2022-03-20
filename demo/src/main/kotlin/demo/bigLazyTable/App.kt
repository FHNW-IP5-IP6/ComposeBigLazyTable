package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import demo.bigLazyTable.data.database.DBService
import demo.bigLazyTable.data.database.SqliteDb
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.model.PlaylistModel
import demo.bigLazyTable.ui.BigLazyTableUI
import java.awt.Dimension

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
        remember {
            // Needs remember. Without it, the first language change in no full-screen leads to a full-screen window
            initializeWindowSize()

            // Needs remember. Without it, initializeConnection would be called again (f.e. on language change)
            SqliteDb(
                pathToDb = "./demo/src/main/resources/spotify_playlist_dataset.db",
                caseSensitiveFiltering = true
            ).initializeConnection()
        }

        val service = DBService

        // Needs remember. Without it, the view just shows empty (...) Items (happens after language change)
//        val appState = remember { AppState(pagingService = service) }

        val controller = remember {
            LazyTableController<PlaylistModel>(
                pagingService = service,
//                appState = appState
            ) // side effect: init loads first data to display
        }
        BigLazyTableUI(
            controller = controller,
//            appState = appState
        )
    }
}

fun FrameWindowScope.initializeWindowSize() {
    window.apply {
        minimumSize = Dimension(1000, 800)
        placement = WindowPlacement.Maximized
    }
}