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
import java.util.*
import kotlin.collections.ArrayList

@Composable
fun HeaderRow(
    horizontalScrollState: ScrollState,
    appState: AppState,
    controller: LazyTableController
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
        for (attribute in appState.defaultPlaylistModel.displayedAttributesInTable) {
            Column {
                if (attribute.canBeFiltered) {

                    TextField(
                        modifier = Modifier.width(180.dp),
                        value = controller.attributeFilter[attribute].toString(),
                        onValueChange = { newValue ->
                            controller.onFiltersChanged(attribute, newValue)
                        },
                        textStyle = TextStyle(color = Color.White),
                        label = { Text("Filter", color = Color.White) },
                        singleLine = true,
                        trailingIcon = {
                            if (controller.attributeFilter[attribute].toString().isNotEmpty()/* != ""*/) {
                                IconButton(onClick = {
//                                    controller.filteredAttributes.remove(attribute)
                                    controller.onFiltersChanged(attribute, "")
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
