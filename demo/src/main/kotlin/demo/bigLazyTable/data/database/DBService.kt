package demo.bigLazyTable.data.database

import bigLazyTable.paging.*
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.model.PlaylistDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
            println("Inside getPage transaction without sort")
            val start1 = System.currentTimeMillis()
            val rv = transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .limit(n = pageSize, offset = start)
                    .map {
                        println("Inside map of getPage without sort")
                        val start2 = System.currentTimeMillis()
                        val rv = PlaylistDto(it).toPlaylist()
                        val end = System.currentTimeMillis()
                        println("map of getPage without sort needed ${end - start2} ms")
                        rv
                    }
            }
            val end = System.currentTimeMillis()
            println("getPage transaction without sort needed ${end - start1} ms")
            return rv
        } else {
            println("Inside getPage transaction with sort")
            val start1 = System.currentTimeMillis()
            val rv = transaction {
                DatabasePlaylists
                    .selectWithAllFilters(filters)
                    .orderBy(sort.dbField as Column<String> to sort.sortOrder)
                    .limit(n = pageSize, offset = start)
                    .map {
                        println("Inside map of getPage with sort")
                        val start2 = System.currentTimeMillis()
                        val rv = PlaylistDto(it).toPlaylist()
                        val end = System.currentTimeMillis()
                        println("map of getPage with sort needed ${end - start2} ms")
                        rv
                    }
            }

            val end = System.currentTimeMillis()
            println("getPage transaction without sort needed ${end - start1} ms")
            return rv
        }
    }

    fun Column<String>.toType() {
        val x = this as? Column<Int> ?: this as? Column<Double>
    }

    // https://discuss.kotlinlang.org/t/checking-type-in-generic/3100/2
    inline fun <reified T> test(): String {
        println("Reified test()")
        return when (T::class) {
            Int::class -> "Int"
            String::class -> "String"
            else -> "${T::class}"
        }
    }

    private fun Table.selectWithAllFilters(filters: List<Filter>): Query {
        println("Inside selectWithAllFilters")
        val start = System.currentTimeMillis()
        if (filters.isEmpty()) return Query(this, null)

        // TODO: Could this be removed due to the below for loop?
        if (filters.size == 1) {
            val filter = filters.first()
            return selectWithFilter(filter)
        }

        // TODO: Better way than this ugly code?
        var sql: Op<Boolean> = when (val firstFilter = filters.first()) {
            is LongFilter -> firstFilter.dbField as Column<String> eq firstFilter.filter.toString()
            is IntFilter -> firstFilter.dbField as Column<String> eq firstFilter.filter.toString()
            is DoubleFilter -> firstFilter.dbField as Column<String> eq firstFilter.filter.toString()
            is FloatFilter -> firstFilter.dbField as Column<String> eq firstFilter.filter.toString()
            is BooleanFilter -> firstFilter.dbField as Column<String> eq firstFilter.filter.toString()
            is StringFilter -> caseSensitiveFilter(firstFilter.caseSensitive, firstFilter.dbField, firstFilter.filter)
        }
//        var sql: Op<Boolean> = firstFilter.dbField like "%${firstFilter.filter}%"
        for (i in 1 until filters.size) {
            // TODO: as Column<Double/Float/Int/...> equals filter.toDouble()/usw
            val sql2: Op<Boolean> = when (val filter = filters[i]) {
                is LongFilter -> filter.dbField as Column<String> eq filter.filter.toString()
                is IntFilter -> filter.dbField as Column<String> eq filter.filter.toString()
                is DoubleFilter -> filter.dbField as Column<String> eq filter.filter.toString()
                is FloatFilter -> filter.dbField as Column<String> eq filter.filter.toString()
                is BooleanFilter -> filter.dbField as Column<String> eq filter.filter.toString()
                is StringFilter -> caseSensitiveFilter(filter.caseSensitive, filter.dbField, filter.filter)
            }
            sql = sql and sql2
        }
        val end = System.currentTimeMillis()
        println("Just before return: selectWithAllFilters needed ${end - start} ms")
        return this.select { sql }
    }

    private fun Table.selectWithFilter(filter: Filter): Query {
        println("Inside selectWithFilter")
        val start = System.currentTimeMillis()

        // TODO: Better way than this ugly code?
        val rv: Query
        when (filter) {
            is LongFilter    -> {
                rv = select { filter.dbField as Column<String> eq filter.filter.toString() } // eq works only for id
            }
            is IntFilter     -> {
                rv = select { filter.dbField as Column<String> eq filter.filter.toString() }
            }
            is DoubleFilter  -> {
                // TODO: CHeck with Double values (like, eq, match)
                rv = select { filter.dbField as Column<String> like "${filter.filter}%" }
            }
            is FloatFilter   -> {
                // TODO: Check with Float values (like, eq, match)
                rv = select { filter.dbField as Column<String> like "${filter.filter}%" }
            }
            is BooleanFilter -> {
                rv = select { filter.dbField as Column<String> eq filter.filter.toString() }
            }
            is StringFilter  -> {
                rv = selectWithStringFilter(
                    caseSensitive = filter.caseSensitive,
                    dbField = filter.dbField,
                    filter = filter.filter
                )
            }
        }
        //if (filter.dbField == null) return Query(this, null) // TODO: Can this ever happen?

        val end = System.currentTimeMillis()
        println("selectWithFilter needed ${end - start} ms")
        return rv
    }

    private fun Table.selectWithStringFilter(caseSensitive: Boolean, dbField: Column<String>?, filter: String): Query {
        if (dbField == null) return Query(this, null)
        return this.select { caseSensitiveFilter(caseSensitive, dbField, filter) }
    }

    // TODO: Move this knowledge into documentation
// By default, the SQLite LIKE operator is case-insensitive for ASCII characters (which covers all english language
// letters), and case-sensitive for unicode characters that are beyond the ASCII range (ä, ö, ü, ...)
// PostgreSQL is a case-sensitive database by default
// Text comparison in MySQL is case insensitive by default, while in H2 it is case sensitive
// From MariaDB docs, it depends on OS. For Windows, it's not case-sensitive.
    private fun caseSensitiveFilter(
        caseSensitive: Boolean,
        columnWhichShouldMatch: Column<String>,
        filter: String
    ): Op<Boolean> {
        println("Inside caseSensitiveFilter")

        val start = System.currentTimeMillis()
        val rv = if (caseSensitive) columnWhichShouldMatch like "%$filter%"
        else columnWhichShouldMatch.lowerCase() like "%${filter.lowercase(Locale.getDefault())}%"

        val end = System.currentTimeMillis()
        println("caseSensitiveFilter needed ${end - start} ms")
        return rv
    }

    override fun getTotalCount(): Int = transaction {
        println("getTotalCount is called")
        DatabasePlaylists.selectAll().count().toInt()
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

    override fun indexOf(id: Long, filter: String): Int {
        if (id < 0) throw IllegalArgumentException("only positive id as parameter is allowed")
        transaction {
            // TODO: How can we determine what the gui index is of a given index?
        }
        return -1
    }
}