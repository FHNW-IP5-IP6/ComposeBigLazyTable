package demo.bigLazyTable

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import demo.bigLazyTable.model.BigLazyTablesModel
import demo.bigLazyTable.model.Playlist

/**
 * @author Marco Sprenger
 */
@Composable
@Preview
fun ComposeListsUI(model: BigLazyTablesModel) {
    with(model) {
        DesktopMaterialTheme {
            PlaylistList(playlists)
        }
    }
}

@Composable
private fun PlaylistList(playlists: List<Playlist>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier.background(Color.DarkGray).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(text = "name", color = Color.White)
                }
                item {
                    Text(text = "collaborative", color = Color.White)
                }
                item {
                    Text(text = "modified_at", color = Color.White)
                }
                item {
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
                }
            }
        }
        items(playlists) { playlist ->
            PlaylistRow(playlist)
        }
    }
}

@Composable
private fun PlaylistRow(playlist: Playlist) {
    LazyRow(
        modifier = Modifier.background(Color.DarkGray).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),

    ) {
        item {
            Text(text = playlist.name, color = Color.White)
        }
        item {
            Text(text = playlist.collaborative.toString(), color = Color.White)
        }
        item {
            Text(text = playlist.modified_at, color = Color.White)
        }
        item {
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
        }
    }
}