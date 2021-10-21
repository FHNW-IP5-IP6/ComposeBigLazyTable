package demo.biglazytable

import androidx.compose.ui.window.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.application
import ui.Form

@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun main() =  application {
    Window(
        title = "Person Editor",
        onCloseRequest = ::exitApplication
    ) {

        val model = remember { ExampleModel() }
        App(model)

    }
}