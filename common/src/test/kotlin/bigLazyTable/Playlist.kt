package bigLazyTable

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
)