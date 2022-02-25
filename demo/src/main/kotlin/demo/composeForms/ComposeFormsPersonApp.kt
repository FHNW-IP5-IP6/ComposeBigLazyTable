package demo.composeForms

import androidx.compose.ui.window.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.application
import composeForms.ui.Form
import java.awt.Dimension


@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Person Editor"
    ) {
        window.size = Dimension(1600, 800)

        val model = remember { ComposeFormsPersonModel() }

        Form().of(model)
    }
}