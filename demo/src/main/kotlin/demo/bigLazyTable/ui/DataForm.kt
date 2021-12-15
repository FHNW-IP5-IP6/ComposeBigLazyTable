package demo.bigLazyTable.ui


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import demo.bigLazyTable.model.PlaylistFormModel
import composeForms.ui.Form

/**
 * Creates a ComposeForm with the data from the selected Playlist.
 *
 * @author Marco Sprenger, Livio Näf
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun DataForm(model: PlaylistFormModel) = Form().of(model)