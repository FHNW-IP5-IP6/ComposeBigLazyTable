package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import demo.bigLazyTable.data.database.DBService
import demo.bigLazyTable.model.ViewModelLazyList
import demo.bigLazyTable.ui.ComposeListsUI
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
        // Set window size to Window-Fullscreen
        window.placement = WindowPlacement.Maximized
        // Initialize database connection
        setupDatabase()
        // Initialize composeForms.model and load first batch of data to display the UI
        val model = remember { ViewModelLazyList }
        // Initialize UI
        ComposeListsUI(model = model)
    }
}

private fun setupDatabase() {
    Database.connect("jdbc:sqlite:./demo/src/main/resources/spotify_playlist_dataset.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
}