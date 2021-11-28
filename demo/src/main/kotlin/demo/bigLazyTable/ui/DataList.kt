package demo.bigLazyTable.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.AppState
import demo.bigLazyTable.model.ViewModelLazyList
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.model.PlaylistFormModel

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun PlaylistList(model: ViewModelLazyList) {
    val testPlaylist = AppState.uiList

    Column {
        HeaderRow(Playlist())

        val listState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

        val scrollbarStyle = ScrollbarStyle(
            minimalHeight = 16.dp,
            thickness = 12.dp,
            shape = RoundedCornerShape(4.dp),
            hoverDurationMillis = 1000,
            unhoverColor = Color.Red.copy(alpha = 0.3f),
            hoverColor = Color.Red.copy(alpha = 0.8f)
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (listState.firstVisibleItemIndex != model.firstIndex) {
                model.get(listState.firstVisibleItemIndex)
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = listState
            ) {
                items(testPlaylist) { playlistFormModel ->
                    if (playlistFormModel != null) {
                        PlaylistRow(model, playlistFormModel)
                    } else {
                        PlaylistRowPlaceholder()
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(listState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                style = scrollbarStyle
            )
        }
    }
}

// Helpers to get all attributes of an object
private fun Any.getNumberOfAttributes() = javaClass.declaredFields.size - 1 // no id & no $stable
private fun Any.getFieldNameOfIndex(index: Int) = javaClass.declaredFields[index].name
private fun Any.getFieldValueOfIndex(index: Int): Any {
    val field = javaClass.declaredFields[index]
    field.isAccessible = true
    return field.get(this)
}

@Composable
private fun HeaderRow(playlist: Playlist) = LazyRow(
    modifier = Modifier.background(Color.Red).fillMaxWidth().defaultMinSize(minWidth = 30.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    items(count = playlist.getNumberOfAttributes()) { attributeIndex ->
        Box(modifier = Modifier.background(Color.Red).border(width = 2.dp, color = Color.Red)) {
            Text(text = playlist.getFieldNameOfIndex(attributeIndex), color = Color.Black)
        }
    }
}

@Composable
private fun PlaylistRow(model: ViewModelLazyList, playlistFormModel: PlaylistFormModel) {
    val isSelected = AppState.selectedPlaylist.value == playlistFormModel
    val backgroundColor = if (isSelected) { Color.Yellow } else { Color.LightGray }

    LazyRow(
    modifier = Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .defaultMinSize(minWidth = 50.dp)
        .selectable(
            selected = isSelected,
            onClick = {
                model.selectPlaylist(playlistFormModel)
            }
        ),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    items(count = playlistFormModel.playlist.getNumberOfAttributes()) { attributeIndex ->
        Box(
            Modifier.background(backgroundColor).border(width = 2.dp, color = backgroundColor).fillMaxWidth()
        ) {
            Text(text = playlistFormModel.playlist.getFieldValueOfIndex(attributeIndex).toString(), color = Color.Black, modifier = Modifier.background(backgroundColor))
        }
    }
}
}

@Composable
private fun PlaylistRowPlaceholder() = LazyRow(
    modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .defaultMinSize(minWidth = 30.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    item {
        Box(
            Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.LightGray)
        ) {
            Text("")
        }
    }
}