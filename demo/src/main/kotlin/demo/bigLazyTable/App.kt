package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import bigLazyTable.controller.AppState
import bigLazyTable.controller.LazyTableController
import bigLazyTable.data.database.SqliteDb
import demo.bigLazyTable.data.service.DBService
import demo.bigLazyTable.model.PlaylistModel
import bigLazyTable.view.BigLazyTableUI
import composeForms.model.BaseModel
import demo.bigLazyTable.data.service.Playlist
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

        val mapToPlaylistModels: (List<Any?>, AppState<BaseModel<*>>) -> List<PlaylistModel> = { page, appState ->
            page.map { PlaylistModel(it as Playlist).apply { this.appState = appState } }
        }

        val controller = remember {
            LazyTableController(
                pagingService = service,
                defaultModel = PlaylistModel(Playlist()),
                mapToModels = mapToPlaylistModels
            ) // side effect: init loads first data to display
        }
        BigLazyTableUI(controller = controller)
    }
}

fun FrameWindowScope.initializeWindowSize() {
    window.apply {
        minimumSize = Dimension(1000, 800)
        placement = WindowPlacement.Maximized
    }
}