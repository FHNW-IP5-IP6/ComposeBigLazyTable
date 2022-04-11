package demo.bigLazyTable.newDemo.data.service

import bigLazyTable.data.paging.Filter
import bigLazyTable.data.paging.IPagingService
import bigLazyTable.data.paging.Sort
import bigLazyTable.data.paging.selectWithAllFilters
import demo.bigLazyTable.newDemo.data.database.SuperstoreDatabase
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object SuperstoreDBService: IPagingService<Superstore> {

    private val lastIndex by lazy { getTotalCount() - 1 }

    override fun getPage(
        startIndex: Int,
        pageSize: Int,
        filters: List<Filter>,
        sort: Sort?
    ): List<Superstore> {
        if (startIndex > lastIndex) throw IllegalArgumentException("startIndex must be smaller than/equal to the lastIndex and not $startIndex")
        if (startIndex < 0) throw IllegalArgumentException("only positive values are allowed for startIndex")

        val start: Long = startIndex.toLong()
        if (sort == null) {
            return transaction {
                SuperstoreDatabase
                    .selectWithAllFilters(filters)
                    .limit(n = pageSize, offset = start)
                    .map { SuperstoreDto(it).toSuperstore() }
            }
        } else {
            return transaction {
                SuperstoreDatabase
                    .selectWithAllFilters(filters)
                    .orderBy(sort.dbField as Column<String> to sort.sortOrder)
                    .limit(n = pageSize, offset = start)
                    .map { SuperstoreDto(it).toSuperstore() }
            }
        }
    }

    override fun getTotalCount(): Int = transaction {
        SuperstoreDatabase
            .selectAll()
            .count()
            .toInt()
    }

    override fun getFilteredCount(filters: List<Filter>): Int {
        if (filters.isEmpty()) throw IllegalArgumentException("A Filter must be set - Passed an empty filter list to getFilteredCountNew")

        return transaction {
            SuperstoreDatabase
                .selectWithAllFilters(filters)
                .count()
                .toInt()
        }
    }

    override fun get(id: Long): Superstore = transaction {
        SuperstoreDatabase
            .select { SuperstoreDatabase.RowID eq id.toInt() }
            .single()
            .let { SuperstoreDto(it).toSuperstore() }
    }

    override fun indexOf(id: Long, filters: List<Filter>): Int {
        if (id < 0) throw IllegalArgumentException("only positive id as parameter is allowed")
        transaction {
            // TODO-Future: How can we determine what the gui index is of a given index?
        }
        return -1
    }

}