package bigLazyTable.view.form

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import composeForms.model.BaseModel
import composeForms.ui.Form
import bigLazyTable.controller.AppState

/**
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun <T: BaseModel<*>> RowScope.FormContainer(weight: Float, model: T, appState: AppState<out BaseModel<*>>) {
    Box(modifier = Modifier.weight(weight)) {
        Form().of(model, appState)
    }
}