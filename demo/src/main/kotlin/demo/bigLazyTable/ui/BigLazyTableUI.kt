package demo.bigLazyTable.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.ui.form.FormContainer
import demo.bigLazyTable.ui.table.TableContainer
import demo.bigLazyTable.ui.theme.BigLazyTableTheme

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun BigLazyTableUI(controller: LazyTableController, appState: AppState) {
    BigLazyTableTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            TableContainer(
                weight = 2f,
                controller = controller,
                appState = appState
            )
            FormContainer(
                weight = 3f,
                model = appState.selectedPlaylistModel,
                appState = appState
            )
        }
    }
}