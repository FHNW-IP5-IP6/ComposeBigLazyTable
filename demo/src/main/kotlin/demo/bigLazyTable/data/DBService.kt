package demo.bigLazyTable.data

import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
class DBService : IPagingService<Playlist> {

    override fun getPage(start: Int, pageSize: Int, filter: String): List<Playlist> {
        val startIndex: Long = if (filter == "") start.toLong() else 0
        return transaction {
            DatabasePlaylists
                .select { DatabasePlaylists.name like "%${filter}%" }
                .limit(pageSize, startIndex)
                .map { mapResultRowToPlaylist(it) }
        }
    }

    private fun mapResultRowToPlaylist(resultRow: ResultRow) = resultRow.let {
        Playlist(
            it[DatabasePlaylists.name],
            it[DatabasePlaylists.collaborative],
            it[DatabasePlaylists.modified_at],
            it[DatabasePlaylists.id]
        )
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
        TODO()
    }
}