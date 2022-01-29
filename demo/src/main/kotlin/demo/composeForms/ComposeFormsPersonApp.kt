package demo.composeForms

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import composeForms.ui.Form


@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun main() = Window(
    title = "Person Editor",
    size = IntSize(1600, 800)
) {
    val model = remember { ComposeFormsPersonModel() }

//    Form().of(model, appState)
}