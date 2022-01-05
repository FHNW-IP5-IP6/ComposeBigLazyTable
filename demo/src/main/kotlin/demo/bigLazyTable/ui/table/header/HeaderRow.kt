package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.ui.table.TableCell
import demo.bigLazyTable.ui.theme.BackgroundColorHeader

@Composable
fun HeaderRow(horizontalScrollState: ScrollState) {
    Row(
        modifier = Modifier
            .background(BackgroundColorHeader)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (attribute in AppState.defaultPlaylistModel.lazyListAttributes) {
            TableCell(
                text = attribute.getLabel(),
                color = Color.White,
                backgroundColor = BackgroundColorHeader,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
