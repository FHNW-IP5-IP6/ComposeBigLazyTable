package demo.bigLazyTable.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import demo.bigLazyTable.controller.LazyTableController
import demo.bigLazyTable.view.form.FormContainer
import demo.bigLazyTable.view.table.TableContainer
import demo.bigLazyTable.view.theme.BigLazyTableTheme

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun BigLazyTableUI(controller: LazyTableController<*>) {
    BigLazyTableTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            TableContainer(
                weight = 2f,
                controller = controller,
                appState = controller.appState
            )
            FormContainer(
                weight = 3f,
                model = controller.appState.selectedTableModel,
                appState = controller.appState
            )
        }
    }
}