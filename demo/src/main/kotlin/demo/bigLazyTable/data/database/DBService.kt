package demo.bigLazyTable.data.database

import bigLazyTable.paging.*
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.model.PlaylistDto
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
        filters: List<Filter>,
        sort: Sort?
    ): List<Playlist> {
        // TODO: check if lazy is working correctly without any downside
        if (startIndex > lastIndex) throw IllegalArgumentException("startIndex must be smaller than/equal to the lastIndex and not $startIndex")
        if (startIndex < 0) throw IllegalArgumentException("only positive values are allowed for startIndex")

        val start: Long = startIndex.toLong()
        println("Offset: Start = $start")
        if (sort == null) {
            return transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .limit(n = pageSize, offset = start)
                    .map { PlaylistDto(it).toPlaylist() }
            }
        }
        else {
            return transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .orderBy(sort.dbField as Column<String> to sort.sortOrder)
                    .limit(n = pageSize, offset = start)
                    .map { PlaylistDto(it).toPlaylist() }
            }
        }
    }

    fun Column<String>.toType() {
        val x = this as? Column<Int> ?: this as? Column<Double>
    }

    // https://discuss.kotlinlang.org/t/checking-type-in-generic/3100/2
    inline fun <reified T> Column<T>.test() {
        when (T::class) {
            Int::class -> {
                println("Int")
                this as Column<Int>
            }
            else -> println("${T::class}")
        }
    }

    private fun Table.selectWithAllFilters(filters: List<Filter>): Query {
        if (filters.isEmpty()) return Query(this, null)

        // TODO: Could this be removed due to the below for loop?
        if (filters.size == 1) {
            val filter = filters.first()
            return selectWithFilter(filter)
        }

        val firstFilter = filters.first()
//        when (firstFilter.dbField) {
//            is Column<Int> -> {}
//            is String -> {}
//        }


        var sql: Op<Boolean> = firstFilter.dbField as Column<String> like "%${firstFilter.filter}%"
        for (i in 1 until filters.size) {
            // TODO: as Column<Double/Float/Int/...> equals filter.toDouble()/usw
//            when (dbField)
            val filter = filters[i]
            sql = sql and (filter.dbField as Column<String> like "%${filter.filter}%")
        }
        return this.select { sql }
    }

    private fun Table.selectWithFilter(filter: Filter): Query {
        if (filter.dbField == null) return Query(this, null)

        return selectWithFilter(
            caseSensitive = filter.caseSensitive,
            dbField = filter.dbField,
            filter = filter.filter
        )
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

    override fun getTotalCount(): Int = transaction {
        println("getTotalCount is called")
        DatabasePlaylists.selectAll().count().toInt()
    }

    override fun get(id: Long): Playlist = transaction {
        DatabasePlaylists
            .select { DatabasePlaylists.id eq id }
            .single()
            .let { PlaylistDto(it).toPlaylist() }
    }

    override fun indexOf(id: Long, filter: String): Int {
        if (id < 0) throw IllegalArgumentException("only positive id as parameter is allowed")
        transaction {
            // TODO: How can we determine what the gui index is of a given index?
        }
        return -1
    }

    override fun getFilteredCount(filters: List<Filter>): Int {
        if (filters.isEmpty()) throw IllegalArgumentException("A Filter must be set - Passed an empty filter list to getFilteredCountNew")

        return transaction {
            DatabasePlaylists
                .selectWithAllFilters(filters = filters)
                .count()
                .toInt()
        }
    }
}