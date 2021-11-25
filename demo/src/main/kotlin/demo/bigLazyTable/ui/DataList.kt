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
    val playlists = AppState.uiList

    Column {
        //HeaderRow(playlists.first().playlist)

        var listState = rememberLazyListState()
        //println("visible items:" + listState.layoutInfo.visibleItemsInfo.size)

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
            val last = listState.layoutInfo.visibleItemsInfo
            //val timeToLoadNextPage = if (last.isNotEmpty()) last.last().index == playlists.lastIndex else false
            val timeToLoadNextPage = listState.firstVisibleItemIndex == (playlists.size*0.25).toInt()

            if (timeToLoadNextPage) {
                //listState = rememberLazyListState()
                model.get(last.last().index + 1)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state = listState
            ) {
                // LazyList
                items(playlists) { playlistFormModel ->
                    PlaylistRow(model, playlistFormModel.playlist)
                }
                items(count = 1_000_000) { index ->

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
        Box(modifier = Modifier.background(Color.Red).border(width = 2.dp, color = Color.White)) {
            Text(text = playlist.getFieldNameOfIndex(attributeIndex), color = Color.Blue)
        }
    }
}

@Composable
private fun PlaylistRow(model: ViewModelLazyList, playlist: Playlist) = LazyRow(
    modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .defaultMinSize(minWidth = 30.dp),
//        .selectable(
//            selected = AppState.selectedPlaylist ==
//            model.currentPlaylistIndex.value == model.playlists.indexOf(playlist),
//            onClick = {
//                model.currentPlaylistIndex.value = model.playlists.indexOf(playlist)
//                model.setCurrentPlaylist()
//            }
//        ),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    items(count = playlist.getNumberOfAttributes()) { attributeIndex ->
        Box(
            Modifier.background(Color.White).border(width = 2.dp, color = Color.White)
//            if (model.currentPlaylistIndex.value == model.playlists.indexOf(playlist)) {
//                Modifier.background(Color.White).border(width = 2.dp, color = Color.White)
//            } else {
//                Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
//            }
        ) {
            Text(text = playlist.getFieldValueOfIndex(attributeIndex).toString(), color = Color.Blue)
        }
    }
}