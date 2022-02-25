package demo.bigLazyTable.data.database

import bigLazyTable.paging.Filter
import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
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

    override fun getPage(
        startIndex: Int,
        pageSize: Int,
        filter: String,
        dbField: Column<*>?,
        caseSensitive: Boolean,
        sorted: String
    ): List<Playlist> {
        // TODO: check if lazy is working correctly without any downside
        if (startIndex > lastIndex) throw IllegalArgumentException("startIndex must be smaller than/equal to the lastIndex and not $startIndex")
        if (startIndex < 0) throw IllegalArgumentException("only positive values are allowed for startIndex")

        val start: Long = startIndex.toLong()
        println("Offset: Start = $start")
        if (sorted != "ASC" && sorted != "DESC") {
            return transaction {
                DatabasePlaylists
//                    .select { dbField?.let { caseSensitiveFilter(caseSensitive, it as Column<String>, filter) }!! }
                    .selectWithFilter(caseSensitive, dbField, filter)
                    .limit(n = pageSize, offset = start)
                    .map { mapResultRowToPlaylist(it) }
            }
        } else return emptyList() // TODO: Remove this & comment below code back in!
//        else {
//            val sortOrder = if (sorted == "ASC") SortOrder.ASC else SortOrder.DESC
//            return transaction {
//                DatabasePlaylists
//                    .select { caseSensitiveFilter(caseSensitive, DatabasePlaylists.name, filter) }
//                    .orderBy(DatabasePlaylists.name to sortOrder)
//                    .limit(n = pageSize, offset = start)
//                    .map { mapResultRowToPlaylist(it) }
//            }
//        }
    }

    override fun getPageNew(
        startIndex: Int,
        pageSize: Int,
        filters: List<Filter>?,
//        filter: String,
//        dbFields: List<Column<*>>?,
//        caseSensitive: Boolean,
        sorted: String
    ): List<Playlist> {
        // TODO: check if lazy is working correctly without any downside
        if (startIndex > lastIndex) throw IllegalArgumentException("startIndex must be smaller than/equal to the lastIndex and not $startIndex")
        if (startIndex < 0) throw IllegalArgumentException("only positive values are allowed for startIndex")

        val start: Long = startIndex.toLong()
        println("Offset: Start = $start")
        if (sorted != "ASC" && sorted != "DESC") {
            return transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .limit(n = pageSize, offset = start)
                    .map { mapResultRowToPlaylist(it) }
            }
        } else return emptyList() // TODO: Remove this & comment below code back in!
//        else {
//            val sortOrder = if (sorted == "ASC") SortOrder.ASC else SortOrder.DESC
//            return transaction {
//                DatabasePlaylists
//                    .select { caseSensitiveFilter(caseSensitive, DatabasePlaylists.name, filter) }
//                    .orderBy(DatabasePlaylists.name to sortOrder)
//                    .limit(n = pageSize, offset = start)
//                    .map { mapResultRowToPlaylist(it) }
//            }
//        }
    }

    private fun Table.selectWithAllFilters(filters: List<Filter>?): Query {
        if (filters == null || filters.isEmpty()) return Query(this, null)

        if (filters.size == 1) {
            val filter = filters.first()
            return this.select {
                caseSensitiveFilter(
                    filter.caseSensitive,
                    filter.dbField as Column<String>,
                    filter.filter
                )
            }
        }

        val firstFilter = filters.first()
        var sql: Op<Boolean> = firstFilter.dbField as Column<String> like "%${firstFilter.filter}%"
        for (filter in filters) {
            // TODO: as Column<Double/Float/Int/...> equals filter.toDouble()/usw
//            when (dbField)
            sql = sql and (filter.dbField as Column<String> like "%${filter.filter}%")
        }
        return this.select { sql }
    }

    private fun Table.selectWithFilter(filter: Filter): Query {
        if (filter.dbField == null) return Query(this, null)
        return this.select {
            caseSensitiveFilter(
                filter.caseSensitive,
                filter.dbField as Column<String>,
                filter.filter
            )
        }
    }

    private fun Table.selectWithFilter(caseSensitive: Boolean, dbField: Column<*>?, filter: String): Query {
        if (dbField == null) return Query(this, null)
        return this.select { caseSensitiveFilter(caseSensitive, dbField as Column<String>, filter) }
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

    override fun getFilteredCount(filter: String, dbField: Column<*>?, caseSensitive: Boolean): Int {
        if (filter == "") throw IllegalArgumentException("Filter must be set - empty string is not allowed (leads to java.lang.OutOfMemoryError: Java heap space)")
        if (dbField == null) return 0

        return transaction {
            DatabasePlaylists
                .select { caseSensitiveFilter(caseSensitive, dbField as Column<String>, filter) }
                .count()
                .toInt()
        }
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
    // TODO: Playlist in data layer
    private fun mapResultRowToPlaylist(resultRow: ResultRow): Playlist = resultRow.let {
        Playlist(
            it[DatabasePlaylists.id],
            it[DatabasePlaylists.name],
            it[DatabasePlaylists.collaborative],
            it[DatabasePlaylists.modified_at],
            it[DatabasePlaylists.num_tracks],
            it[DatabasePlaylists.num_tracks_double],
            it[DatabasePlaylists.num_tracks_float],
//            it[DatabasePlaylists.num_albums],
//            it[DatabasePlaylists.num_followers],
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