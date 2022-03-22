package demo.bigLazyTable.data.service

import demo.bigLazyTable.controller.AppState
import demo.bigLazyTable.data.database.DatabasePlaylists
import demo.bigLazyTable.model.PlaylistModel
import org.jetbrains.exposed.sql.ResultRow

/**
 * @author Marco Sprenger, Livio Näf
 */

const val loadingPlaceholderString = "..."
const val loadingPlaceholderNumber = -999_999

data class Playlist(
    val id: Long = loadingPlaceholderNumber.toLong(),
    val name: String = loadingPlaceholderString,
    val collaborative: Boolean = false,
    val modifiedAt: Int = loadingPlaceholderNumber,
    val numTracks: Int = loadingPlaceholderNumber,
//    val numTracksDouble: Double = loadingPlaceholderNumber.toDouble(),
//    val numTracksFloat: Float = loadingPlaceholderNumber.toFloat(),
    val numAlbums: Int = loadingPlaceholderNumber,
    val numFollowers: Int = loadingPlaceholderNumber,

    val numEdits: Int = loadingPlaceholderNumber,
    val durationMs: Int = loadingPlaceholderNumber,
    val numArtists: Int = loadingPlaceholderNumber,
    val track0ArtistName: String = loadingPlaceholderString,
    val track0TrackName: String = loadingPlaceholderString,
    val track0DurationMs: Int = loadingPlaceholderNumber,
    val track0AlbumName: String = loadingPlaceholderString,
    val track1ArtistName: String = loadingPlaceholderString,
    val track1TrackName: String = loadingPlaceholderString,
    val track1DurationMs: Int = loadingPlaceholderNumber,
    val track1AlbumName: String = loadingPlaceholderString,
    val track2ArtistName: String = loadingPlaceholderString,
    val track2TrackName: String = loadingPlaceholderString,
    val track2DurationMs: Int = loadingPlaceholderNumber,
    val track2AlbumName: String = loadingPlaceholderString,
    val track3ArtistName: String = loadingPlaceholderString,
    val track3TrackName: String = loadingPlaceholderString,
    val track3DurationMs: Int = loadingPlaceholderNumber,
    val track3AlbumName: String = loadingPlaceholderString,
    val track4ArtistName: String = loadingPlaceholderString,
    val track4TrackName: String = loadingPlaceholderString,
    val track4DurationMs: Int = loadingPlaceholderNumber,
    val track4AlbumName: String = loadingPlaceholderString
) {

    fun toPlaylistModel(appState: AppState<PlaylistModel>?): PlaylistModel =
        PlaylistModel(playlist = this, appState = appState)
}

data class PlaylistDto(val resultRow: ResultRow) {

    /**
     * Helper function to map an Exposed [resultRow] into a Playlist
     * @param resultRow the return type of a query from the Exposed framework
     * @return a Playlist filled with all the needed attributes from the [resultRow]
     */
    fun toPlaylist(): Playlist = resultRow.let {
        Playlist(
            it[DatabasePlaylists.id],
            it[DatabasePlaylists.name],
            it[DatabasePlaylists.collaborative],
            it[DatabasePlaylists.modified_at],
            it[DatabasePlaylists.num_tracks],
            it[DatabasePlaylists.num_albums],
            it[DatabasePlaylists.num_followers],
//            it[DatabasePlaylists.num_tracks_double],
//            it[DatabasePlaylists.num_tracks_float],
            it[DatabasePlaylists.num_edits],
            it[DatabasePlaylists.duration_ms],
            it[DatabasePlaylists.num_artists],
            it[DatabasePlaylists.track0_artist_name],
            it[DatabasePlaylists.track0_track_name],
            it[DatabasePlaylists.track0_duration_ms],
            it[DatabasePlaylists.track0_album_name],
            it[DatabasePlaylists.track1_artist_name],
            it[DatabasePlaylists.track1_track_name],
            it[DatabasePlaylists.track1_duration_ms],
            it[DatabasePlaylists.track1_album_name],
            it[DatabasePlaylists.track2_artist_name],
            it[DatabasePlaylists.track2_track_name],
            it[DatabasePlaylists.track2_duration_ms],
            it[DatabasePlaylists.track2_album_name],
            it[DatabasePlaylists.track3_artist_name],
            it[DatabasePlaylists.track3_track_name],
            it[DatabasePlaylists.track3_duration_ms],
            it[DatabasePlaylists.track3_album_name],
            it[DatabasePlaylists.track4_artist_name],
            it[DatabasePlaylists.track4_track_name],
            it[DatabasePlaylists.track4_duration_ms],
            it[DatabasePlaylists.track4_album_name]
        )
    }

}
