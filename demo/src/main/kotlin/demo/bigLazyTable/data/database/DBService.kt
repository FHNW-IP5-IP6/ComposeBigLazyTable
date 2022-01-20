package demo.bigLazyTable.data.database

import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementType
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * @author Marco Sprenger, Livio Näf
 */
object DBService : IPagingService<Playlist> {

    private val maxItems by lazy { getTotalCount() }

    override suspend fun getPage(
        startIndex: Int,
        pageSize: Int,
        filter: String,
        caseSensitive: Boolean
    ): List<Playlist> {
        // TODO: check if lazy is working correctly without any downside
        if (startIndex > maxItems) throw IllegalArgumentException("startIndex must be smaller than the totalCount")

        val start: Long = if (filter == "") startIndex.toLong() else 0
        return transaction {
            DatabasePlaylists
                .select { caseSensitiveSelect(caseSensitive, DatabasePlaylists.name, filter) }
                .limit(pageSize, start)
                .map { mapResultRowToPlaylist(it) }
        }
    }

    // TODO: Find out how to check filter with caseSensitive
    // By default, the SQLite LIKE operator is case-insensitive for ASCII characters (which covers all english language
    // letters), and case-sensitive for unicode characters that are beyond the ASCII range (ä, ö, ü, ...)
    // PostgreSQL is a case-sensitive database by default
    // Text comparison in MySQL is case insensitive by default, while in H2 it is case sensitive
    // From MariaDB docs, it depends on OS. For Windows, it's not case-sensitive.
    private fun SqlExpressionBuilder.caseSensitiveSelect(
        caseSensitive: Boolean,
        columnWhichShouldMatch: Column<String>,
        filter: String
    ): Op<Boolean> {
        // TODO: https://www.sqlitetutorial.net/sqlite-like/#:~:text=Note%20that%20SQLite%20LIKE%20operator,LIKE%20%22%C3%A4%22%20is%20false.
        // TODO: How to PRAGMA case_sensitive_like = true;
        return if (caseSensitive) columnWhichShouldMatch like "%$filter%"
        // TODO: match & like != caseSensitive
        else columnWhichShouldMatch.lowerCase() like "%${filter.lowercase(Locale.getDefault())}%"
    }

    // TODO: Filter works with ignoreCase=true, but what if the user wants to filter caseSensitive?
    override fun getFilteredCount(filter: String, caseSensitive: Boolean): Int = transaction {
        // doesnt work
//        exec("PRAGMA case_sensitive_like = true", explicitStatementType = StatementType.EXEC)
        DatabasePlaylists
            .select { caseSensitiveSelect(caseSensitive, DatabasePlaylists.name, filter) }
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
        // TODO: How can we determine what the gui index is of a given index?
        TODO("Implement indexOf function in DBService")
    }

    /**
     * Helper function to map an Exposed [resultRow] into a Playlist
     * @param resultRow the return type of a query from the Exposed framework
     * @return a Playlist filled with all the needed attributes from the [resultRow]
     */
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