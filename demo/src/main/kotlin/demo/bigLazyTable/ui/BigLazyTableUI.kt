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
import demo.bigLazyTable.model.BigLazyTablesViewModel

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
@Preview
fun ComposeListsUI(model: BigLazyTablesViewModel) {
    model.initialLoad()
    with(model) {
        DesktopMaterialTheme {
            MainContent(this)
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun MainContent(model: BigLazyTablesViewModel) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            PlaylistList(model)
        }
        Box(modifier = Modifier.weight(1f)) {
            DataForm(model)
        }
    }
}
