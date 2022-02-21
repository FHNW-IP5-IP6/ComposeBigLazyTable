package demo.bigLazyTable.data.database

import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * @author Marco Sprenger, Livio Näf
 */
object DBService : IPagingService<Playlist> {

    private val lastIndex by lazy {
        val totalCount = getTotalCount() - 1
        println("DBService: lastIndex called with totalCount = $totalCount")
        totalCount
    }

//    private val filteredCount by lazy {
//        val filtered = getFilteredCount("elorette")
//        println("DBService: filteredCount called with filteredCount = $filtered")
//        filtered
//    }
//    var count = 0

    override fun getPage(
        startIndex: Int,
        pageSize: Int,
        filter: String,
        caseSensitive: Boolean,
        sorted: String
    ): List<Playlist> {
        // TODO: check if lazy is working correctly without any downside
        if (startIndex > lastIndex) throw IllegalArgumentException("startIndex must be smaller than/equal to the lastIndex and not $startIndex")
        if (startIndex < 0) throw IllegalArgumentException("only positive values are allowed for startIndex")

//        println("filteredCount is $filteredCount")
//        println("start: count is $count")
        val start: Long = /*if (filter == "") */startIndex.toLong()
        println("Offset: Start = $start")
//        } else if (filteredCount >= pageSize) {
//            count += pageSize
//            println("filteredCount >= pageSize: count is $count")
//            count.toLong()
//        } else {
//            count += filteredCount
//            println("filteredCount < pageSize: count is $count")
//            count.toLong()
//        }
        if (sorted != "ASC" && sorted != "DESC") {
            return transaction {
                DatabasePlaylists
                    .select { caseSensitiveFilter(caseSensitive, DatabasePlaylists.name, filter) }
                    .limit(n = pageSize, offset = start)
                    .map { mapResultRowToPlaylist(it) }
            }
        } else {
            val sortOrder = if (sorted == "ASC") SortOrder.ASC else SortOrder.DESC
            return transaction {
                DatabasePlaylists
                    .select { caseSensitiveFilter(caseSensitive, DatabasePlaylists.name, filter) }
                    .orderBy(DatabasePlaylists.name to sortOrder)
                    .limit(n = pageSize, offset = start)
                    .map { mapResultRowToPlaylist(it) }
            }
        }
    }

    // TODO: Move this knowledge into documentation
// By default, the SQLite LIKE operator is case-insensitive for ASCII characters (which covers all english language
// letters), and case-sensitive for unicode characters that are beyond the ASCII range (ä, ö, ü, ...)
// PostgreSQL is a case-sensitive database by default
// Text comparison in MySQL is case insensitive by default, while in H2 it is case sensitive
// From MariaDB docs, it depends on OS. For Windows, it's not case-sensitive.
    private fun SqlExpressionBuilder.caseSensitiveFilter(
        caseSensitive: Boolean,
        columnWhichShouldMatch: Column<String>,
        filter: String
    ): Op<Boolean> {
        return if (caseSensitive) columnWhichShouldMatch like "%$filter%"
        else columnWhichShouldMatch.lowerCase() like "%${filter.lowercase(Locale.getDefault())}%"
    }

    fun getSortedPage() {

    }

    override fun getFilteredCount(filter: String, caseSensitive: Boolean): Int = transaction {
        if (filter == "") throw IllegalArgumentException("Filter must be set - empty string is not allowed (leads to java.lang.OutOfMemoryError: Java heap space)")
        DatabasePlaylists
            .select { caseSensitiveFilter(caseSensitive, DatabasePlaylists.name, filter) }
            .count()
            .toInt()
    }

    override fun getTotalCount(): Int = transaction {
        println("getTotalCount is called")
        DatabasePlaylists.selectAll().count().toInt()
    }

    override fun get(id: Long): Playlist = transaction {
        DatabasePlaylists
            .select { DatabasePlaylists.id eq id }
            .single()
            .let { mapResultRowToPlaylist(it) }
    }

    override fun indexOf(id: Long, filter: String): Int {
        if (id < 0) throw IllegalArgumentException("only positive id as parameter is allowed")
        transaction {
            // TODO: How can we determine what the gui index is of a given index?
        }
        return -1
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