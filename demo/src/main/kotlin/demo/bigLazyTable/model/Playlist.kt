package demo.bigLazyTable.model

/**
 * @author Marco Sprenger, Livio Näf
 */
data class Playlist(
    val id: Long = -1,
    val name: String = "...",
    val collaborative: Boolean = false,
    val modifiedAt: String = "...",
    val numTracks: Int = 0,
    val numAlbums: Int = 0,
    val numFollowers: Int = 0,
    val numEdits: Int = 0,
    val durationMs: Int = 0,
    val numArtists: Int = 0,
    val track0ArtistName: String = "...",
    val track0TrackName: String = "...",
    val track0DurationMs: Int = 0,
    val track0AlbumName: String = "...",
    val track1ArtistName: String = "...",
    val track1TrackName: String = "...",
    val track1DurationMs: Int = 0,
    val track1AlbumName: String = "...",
    val track2ArtistName: String = "...",
    val track2TrackName: String = "...",
    val track2DurationMs: Int = 0,
    val track2AlbumName: String = "...",
    val track3ArtistName: String = "...",
    val track3TrackName: String = "...",
    val track3DurationMs: Int = 0,
    val track3AlbumName: String = "...",
    val track4ArtistName: String = "...",
    val track4TrackName: String = "...",
    val track4DurationMs: Int = 0,
    val track4AlbumName: String = "..."
)