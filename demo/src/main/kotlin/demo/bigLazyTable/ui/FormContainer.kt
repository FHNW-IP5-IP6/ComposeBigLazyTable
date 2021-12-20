package demo.bigLazyTable.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import composeForms.ui.Form
import demo.bigLazyTable.model.PlaylistModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun RowScope.FormContainer(weight: Float, model: PlaylistModel) {
    Box(modifier = Modifier.weight(weight)) {
        Form().of(model)
    }
}