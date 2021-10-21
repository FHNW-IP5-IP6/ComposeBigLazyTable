package demo.mountainForm

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import demo.mountainForm.model.MountainPM
import demo.mountainForm.service.MountainService
import demo.mountainForm.service.serviceimpl.MountainServiceImpl
import ui.Form


@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun main() = Window(
    title = "Mountain Editor",
    size = IntSize(1200, 800)
) {
    val service: MountainService = remember { MountainServiceImpl() }
    val model                    = remember { MountainPM(service)   }

    Form().of(model)
}