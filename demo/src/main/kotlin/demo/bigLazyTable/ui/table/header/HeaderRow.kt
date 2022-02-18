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
        for (attribute in appState.defaultPlaylistModel.lazyListAttributes) {
            Column {
                if (attribute.getLabel() == "Name") { // at the moment only name can be filtered!
                    TextField(
                        modifier = Modifier.width(180.dp),
                        value = controller.nameFilter,
                        onValueChange = { controller.onNameFilterChanged(it) },
                        textStyle = TextStyle(color = Color.White),
                        label = { Text("Filter", color = Color.White) },
                        singleLine = true,
                        trailingIcon = {
                            if (controller.nameFilter != "") {
                                IconButton(
                                    onClick = { controller.onNameFilterChanged("") },
//                                    TODO: Why does it show the text cursor and not normal/hand?
//                                    https://stackoverflow.com/questions/4274606/how-to-change-cursor-icon-in-java
//                                    https://stackoverflow.com/questions/64855189/clickable-areas-of-image-mouseover-event-jetpack-compose-desktop
//                                    modifier = Modifier.pointerMoveFilter(
//                                        onEnter = {
//                                            frameWindowScope.window.cursor = Cursor(HAND_CURSOR)
//                                            println("On Mouse(pointer) Enter")
//                                            false
//                                        },
//                                        onExit = {
//                                            frameWindowScope.getDefaultCursor()
//                                            println("on Mouse(pointer) Exit")
//                                            false
//                                        })
                                ) {
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
