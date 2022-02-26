package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bigLazyTable.paging.Sort
import composeForms.ui.theme.BackgroundColorHeader
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.LazyTableController
import demo.bigLazyTable.model.SortState
import demo.bigLazyTable.ui.table.TableCell
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder

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
                FilterTextField(
                    attribute = attribute,
                    controller = controller
                )
                Box {
                    TableCell(
                        text = attribute.getLabel(),
                        color = Color.White,
                        backgroundColor = BackgroundColorHeader,
                        fontWeight = FontWeight.Bold
                    )
//                    var x by remember { controller.sortState }
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            when (controller.sortState) {
                                null -> {
                                    controller.sortState = SortOrder.ASC
                                    controller.sort = Sort(
                                        dbField = attribute.databaseField as Column<String>,
                                        sorted = SortOrder.ASC
                                    )
                                    controller.isSorting = true
                                }
                                SortOrder.ASC -> {
                                    controller.sortState = SortOrder.DESC
                                    controller.sort = Sort(
                                        dbField = attribute.databaseField as Column<String>,
                                        sorted = SortOrder.DESC
                                    )
                                    controller.isSorting = true
                                }
                                SortOrder.DESC -> {
                                    controller.sortState = null
                                    controller.sort = null
                                    controller.isSorting = false
                                }
                                else -> { /* TODO: Was hier? */ }
                            }
                        }
                    ) {
                        Icon(
                            imageVector =
//                            if (x) Icons.Default.Add else Icons.Default.Sort
                            when (controller.sortState) {
                                SortOrder.ASC   -> Icons.Default.SortByAlpha
                                SortOrder.DESC  -> Icons.Default.Close
                                null            -> Icons.Default.Sort
                                else            -> Icons.Default.Error
                            }
                            ,
                            contentDescription = "Sortieren",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}