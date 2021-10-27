package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.bigLazyTable.model.BigLazyTablesModel
import demo.bigLazyTable.ui.ComposeListsUI

/**
 * @author Marco Sprenger
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComposeLists"
    ) {
        val model = remember { BigLazyTablesModel() }
        ComposeListsUI(model = model)
    }
}