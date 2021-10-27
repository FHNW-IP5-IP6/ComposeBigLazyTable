package demo.bigLazyTable.ui


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import demo.bigLazyTable.model.BigLazyTablesModel
import ui.Form

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun DataForm(model: BigLazyTablesModel) {
    Form().of(model)
}