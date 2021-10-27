package demo.bigLazyTable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.BigLazyTablesModel
import demo.bigLazyTable.model.Playlist

@Composable
fun PlaylistList(model: BigLazyTablesModel, playlists: List<Playlist>) {
    Column(
        content = {
            LazyRow(
                modifier = Modifier.background(Color.LightGray).fillMaxWidth().defaultMinSize(minWidth = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
                    ) {
                        Text(text = "name", color = Color.Blue)
                    }
                }
                item {
                    Box(
                        modifier = Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
                    ) {
                        Text(text = "collaborative", color = Color.Blue)
                    }
                }
                item {
                    Box(
                        modifier = Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
                    ) {
                        Text(text = "modified_at", color = Color.Blue)
                    }
                }
                /*item {
                    Text(text = "num_tracks", color = Color.White)
                }
                item {
                    Text(text = "num_albums", color = Color.White)
                }
                item {
                    Text(text = "num_followers", color = Color.White)
                }
                item {
                    Text(text = "num_edits", color = Color.White)
                }
                item {
                    Text(text = "duration_ms", color = Color.White)
                }
                item {
                    Text(text = "num_artists", color = Color.White)
                }
                item {
                    Text(text = "track0_artist_name", color = Color.White)
                }
                item {
                    Text(text = "track0_track_name", color = Color.White)
                }
                item {
                    Text(text = "track0_duration_ms", color = Color.White)
                }
                item {
                    Text(text = "track0_album_name", color = Color.White)
                }
                item {
                    Text(text = "track1_artist_name", color = Color.White)
                }
                item {
                    Text(text = "track1_track_name", color = Color.White)
                }
                item {
                    Text(text = "track1_duration_ms", color = Color.White)
                }
                item {
                    Text(text = "track1_album_name", color = Color.White)
                }
                item {
                    Text(text = "track2_artist_name", color = Color.White)
                }
                item {
                    Text(text = "track2_track_name", color = Color.White)
                }
                item {
                    Text(text = "track2_duration_ms", color = Color.White)
                }
                item {
                    Text(text = "track2_album_name", color = Color.White)
                }
                item {
                    Text(text = "track3_artist_name", color = Color.White)
                }
                item {
                    Text(text = "track3_track_name", color = Color.White)
                }
                item {
                    Text(text = "track3_duration_ms", color = Color.White)
                }
                item {
                    Text(text = "track3_album_name", color = Color.White)
                }*/
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistRow(model, playlist)
                }
            }
        }
    )

}

@Composable
private fun PlaylistRow(model: BigLazyTablesModel, playlist: Playlist) {
    LazyRow(
        modifier = Modifier.background(Color.LightGray).fillMaxWidth().defaultMinSize(minWidth = 30.dp).selectable(
            selected = model.currentPlaylistIndex.value == model.playlists.indexOf(playlist),
            onClick = {
                model.currentPlaylistIndex.value = model.playlists.indexOf(playlist)
                model.setCurrentPlaylist()
            }
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
        item {
            Box(
                if (model.currentPlaylistIndex.value == model.playlists.indexOf(playlist)) {
                    Modifier.background(Color.White).border(width = 2.dp, color = Color.White)
                } else {
                    Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
                }
            ) {
                Text(text = playlist.name, color = Color.Blue)
            }
        }
        item {
            Box(
                if (model.currentPlaylistIndex.value == model.playlists.indexOf(playlist)) {
                    Modifier.background(Color.White).border(width = 2.dp, color = Color.White)
                } else {
                    Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
                }
            ) {
                Text(text = playlist.collaborative.toString(), color = Color.Blue)
            }
        }
        item {
            Box(
                if (model.currentPlaylistIndex.value == model.playlists.indexOf(playlist)) {
                    Modifier.background(Color.White).border(width = 2.dp, color = Color.White)
                } else {
                    Modifier.background(Color.LightGray).border(width = 2.dp, color = Color.White)
                }
            ) {
                Text(text = playlist.modified_at, color = Color.Blue)
            }
        }
        /*item {
            Text(text = playlist.num_tracks, color = Color.White)
        }
        item {
            Text(text = playlist.num_albums, color = Color.White)
        }
        item {
            Text(text = playlist.num_followers, color = Color.White)
        }
        item {
            Text(text = playlist.num_edits, color = Color.White)
        }
        item {
            Text(text = playlist.duration_ms, color = Color.White)
        }
        item {
            Text(text = playlist.num_artists, color = Color.White)
        }
        item {
            Text(text = playlist.track0_artist_name, color = Color.White)
        }
        item {
            Text(text = playlist.track0_track_name, color = Color.White)
        }
        item {
            Text(text = playlist.track0_duration_ms, color = Color.White)
        }
        item {
            Text(text = playlist.track0_album_name, color = Color.White)
        }
        item {
            Text(text = playlist.track1_artist_name, color = Color.White)
        }
        item {
            Text(text = playlist.track1_track_name, color = Color.White)
        }
        item {
            Text(text = playlist.track1_duration_ms, color = Color.White)
        }
        item {
            Text(text = playlist.track1_album_name, color = Color.White)
        }
        item {
            Text(text = playlist.track2_artist_name, color = Color.White)
        }
        item {
            Text(text = playlist.track2_track_name, color = Color.White)
        }
        item {
            Text(text = playlist.track2_duration_ms, color = Color.White)
        }
        item {
            Text(text = playlist.track2_album_name, color = Color.White)
        }
        item {
            Text(text = playlist.track3_artist_name, color = Color.White)
        }
        item {
            Text(text = playlist.track3_track_name, color = Color.White)
        }
        item {
            Text(text = playlist.track3_duration_ms, color = Color.White)
        }
        item {
            Text(text = playlist.track3_album_name, color = Color.White)
        }*/
    }
}