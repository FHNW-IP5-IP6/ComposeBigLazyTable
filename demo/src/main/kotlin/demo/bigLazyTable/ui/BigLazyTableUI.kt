package demo.bigLazyTable.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.theme.BigLazyTableTheme

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun BigLazyTableUI(viewModel: LazyTableViewModel) {
    BigLazyTableTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            TableContainer(weight = 2f, viewModel = viewModel)
            FormContainer(weight = 3f, model = AppState.selectedPlaylistModel)
        }
    }
}