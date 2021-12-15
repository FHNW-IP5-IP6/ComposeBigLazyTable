package demo.bigLazyTable.ui

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.ViewModelLazyList

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
@Preview
fun BigLazyTableUI(model: ViewModelLazyList) {
    DesktopMaterialTheme {
        MainContent(model = model)
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun MainContent(model: ViewModelLazyList) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(2f)) {
            DataTable(model = model)
        }
        Box(modifier = Modifier.weight(3f)) {
            val playlistFormModel = AppState.selectedPlaylist
            DataForm(model = playlistFormModel)
        }
    }
}
