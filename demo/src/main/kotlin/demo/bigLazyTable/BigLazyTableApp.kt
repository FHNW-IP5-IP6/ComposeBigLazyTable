package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import demo.bigLazyTable.model.ViewModelLazyList
import demo.bigLazyTable.ui.BigLazyTableUI
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

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
        setWindowSizeToFullscreen()

        initializeDatabaseConnection()

        val model = remember { ViewModelLazyList } // side effect: init loads first data to display
        BigLazyTableUI(model = model)
    }
}

private fun FrameWindowScope.setWindowSizeToFullscreen() {
    window.placement = WindowPlacement.Maximized
}

private fun initializeDatabaseConnection() {
    Database.connect("jdbc:sqlite:./demo/src/main/resources/spotify_playlist_dataset.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
}