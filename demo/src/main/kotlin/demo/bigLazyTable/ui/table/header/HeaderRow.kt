package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.ui.table.TableCell
import demo.bigLazyTable.ui.theme.BackgroundColorHeader

@Composable
fun HeaderRow(
    horizontalScrollState: ScrollState,
    appState: AppState,
    viewModel: LazyTableController
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
                if (attribute.canBeFiltered) {

                    TextField(
                        modifier = Modifier.width(180.dp),
                        value = viewModel.attributeFilter[attribute].toString(), //viewModel.nameFilter,
                        onValueChange = {
                            viewModel.lastFilteredAttribute = attribute // with one filter at a time approach
                            viewModel.filteredAttributes.add(attribute) // with many filters approach

                            viewModel.onFiltersChanged(attribute, it)
                        }, //{ viewModel.onNameFilterChanged(it) },
                        textStyle = TextStyle(color = Color.White),
                        label = { Text("Filter", color = Color.White) },
                        singleLine = true,
                        trailingIcon = {
                            if (/*viewModel.nameFilter*/viewModel.attributeFilter[attribute].toString() != "") {
                                IconButton(onClick = {
                                    viewModel.lastFilteredAttribute = null
                                    viewModel.filteredAttributes.remove(attribute)
                                    viewModel.onFiltersChanged(attribute, "")
//                                    viewModel.onNameFilterChanged("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear Filter",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    )
                } else {
                    TextField(
                        modifier = Modifier.width(180.dp),
                        value = "",
                        onValueChange = {},
                        textStyle = TextStyle(color = Color.White),
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
