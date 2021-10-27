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
import demo.bigLazyTable.model.BigLazyTablesModel

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
@Preview
fun ComposeListsUI(model: BigLazyTablesModel) {
    with(model) {
        DesktopMaterialTheme {
            if (dataChooserStatus.value) {
                MainContent(model)
            } else {
                DataChooser(model)
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun MainContent(model: BigLazyTablesModel) {
    with(model) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                PlaylistList(model, playlists)
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                DataForm(model)
            }
        }
    }
}
