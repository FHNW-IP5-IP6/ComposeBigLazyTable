package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableViewModel
import demo.bigLazyTable.ui.table.TableCell
import demo.bigLazyTable.ui.theme.BackgroundColorHeader

@Composable
fun HeaderRow(
    horizontalScrollState: ScrollState,
    appState: AppState,
    viewModel: LazyTableViewModel
) {
    Row(
        modifier = Modifier
            .background(BackgroundColorHeader)
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .horizontalScroll(horizontalScrollState),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (attribute in appState.defaultPlaylistModel.lazyListAttributes) {
            Column {
                if (attribute.getLabel() == "Name") { // at the moment only name can be filtered!
                    TextField(
                        modifier = Modifier.width(180.dp),
                        value = viewModel.nameFilter,
                        onValueChange = { viewModel.onNameFilterChanged(it) },
                        textStyle = TextStyle(color = Color.White),
                        label = { Text("Filter", color = Color.White) },
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear Filter",
                                modifier = Modifier.clickable {
                                    viewModel.onNameFilterChanged("")
                                }
                            )
                        }
                    )
                } else {
                    TextField(
                        modifier = Modifier.width(180.dp),
                        value = "",
                        onValueChange = {},
                        textStyle = TextStyle(color = Color.White),
//                        label = { Text("Filter", color = Color.White) },
                        singleLine = true,
                        enabled = false
                    )
                }
                TableCell(
                    text = attribute.getLabel(),
                    color = Color.White,
                    backgroundColor = BackgroundColorHeader,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
