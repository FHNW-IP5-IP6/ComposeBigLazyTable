package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
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
fun main() {

    SqliteDb(
        pathToDb = "./demo/src/main/resources/spotify_playlist_dataset.db",
        caseSensitiveFiltering = true
    ).initializeConnection()

    val mapToPlaylistModels: (List<Any?>, AppState<BaseModel<*>>) -> List<PlaylistModel> = { page, appState ->
        page.map { PlaylistModel(it as Playlist).apply { this.appState = appState } }
    }

    val controller = LazyTableController(
        pagingService = DBService,
        defaultModel = PlaylistModel(Playlist()),
        mapToModels = mapToPlaylistModels
    ) // side effect: init loads first data to display

    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            title = "ComposeLists"
        ) {
            window.minimumSize = Dimension(1000, 800)

            BigLazyTableUI(controller = controller)
        }
    }
}