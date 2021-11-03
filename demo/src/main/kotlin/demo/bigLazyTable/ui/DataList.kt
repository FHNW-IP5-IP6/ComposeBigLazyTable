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
import demo.bigLazyTable.model.BigLazyTablesViewModel
import demo.bigLazyTable.model.Playlist

@Composable
fun PlaylistList(model: BigLazyTablesViewModel) {
    val playlists = model.playlists

    Column {
        HeaderRow(model = model, playlists.first())

        val listState = rememberLazyListState()
        println("visible items:" + listState.layoutInfo.visibleItemsInfo.size)

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
            val info = listState.layoutInfo.visibleItemsInfo
            val timeToLoadNextPage = if (info.isNotEmpty()) info.last().index == playlists.lastIndex else false // TODO: When position is at the last item of the list

            // TODO: Now its buggy and loads all new items immediately -> specify timeToLoadNextPage more specific
            if (timeToLoadNextPage) {
                model.loadNextPage()
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = listState
            ) {
                items(playlists) { playlist ->
                    PlaylistRow(model, playlist)
                }
            }

            println("last visible index:" + if (info.isNotEmpty()) info.last().index else "empty")

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
private fun HeaderRow(model: BigLazyTablesViewModel, playlist: Playlist) = LazyRow(
    modifier = Modifier.background(Color.Red).fillMaxWidth().defaultMinSize(minWidth = 30.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    items(count = playlist.getNumberOfAttributes()) { attributeIndex ->
        Box(modifier = Modifier.background(Color.Red).border(width = 2.dp, color = Color.White)) {
            Text(text = playlist.getFieldNameOfIndex(attributeIndex), color = Color.Blue)
        }
    }
}

@Composable
private fun PlaylistRow(model: BigLazyTablesViewModel, playlist: Playlist) = LazyRow(
    modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .defaultMinSize(minWidth = 30.dp)
        .selectable(
            selected = model.currentPlaylistIndex.value == model.playlists.indexOf(playlist),
            onClick = {
                model.currentPlaylistIndex.value = model.playlists.indexOf(playlist)
                model.setCurrentPlaylist()
            }
        ),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    items(count = playlist.getNumberOfAttributes()) { attributeIndex ->
        Box(
            if (model.currentPlaylistIndex.value == model.playlists.indexOf(playlist)) {
                Modifier.background(Color.White).border(width = 2.dp, color = Color.White)
            } else {
                Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
            }
        ) {
            Text(text = playlist.getFieldValueOfIndex(attributeIndex).toString(), color = Color.Blue)
        }
    }
}