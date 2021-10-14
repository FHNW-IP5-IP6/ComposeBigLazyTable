package demo.composeLists

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

/**
 * @author Marco Sprenger
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeLists"
    ) {
        ComposeListsUI(model = ComposeListsModel())
    }
}