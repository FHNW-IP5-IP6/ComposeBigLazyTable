package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute
import composeForms.ui.theme.BackgroundColorHeader
import demo.bigLazyTable.model.*
import demo.bigLazyTable.ui.table.AttributeTableCell
import demo.bigLazyTable.ui.table.TableCell

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
                    AttributeTableCell(
                        isTitle = true,
                        attribute = attribute,
                        color = Color.White,
                        backgroundColor = BackgroundColorHeader
                    )
//                    TableCell(
//                        text = attribute.getLabel(),
//                        color = Color.White,
//                        backgroundColor = BackgroundColorHeader,
//                        fontWeight = FontWeight.Bold
//                    )
                    SortButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        attribute = attribute,
                        controller = controller
                    )
                }
            }
        }
    }
}