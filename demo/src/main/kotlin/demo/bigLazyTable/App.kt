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
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.BigLazyTableUI
import java.awt.Cursor
import java.awt.Dimension

lateinit var frameWindowScope: FrameWindowScope

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
        // TODO: https://github.com/JetBrains/compose-jb/tree/master/tutorials/Mouse_Events#mouse-scroll-listeners
//          https://github.com/JetBrains/compose-jb/tree/master/tutorials/Mouse_Events#mouse-rightmiddle-clicks-and-keyboard-modifiers

        frameWindowScope = this

        // Needs remember. Without it, the first language change in no full-screen leads to a full-screen window
        remember { initializeWindowSize() }

        // Needs remember. Without it, initializeConnection would be called again (f.e. on language change)
        remember {
            SqliteDb(
                pathToDb = "./demo/src/main/resources/spotify_playlist_dataset.db",
                caseSensitiveFiltering = true
            ).initializeConnection()
        }

        val service = DBService

        // Needs remember. Without it, the view just shows empty (...) Items (happens after language change)
        val appState = remember { AppState(pagingService = service) }

        val viewModel = remember {
            LazyTableViewModel(
                pagingService = service,
                appState = appState
            ) // side effect: init loads first data to display
        }
        BigLazyTableUI(
            viewModel = viewModel,
            appState = appState
        )
    }
}

fun FrameWindowScope.initializeWindowSize() {
    window.apply {
        minimumSize = Dimension(1000, 800)
        placement = WindowPlacement.Maximized
    }
}

fun FrameWindowScope.getDefaultCursor() {
    window.cursor = Cursor.getDefaultCursor()
}