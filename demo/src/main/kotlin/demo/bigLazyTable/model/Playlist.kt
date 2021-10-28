package demo.bigLazyTable.model

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
data class Playlist(
    val name: String = "",
    val collaborative: Boolean = false,
    val modified_at: String = "",
/*    val num_tracks: String = "",
    val num_albums: String = "",
    val num_followers: String = "",
    val num_edits: String = "",
    val duration_ms: String = "",
    val num_artists: String = "",
    val track0_artist_name: String = "",
    val track0_track_name: String = "",
    val track0_duration_ms: String = "",
    val track0_album_name: String = "",
    val track1_artist_name: String = "",
    val track1_track_name: String = "",
    val track1_duration_ms: String = "",
    val track1_album_name: String = "",
    val track2_artist_name: String = "",
    val track2_track_name: String = "",
    val track2_duration_ms: String = "",
    val track2_album_name: String = "",
    val track3_artist_name: String = "",
    val track3_track_name: String = "",
    val track3_duration_ms: String = "",
    val track3_album_name: String = "",*/
    val id: Long = 0
)
