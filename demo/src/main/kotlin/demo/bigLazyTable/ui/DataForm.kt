package demo.bigLazyTable.ui


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import demo.bigLazyTable.model.BigLazyTablesViewModel
import ui.Form

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun DataForm(model: BigLazyTablesViewModel) {
    Form().of(model)
}