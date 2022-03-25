package demo.bigLazyTable.data.service

import bigLazyTable.data.paging.Filter
import bigLazyTable.data.paging.IPagingService
import bigLazyTable.data.paging.Sort
import bigLazyTable.data.paging.selectWithAllFilters
import demo.bigLazyTable.data.database.DatabasePlaylists
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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
        filters: List<Filter>,
        sort: Sort?
    ): List<Playlist> {
        if (startIndex > lastIndex) throw IllegalArgumentException("startIndex must be smaller than/equal to the lastIndex and not $startIndex")
        if (startIndex < 0) throw IllegalArgumentException("only positive values are allowed for startIndex")

        val start: Long = startIndex.toLong()
        if (sort == null) {
            return transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .limit(n = pageSize, offset = start)
                    .map { PlaylistDto(it).toPlaylist() }
            }
        } else {
            return transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .orderBy(sort.dbField as Column<String> to sort.sortOrder) // TODO-Future: Remove Column<String> cast
                    .limit(n = pageSize, offset = start)
                    .map { PlaylistDto(it).toPlaylist() }
            }
        }
    }

    override fun getTotalCount(): Int = transaction {
        DatabasePlaylists
            .selectAll()
            .count()
            .toInt()
    }

    override fun getFilteredCount(filters: List<Filter>): Int {
        if (filters.isEmpty()) throw IllegalArgumentException("A Filter must be set - Passed an empty filter list to getFilteredCountNew")

        return transaction {
            DatabasePlaylists
                .selectWithAllFilters(filters)
                .count()
                .toInt()
        }
    }

    override fun get(id: Long): Playlist = transaction {
        DatabasePlaylists
            .select { DatabasePlaylists.id eq id }
            .single()
            .let { PlaylistDto(it).toPlaylist() }
    }

    override fun indexOf(id: Long, filters: List<Filter>): Int {
        if (id < 0) throw IllegalArgumentException("only positive id as parameter is allowed")
        transaction {
            // TODO-Future: How can we determine what the gui index is of a given index?
        }
        return -1
    }
}