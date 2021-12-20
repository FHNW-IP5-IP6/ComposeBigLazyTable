package demo.bigLazyTable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import demo.bigLazyTable.data.database.Db
import demo.bigLazyTable.model.ViewModelLazyList
import demo.bigLazyTable.ui.BigLazyTableUI
import java.awt.Dimension

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

        Db.initializeConnection()

        val model = remember { ViewModelLazyList } // side effect: init loads first data to display
        BigLazyTableUI(model = model)
    }
}

private fun FrameWindowScope.setWindowSizeToFullscreen() {
    window.apply {
        minimumSize = Dimension(1000, 800)
        placement = WindowPlacement.Maximized
    }
}