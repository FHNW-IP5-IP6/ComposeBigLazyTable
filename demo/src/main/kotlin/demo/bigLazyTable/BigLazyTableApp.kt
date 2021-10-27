package demo.bigLazyTable

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.bigLazyTable.model.BigLazyTablesModel
import demo.bigLazyTable.ui.ComposeListsUI

/**
 * @author Marco Sprenger
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeLists"
    ) {
        ComposeListsUI(model = BigLazyTablesModel())
    }
}