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
//        onKeyEvent = TODO: key events would needed to be passed here or for each TextField or other UI Item,
        title = "Person Editor"
    ) {
        window.size = Dimension(1600, 800)

        val model = remember { ComposeFormsPersonModel() }

        Form().of(model)
    }
}