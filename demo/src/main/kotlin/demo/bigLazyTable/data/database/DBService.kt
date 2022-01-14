package demo.bigLazyTable.data.database

import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Marco Sprenger, Livio Näf
 */
object DBService : IPagingService<Playlist> {

    // TODO: Validate startIndex -> should not be bigger than what getTotalCount() returns
    override suspend fun getPage(startIndex: Int, pageSize: Int, filter: String): List<Playlist> {
        val start: Long = if (filter == "") startIndex.toLong() else 0
        return transaction {
            DatabasePlaylists
                .select { DatabasePlaylists.name like "%${filter}%" }
                .limit(pageSize, start)
                .map { mapResultRowToPlaylist(it) }
        }
    }

    override fun getFilteredCount(filter: String): Int = transaction {
        DatabasePlaylists
            .select { DatabasePlaylists.name like "%${filter}%" }
            .count()
            .toInt()
    }

    override fun getTotalCount(): Int = transaction {
        DatabasePlaylists.selectAll().count().toInt()
    }

    override fun get(id: Long): Playlist = transaction {
        DatabasePlaylists
            .select { DatabasePlaylists.id eq id }
            .single()
            .let { mapResultRowToPlaylist(it) }
    }

    override fun indexOf(id: Long, filter: String): Int = transaction {
        TODO("Implement indexOf function in DBService")
    }

    // Helper functions
    private fun mapResultRowToPlaylist(resultRow: ResultRow): Playlist = resultRow.let {
        Playlist(
            it[DatabasePlaylists.id],
            it[DatabasePlaylists.name],
            it[DatabasePlaylists.collaborative],
            it[DatabasePlaylists.modified_at],
            it[DatabasePlaylists.num_tracks],
            it[DatabasePlaylists.num_albums],
            it[DatabasePlaylists.num_followers],
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