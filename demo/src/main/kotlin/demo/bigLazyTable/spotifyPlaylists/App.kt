package demo.bigLazyTable.spotifyPlaylists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.window.*
import bigLazyTable.controller.LazyTableController
import bigLazyTable.data.database.SqliteDb
import demo.bigLazyTable.spotifyPlaylists.data.service.DBService
import demo.bigLazyTable.spotifyPlaylists.model.PlaylistModel
import bigLazyTable.view.BigLazyTableUI
import demo.bigLazyTable.spotifyPlaylists.data.service.Playlist
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

    val controller = LazyTableController(
        pagingService = DBService,
        defaultModel = PlaylistModel(Playlist()),
        mapToModels = { page, appState ->
            page.map { PlaylistModel(it as Playlist).apply { this.appState = appState } }
        }
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