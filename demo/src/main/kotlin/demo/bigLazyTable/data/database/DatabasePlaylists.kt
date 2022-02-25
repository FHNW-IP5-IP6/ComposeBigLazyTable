package demo.bigLazyTable.data.database

import org.jetbrains.exposed.sql.Table

/**
 * @author Marco Sprenger, Livio Näf
 */
object DatabasePlaylists : Table() {
    // The filtered ones - have all different types we have in our Table [long, int, bool, string]
    val id                  = long("id")
    val name                = varchar("name", length = 100)
    val modified_at         = varchar("modified_at", length = 20)
    val track0_artist_name  = varchar("track0_artist_name", length = 100)
    val collaborative       = bool("collaborative")
    val num_tracks          = integer("num_tracks")
    val num_tracks_double   = double("num_albums")
    val num_tracks_float    = float("num_followers")

//    val num_albums          = integer("num_albums")
//    val num_followers       = integer("num_followers")
    val num_edits           = integer("num_edits")
    val duration_ms         = integer("duration_ms")
    val num_artists         = integer("num_artists")
    val track0_track_name   = varchar("track0_track_name", length = 100)
    val track0_duration_ms  = integer("track0_duration_ms")
    val track0_album_name   = varchar("track0_album_name", length = 100)
    val track1_artist_name  = varchar("track1_artist_name", length = 100)
    val track1_track_name   = varchar("track1_track_name", length = 100)
    val track1_duration_ms  = integer("track1_duration_ms")
    val track1_album_name   = varchar("track1_album_name", length = 100)
    val track2_artist_name  = varchar("track2_artist_name", length = 100)
    val track2_track_name   = varchar("track2_track_name", length = 100)
    val track2_duration_ms  = integer("track2_duration_ms")
    val track2_album_name   = varchar("track2_album_name", length = 100)
    val track3_artist_name  = varchar("track3_artist_name", length = 100)
    val track3_track_name   = varchar("track3_track_name", length = 100)
    val track3_duration_ms  = integer("track3_duration_ms")
    val track3_album_name   = varchar("track3_album_name", length = 100)
    val track4_artist_name  = varchar("track4_artist_name", length = 100)
    val track4_track_name   = varchar("track4_track_name", length = 100)
    val track4_duration_ms  = integer("track4_duration_ms")
    val track4_album_name   = varchar("track4_album_name", length = 100)

    override val primaryKey = PrimaryKey(id, name = "PK_DatabasePlaylist_ID")
}