package demo.bigLazyTable.ui.table

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.model.PlaylistModel
import demo.bigLazyTable.ui.theme.*

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun RowScope.TableContainer(weight: Float, viewModel: LazyTableViewModel) {
    val horizontalScrollState = rememberScrollState()

    Box(modifier = Modifier.weight(weight)) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            verticalArrangement = Arrangement.Top
        ) {
            PageInfoRow(viewModel = viewModel)
            HeaderRow(horizontalScrollState = horizontalScrollState)
            LazyTable(
                viewModel = viewModel,
                horizontalScrollState = horizontalScrollState
            )
        }

        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            style = CustomScrollbarStyle
        )
    }
}